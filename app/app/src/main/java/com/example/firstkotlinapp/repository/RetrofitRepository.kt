package com.example.firstkotlinapp.repository

import android.annotation.SuppressLint
import android.util.Log
import com.example.firstkotlinapp.dataClass.MarkerDataVO
import com.example.firstkotlinapp.retrofit.OkHttpManager
import io.reactivex.Single
import io.reactivex.schedulers.Schedulers
import okhttp3.MultipartBody
import okhttp3.ResponseBody
import java.lang.Exception

class RetrofitRepository() {
    private val TAG = "RetrofitRepository"
    private val retrofit = OkHttpManager.KotlinOKHttpRetrofitRxJavaManager.getInstance()

    //////////////////////////////////////////////////////////// Marker CRUD
    fun putDataWithImage(
        seq: Int,
        subject: String,
        snippet: String,
        lat: Double,
        lng: Double,
        writer: String,
        address: String,
        file: MutableList<MultipartBody.Part>?
    ): Single<MarkerDataVO> {
        return retrofit.putDataWithImage(seq, subject, snippet, lat, lng, writer, address, file)
            .subscribeOn(Schedulers.io())
            .observeOn(Schedulers.io())
    }

    @SuppressLint("CheckResult")
    fun getDataList(): Single<List<MarkerDataVO>> {
        return retrofit.getDataList()
            .subscribeOn(Schedulers.io())
            .observeOn(Schedulers.io())
    }

    @SuppressLint("CheckResult")
    fun updateData(seq : Int, subject: String, content: String) {
        retrofit.updateData(seq, subject, content)
            .subscribeOn(Schedulers.io())
            .observeOn(Schedulers.io())
            .subscribe({
                Log.d(TAG, "데이터 업데이트 성공")
                Log.d(TAG, "updateData함 : ${it}")
            }, {
                Log.d(TAG, "데이터 업데이트 실패")
                Log.d(TAG, it.toString())
            })
    }

    @SuppressLint("CheckResult")
    fun deleteData(seq : Int) {
        retrofit.deleteData(seq)
            .subscribeOn(Schedulers.io())
            .observeOn(Schedulers.io())
            .subscribe({
                Log.d(TAG, "데이터 삭제 성공")
            }, {
                Log.d(TAG, "데이터 삭제 실패")
            })
    }
    //////////////////////////////////////////////////// end Of Marker CRUD

    @SuppressLint("CheckResult")
    fun getMarkerImage(seq: Int): Single<ResponseBody> {
        return retrofit.getImage(seq)
            .subscribeOn(Schedulers.io())
            .observeOn(Schedulers.io())
    }

    suspend fun getSearchMarkerData(searchString : String): List<MarkerDataVO>? {
        try {
            val result = retrofit.getDataWithSearch(searchString)
            return if(result.isSuccessful){
                result.body()
            }else{
                emptyList()
            }
        } catch(error : Exception) {
            Log.d(TAG, "실패 이유는 ${error}")
        }
        return emptyList()
    }

}