package com.example.dogsproject.model

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query

@Dao
interface DogDao {
    @Insert
    suspend fun insertAll(vararg dogs: DogBreed): List<Long>

    @Query("SELECT * FROM dogBreed")
    suspend fun getAllDogs(): List<DogBreed>

    @Query("SELECT * FROM dogBreed WHERE uuid =:dogId")
    suspend fun getDog(dogId: Int): DogBreed

    @Query("DELETE FROM dogBreed")
    suspend fun deleteAllDogs()
}