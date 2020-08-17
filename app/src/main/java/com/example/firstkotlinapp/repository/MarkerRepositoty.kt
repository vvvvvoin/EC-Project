package com.example.firstkotlinapp.repository

import com.example.firstkotlinapp.dataBase.MarkerDAO
import com.example.firstkotlinapp.dataClass.MarkerDataVO
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class MarkerRepositoty private constructor(private val markerDAO: MarkerDAO) {

    companion object {
        @Volatile
        private var instance: MarkerRepositoty? = null

        fun getInstance(markerDAO: MarkerDAO) =
            instance ?: synchronized(this) {
                instance ?: MarkerRepositoty(markerDAO).also { instance = it }
            }
    }

    suspend fun getAll() {
        withContext(Dispatchers.IO) {
            return@withContext markerDAO.getAll()
        }
    }


}