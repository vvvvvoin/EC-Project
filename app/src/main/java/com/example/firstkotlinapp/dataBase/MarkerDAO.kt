package com.example.firstkotlinapp.dataBase

import androidx.lifecycle.LiveData
import androidx.room.*
import androidx.room.OnConflictStrategy.REPLACE
import com.example.firstkotlinapp.dataClass.MarkerDataVO
//import com.example.firstkotlinapp.dataClass.MarkerEntity

@Dao
interface MarkerDAO{
    @Query("SELECT * FROM marker")
     fun getAll() : List<MarkerDataVO>

    @Query("SELECT * FROM marker")
    fun getAllLive() : LiveData<List<MarkerDataVO>>

    @Query("SELECT * FROM marker WHERE seq = :seq")
     fun getOne(seq : Int) : MarkerDataVO

    //앱 실행시 비동기된 데이터 불러오기
    @Query("SELECT * FROM marker WHERE synchronization != 'true'")
    fun getAsyncList() :  List<MarkerDataVO>

    @Insert
     fun insert(markerEntity: MarkerDataVO)

    //충돌이 발생하면 기존데이터와 입력데이터를 교체합니다.
    @Insert(onConflict = REPLACE)
    fun insertMarkerList(markerEntity: List<MarkerDataVO>?)

    @Delete
     fun delete(markerEntity: MarkerDataVO)

    //비동기 된 데이터 동기화 처리
    @Update
    fun updateSync(markerEntity: MarkerDataVO)

    @Query("SELECT SEQ FROM marker ORDER BY seq DESC LIMIT 1")
    fun getSeq() : Int
}