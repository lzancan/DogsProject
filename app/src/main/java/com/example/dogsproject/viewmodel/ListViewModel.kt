package com.example.dogsproject.viewmodel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.dogsproject.model.DogBreed

class ListViewModel: ViewModel() {

    val dogs = MutableLiveData<List<DogBreed>>()
    val downLoadError = MutableLiveData<Boolean>()
    val loading = MutableLiveData<Boolean>()

    fun refresh(){
        val dog1 = DogBreed("1", "Corgi", "15 years old", "breedGroup", "bredFor", "temperament", "")
        val dog2 = DogBreed("2", "Labrador", "10 years old", "breedGroup", "bredFor", "temperament", "")
        val dog3 = DogBreed("3", "Rotwiller", "20 years old", "breedGroup", "bredFor", "temperament", "")
        val dogsList = arrayListOf(dog1, dog2, dog3)
        dogs.value = dogsList
        downLoadError.value = false
        loading.value = false
    }
}