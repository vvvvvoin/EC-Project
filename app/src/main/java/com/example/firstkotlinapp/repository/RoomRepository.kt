package com.example.firstkotlinapp.repository

import android.app.Application
import com.example.firstkotlinapp.dataBase.AppDatabase

class RoomRepository(application: Application) {
    private val markerDAO = AppDatabase.getInstance(application)
    private val roomDAO = markerDAO.markerDAO()
}