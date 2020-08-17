package com.example.firstkotlinapp.dataClass

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.Ignore
import androidx.room.PrimaryKey
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.clustering.ClusterItem
import okhttp3.MultipartBody
import java.io.File
import java.io.Serializable

@Entity(tableName = "marker")
data class MarkerDataVO(
    @PrimaryKey(autoGenerate = true) var seq: Int = 0,
    var subject: String = "",
    var content: String = "",
    @ColumnInfo(typeAffinity = ColumnInfo.REAL) val lat: Double = 0.0,
    @ColumnInfo(typeAffinity = ColumnInfo.REAL) val lng: Double = 0.0,
    var writer: String = "",
    val address: String = "",
    var synchronization: String = "true"
) : Serializable, ClusterItem {
    override fun getPosition(): LatLng {
        return LatLng(lat, lng)
    }

    override fun getSnippet(): String {
        return content
    }

    override fun getTitle(): String {
        return subject
    }
}

/*data class MarkerDataVO(
    val seq: Int,
    var subject: String,
    var content: String,
    val lat: Double,
    val lng: Double,
    var writer: String,
    val address: String
) : Serializable, ClusterItem {
    override fun getPosition(): LatLng {
        return LatLng(lat, lng)
    }

    override fun getSnippet(): String {
        return content
    }

    override fun getTitle(): String {
        return subject
    }
}*/

