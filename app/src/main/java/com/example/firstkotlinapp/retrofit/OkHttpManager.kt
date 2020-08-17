package com.example.firstkotlinapp.retrofit

import android.annotation.SuppressLint
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.util.Log
import android.widget.ImageView
import com.example.firstkotlinapp.dataBase.MarkerDAO
import com.example.firstkotlinapp.dataClass.MarkerDataVO
import com.example.firstkotlinapp.retrofit.OkHttpManager.KotlinOKHttpRetrofitRxJavaManager.TAG
import com.google.maps.android.clustering.ClusterManager
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import kotlinx.coroutines.*
import okhttp3.MultipartBody
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.converter.scalars.ScalarsConverterFactory
import retrofit2.create
import java.io.InputStreamReader
import java.util.concurrent.TimeUnit

class OkHttpManager {
    object KotlinOKHttpRetrofitRxJavaManager {
        val TAG = "OkHttpManager"
        val CONNECT_TIMEOUT: Long = 15
        val WRITE_TIMEOUT: Long = 15
        val READ_TIMEOUT: Long = 15
        val API_URL: String = ""
        var mOKHttpClient: OkHttpClient
        var mRetrofit: Retrofit
        var mKotlinRetrofitInterface: MarkerService
        init {
            val httpLoggingInterceptor = HttpLoggingInterceptor()
            httpLoggingInterceptor.level = HttpLoggingInterceptor.Level.BODY
            mOKHttpClient = OkHttpClient().newBuilder().apply {
                addInterceptor(httpLoggingInterceptor)
                connectTimeout(CONNECT_TIMEOUT, TimeUnit.SECONDS)
                writeTimeout(WRITE_TIMEOUT, TimeUnit.SECONDS)
                readTimeout(READ_TIMEOUT, TimeUnit.SECONDS)
            }.build()
            mRetrofit = Retrofit.Builder().apply {
                baseUrl(API_URL)
                client(mOKHttpClient)
                addConverterFactory(ScalarsConverterFactory.create())
                addConverterFactory(GsonConverterFactory.create())
                addCallAdapterFactory(RxJava2CallAdapterFactory.create())
            }.build()
            mKotlinRetrofitInterface = mRetrofit.create()
        }
        fun getInstance(): MarkerService {
            return mKotlinRetrofitInterface
        }
    }

   /* @SuppressLint("CheckResult")
    fun getDataList(clusterManager: ClusterManager<MarkerDataVO>) {
        OkHttpManager.KotlinOKHttpRetrofitRxJavaManager.getInstance().getDataList()
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({
                Log.d(TAG, "성공")
                clusterManager.clearItems()
                for (marker: MarkerDataVO in it) {
                    clusterManager.addItem(marker)
                }
                clusterManager.cluster()
                Log.d(TAG, "okHttpManager에서 getDataList() 후 cluster함")
            }, {
                Log.d(TAG, "실패")
                Log.d(TAG, it.toString())
            })
    }*/

    @SuppressLint("CheckResult")
    fun getDataList(markerDAO: MarkerDAO, clusterManager: ClusterManager<MarkerDataVO>
    ) {
        Log.d(TAG, "getDataList실행")
        var data : List<MarkerDataVO>? = null
        OkHttpManager.KotlinOKHttpRetrofitRxJavaManager.getInstance().getDataList()
            .subscribeOn(Schedulers.io())
            .observeOn(Schedulers.io())
            .subscribe({
                Log.d(TAG, "데이터 불러오기 성공")
                data = it
                markerDAO.insertMarkerList(data)
                GlobalScope.launch(Dispatchers.Main) {
                    val async = async(Dispatchers.IO) {
                        clusterManager.clearItems()
                        for(data in it){
                            clusterManager.addItem(data)
                    } }
                    async.await().let {
                        clusterManager.cluster()
                    }
                }
            }, {
                Log.d(TAG, "데이터 불러오기 실패")
                Log.d(TAG, it.toString())
            })
    }

    @SuppressLint("CheckResult")
    fun getData(clusterManager: ClusterManager<MarkerDataVO>, seq : Int): MarkerDataVO?{
        var data : MarkerDataVO? = null
        OkHttpManager.KotlinOKHttpRetrofitRxJavaManager.getInstance().getData(seq)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({
                Log.d(TAG, "성공")
                data = it
            }, {
                Log.d(TAG, "실패")
            })
        return  data
    }

    @SuppressLint("CheckResult")
    fun getImage(seq: Int, popupViewImageview: ImageView){
        var bitmap :Bitmap? = null
        OkHttpManager.KotlinOKHttpRetrofitRxJavaManager.getInstance().getImage(seq)
            .subscribeOn(Schedulers.io())
            .observeOn(Schedulers.io())
            .subscribe({
                Log.d(TAG, "이미지 불러오기 성공")
                CoroutineScope(Dispatchers.Main).launch {
                    async(Dispatchers.IO) {
                        val inputStream = it.byteStream()
                        Log.d(TAG, " BitmapFactory.decodeStream(inputStream)")
                        bitmap= BitmapFactory.decodeStream(inputStream)
                    }.await().let {
                        popupViewImageview.setImageBitmap(bitmap)
                        Log.d(TAG, " popupViewImageview.setImageBitmap(it)")
                    }
                }
            }, {
                Log.d(TAG, "이미지 불러오기 실패")
            })
    }

    @SuppressLint("CheckResult")
    fun updateData(seq : Int, subject: String, content: String) {
        OkHttpManager.KotlinOKHttpRetrofitRxJavaManager.getInstance().updateData(seq, subject, content)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({
                Log.d(TAG, "데이터 업데이트 성공")
                Log.d(TAG, "updateData함 : ${it}")
            }, {
                Log.d(TAG, "데이터 업데이트 실패")
                Log.d(TAG, it.toString())
            })
    }

//    @SuppressLint("CheckResult")
//    fun putData (
//        subject: String,
//        content: String,
//        lat: Double,
//        lng: Double,
//        writer: String,
//        address: String
//    ): List<MarkerDataVO>? {
//        var returnData : List<MarkerDataVO>? = null
//        OkHttpManager.KotlinOKHttpRetrofitRxJavaManager.getInstance()
//            .setData(subject, content, lat, lng, writer, address)
//            .subscribeOn(Schedulers.io())
//            .observeOn(Schedulers.io())
//            .subscribe({
//                Log.d(TAG, "성공")
//                returnData =  it
//            }, {
//                Log.d(TAG, "실패")
//                 it.toString()
//            })
//        return returnData
//    }
//    @SuppressLint("CheckResult")
//    fun putData (
//    subject: String,
//    snippet: String,
//    lat: Double,
//    lng: Double,
//    writer: String,
//    address: String
//): List<MarkerDataVO>? {
//        var returnData : List<MarkerDataVO>? = null
//        OkHttpManager.KotlinOKHttpRetrofitRxJavaManager.getInstance()
//            .putDataWithImage(subject, snippet, lat, lng, writer, address, null)
//            .subscribeOn(Schedulers.io())
//            .observeOn(Schedulers.io())
//            .subscribe({
//                Log.d(TAG, "성공")
//                //returnData =  it
//            }, {
//                Log.d(TAG, "실패")
//                it.toString()
//            })
//        return returnData
//    }


    @SuppressLint("CheckResult")
    fun uploadImage(file : MultipartBody.Part) {
        KotlinOKHttpRetrofitRxJavaManager.getInstance().uploadImage(file)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({
                Log.d(TAG, "성공")
            }, {
                Log.d(TAG, "실패")
                Log.d(TAG, it.toString())
            })
    }

    @SuppressLint("CheckResult")
    fun putDataWithImage(
        seq: Int,
        subject: String,
        snippet: String,
        lat: Double,
        lng: Double,
        writer: String,
        address: String,
        file: MutableList<MultipartBody.Part>?
    ) {
        KotlinOKHttpRetrofitRxJavaManager.getInstance().putDataWithImage(seq, subject, snippet, lat, lng, writer, address, file)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({
                Log.d(TAG, "데이터 삽입 성공")
            }, {
                Log.d(TAG, "데이터 삽입 실패")
                Log.d(TAG, it.toString())
            })
    }

    @SuppressLint("CheckResult")
    fun deleteData(seq : Int) {
        KotlinOKHttpRetrofitRxJavaManager.getInstance().deleteData(seq)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({
                Log.d(TAG, "데이터 삭제 성공")
            }, {
                Log.d(TAG, "데이터 삭제 실패")
            })
    }






}