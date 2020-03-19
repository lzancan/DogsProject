package com.example.dogsproject.viewmodel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.dogsproject.model.DogBreed

class DetailViewModel: ViewModel() {

    val dogLiveData = MutableLiveData<DogBreed>()

    fun fetch(){
        val dog = DogBreed("1", "Corgi", "15 years old", "breedGroup", "bredFor", "temperament", "")
    }
}