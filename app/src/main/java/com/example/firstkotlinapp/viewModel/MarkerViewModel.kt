package com.example.firstkotlinapp.viewModel

import android.annotation.SuppressLint
import android.app.Application
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.util.Log
import androidx.core.graphics.drawable.toBitmap
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.firstkotlinapp.R
import com.example.firstkotlinapp.dataClass.MarkerDataVO
import com.example.firstkotlinapp.repository.RetrofitRepository
import com.example.firstkotlinapp.repository.RoomRepository

class MarkerViewModel(application: Application) : AndroidViewModel(application) {
    private val TAG = "MarkerViewModel"
    private val retrofitRepository = RetrofitRepository()
    private val roomRepository = RoomRepository(application)
    private val resource = application.resources

    private val _searchLiveData = MutableLiveData<List<MarkerDataVO>>()
    val searchData: LiveData<List<MarkerDataVO>>
        get() = _searchLiveData

    private val _firstData = MutableLiveData<List<MarkerDataVO>>()
    val firstData: LiveData<List<MarkerDataVO>>
        get() = _firstData

    private val _markerImage = MutableLiveData<Bitmap>()
    val markerImage: LiveData<Bitmap>
        get() = _markerImage

    suspend fun getSearchMarkerData(searchKeyWord: String) {
        _searchLiveData.postValue(retrofitRepository.getSearchMarkerData(searchKeyWord))
    }

    @SuppressLint("CheckResult")
    fun getMarkerImage(seq : Int){
        retrofitRepository.getMarkerImage(seq).subscribe({
            _markerImage.postValue(BitmapFactory.decodeStream(it.byteStream()))
        },{
            Log.d(TAG, "getMarkerImage 오류 = ${it}")
            val bitmap = resource.getDrawable(R.drawable.ic_map_marker04, null).toBitmap()
            _markerImage.postValue(Bitmap.createBitmap(bitmap))
        })
    }

    @SuppressLint("CheckResult")
    fun getFirstLiveDataList() {
        retrofitRepository.getDataList().subscribe({
           _firstData.postValue(it)
        }, {
            _firstData.postValue(emptyList())
        })
    }

}