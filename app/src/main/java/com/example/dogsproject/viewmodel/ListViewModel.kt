package com.example.dogsproject.viewmodel

import android.app.Application
import android.widget.Toast
import androidx.lifecycle.MutableLiveData
import com.example.dogsproject.model.DogApiService
import com.example.dogsproject.model.DogBreed
import com.example.dogsproject.model.DogDatabase
import com.example.dogsproject.util.SharedPreferencesHelper
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.observers.DisposableSingleObserver
import io.reactivex.schedulers.Schedulers
import kotlinx.coroutines.launch

class ListViewModel(application: Application): BaseViewModel(application) {

    private var prefHelper = SharedPreferencesHelper(application)
    private var refreshTime = 5 * 60 * 1000 * 1000 *  100L

    private val dogsService = DogApiService()
    private val disposable = CompositeDisposable()

    val dogs = MutableLiveData<List<DogBreed>>()
    val downLoadError = MutableLiveData<Boolean>()
    val loading = MutableLiveData<Boolean>()

    fun refresh(){
        val updateTime = prefHelper.getUpdateTime()
        if(updateTime != null && updateTime != 0L && System.nanoTime() - updateTime < refreshTime){
            fetchFromDatabase()
        } else {
            fetchFromRemote()
        }
    }

    private fun fetchFromDatabase(){
        loading.value = true
        launch {
            val dogs = DogDatabase(getApplication()).dogDao().getAllDogs()
            dogsRetrieved(dogs)
            Toast.makeText(getApplication(), "Dogs retrieved from database", Toast.LENGTH_SHORT).show()
        }
    }

    fun refreshBypassCache(){
        fetchFromRemote()
    }

    private fun fetchFromRemote() {
        loading.value = true
        disposable.add(
            dogsService.getDogs()
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeWith(object : DisposableSingleObserver<List<DogBreed>>() {

                    override fun onSuccess(dogList: List<DogBreed>) {
                        storeDogsLocally(dogList)
                        Toast.makeText(getApplication(), "Dogs retrieved from remote", Toast.LENGTH_SHORT).show()
                    }

                    override fun onError(e: Throwable) {
                        downLoadError.value = true
                        loading.value = false
                        e.printStackTrace()
                    }

                })
        )
    }

    private fun dogsRetrieved(dogList: List<DogBreed>){
        dogs.value = dogList
        downLoadError.value = false
        loading.value = false
    }

    private fun storeDogsLocally(list: List<DogBreed>){
        launch {
            val dao = DogDatabase(getApplication()).dogDao()
            dao.deleteAllDogs()
            val result = dao.insertAll(*list.toTypedArray())
            var i = 0
            while(i < list.size){
                list[i].uuid = result[i].toInt()
                i++
            }
            dogsRetrieved(list)
        }
        prefHelper.saveUpdateTime(System.nanoTime())
    }

    override fun onCleared() {
        super.onCleared()
        disposable.clear()
    }
}