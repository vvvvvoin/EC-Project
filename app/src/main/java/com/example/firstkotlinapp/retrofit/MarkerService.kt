package com.example.firstkotlinapp.retrofit

import android.graphics.Bitmap
import android.media.Image
import com.example.firstkotlinapp.dataClass.BoardVO
import com.example.firstkotlinapp.dataClass.MarkerDataVO
import io.reactivex.Single
import okhttp3.MultipartBody
import okhttp3.RequestBody
import okhttp3.Response
import okhttp3.ResponseBody
import okio.Utf8
import retrofit2.Call
import retrofit2.http.*

interface MarkerService {

    @GET("EarthCommunity/markerDataTransform")
    fun getDataList( ) : Single<List<MarkerDataVO>>

    @Multipart
    @POST("EarthCommunity/imageUpload")
    fun uploadImage(
        @Part file : MultipartBody.Part
    ) : Single<String>

    @Multipart
    @POST("EarthCommunity/insertMarker.do")
    fun setData(
        @Part("subject" ) subject: String,
        @Part("content") content: String,
        @Part("lat") lat: Double,
        @Part("lng") lng: Double,
        @Part("writer") writer: String,
        @Part("address") address: String
    ) : Single<List<MarkerDataVO>>


    @Multipart
    @POST("EarthCommunity/insertMarker.do")
    fun putDataWithImage(
        @Part("seq" ) seq: Int,
        @Part("subject" ) subject: String,
        @Part("content") content: String,
        @Part("lat") lat: Double,
        @Part("lng") lng: Double,
        @Part("writer") writer: String,
        @Part("address") address: String,
        @Part file : List<MultipartBody.Part>?
    ) : Single<List<MarkerDataVO>>

    @FormUrlEncoded
    @POST("EarthCommunity/getMarker.do")
    fun getData(
        @Field("seq") seq: Int
    ) : Single<MarkerDataVO>

    @FormUrlEncoded
    @POST("EarthCommunity/getImage")
    fun getImage(
        @Field("seq") seq: Int
    ) : Single<ResponseBody>

    @FormUrlEncoded
    @POST("EarthCommunity/updateMarker.do")
    fun updateData(
        @Field("seq") seq: Int,
        @Field("subject") subject: String,
        @Field("content") content: String
    ) : Single<MarkerDataVO>

    @FormUrlEncoded
    @POST("EarthCommunity/deleteMarker.do")
    fun deleteData(
        @Field("seq") seq: Int
    ) : Single<List<MarkerDataVO>>

}