package com.example.firstkotlinapp.retrofit

import com.example.firstkotlinapp.dataClass.MarkerDataVO
import com.example.firstkotlinapp.dataClass.MarkerImageVO
import io.reactivex.Single
import okhttp3.MultipartBody
import okhttp3.ResponseBody
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
    ) : Single<MarkerDataVO>

    @FormUrlEncoded
    @POST("EarthCommunity/getMarker.do")
    fun getData(
        @Field("seq") seq: Int
    ) : Single<MarkerDataVO>

    @FormUrlEncoded
    @POST("EarthCommunity/getMarkerWithSearch.do")
     suspend fun getDataWithSearch(
        @Field("searchString") searchString: String
    ) : retrofit2.Response<List<MarkerDataVO>>


    @POST("EarthCommunity/markerImg/{file_address}")
    fun getImage(
        @Path("file_address") file_address: String
    ) : Single<ResponseBody>

    @FormUrlEncoded
    @POST("EarthCommunity/getImageAddress")
    fun getImageAddress(
        @Field("seq") seq: Int
    ) : Single<List<MarkerImageVO>>


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