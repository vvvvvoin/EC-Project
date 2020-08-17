package com.example.firstkotlinapp.dataClass

import androidx.room.*
import androidx.room.ColumnInfo.REAL

////@Entity(tableName = "marker",
//    //inheritSuperIndices = true
////)
//data class MarkerEntity (
//    @PrimaryKey var id : Int = 0,
//    @ForeignKey(
//        entity = PersonEntity::class,
//        parentColumns = arrayOf("personId"),
//        childColumns = arrayOf("personForeignKey"),
//        onDelete = ForeignKey.CASCADE
//    )var personForeignKey : Int = 0,
//    var subject: String = "",
//    var content: String = "",
//    @ColumnInfo(name = "LATITUDE", index = true) val lat: Double = 0.0,
//    @ColumnInfo(name = "LONGITUDE", index = true) val lng: Double = 0.0,
//    var writer: String = "",
//    @Ignore val address: String = ""
//)