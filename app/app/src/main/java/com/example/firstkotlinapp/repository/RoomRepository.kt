package com.example.firstkotlinapp.repository

import android.app.Application
import androidx.lifecycle.LiveData
import com.example.firstkotlinapp.dataBase.AppDatabase
import com.example.firstkotlinapp.dataClass.MarkerDataVO
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class RoomRepository(application: Application) {
    private val TAG = "RoomRepository"
    private val markerDAO = AppDatabase.getInstance(application)
    private val roomDAO = markerDAO.markerDAO()
    private val markerList = roomDAO.getAll()
    private val markerListAsync = roomDAO.getAsyncList()

    fun getOne(seq: Int): MarkerDataVO? {
        var data: MarkerDataVO? = null
        val job = CoroutineScope(Dispatchers.IO).launch {
            data = roomDAO.getOne(seq)
        }
        return job.onJoin.let { data }
    }

    fun getList(): LiveData<List<MarkerDataVO>> {
        return markerList
    }

    fun getListAsync(): LiveData<List<MarkerDataVO>> {
        return markerListAsync
    }

    fun insert(marker: MarkerDataVO){
        CoroutineScope(Dispatchers.IO).launch {
            roomDAO.insert(marker)
        }
    }

    fun insertMarkerList(markerEntity: List<MarkerDataVO>?){
        CoroutineScope(Dispatchers.IO).launch {
            roomDAO.insertMarkerList(markerEntity)
        }
    }

    fun update(marker: MarkerDataVO){
        CoroutineScope(Dispatchers.IO).launch {
            roomDAO.updateSync(marker)
        }
    }

    fun delete(marker: MarkerDataVO){
        CoroutineScope(Dispatchers.IO).launch {
            roomDAO.delete(marker)
        }
    }

}