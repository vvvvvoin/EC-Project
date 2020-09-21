package com.example.firstkotlinapp.map

import android.content.Context
import com.example.firstkotlinapp.dataClass.MarkerDataVO
import com.google.android.gms.maps.GoogleMap
import com.google.maps.android.clustering.ClusterManager

class ClustManagerSingletone  private constructor(clusterManager: ClusterManager<MarkerDataVO>, mContext: Context, mMap: GoogleMap?){
    companion object ClustManagerObject {

        @Volatile
        private var instance: ClustManagerSingletone? = null

        fun getInstance(clusterManager: ClusterManager<MarkerDataVO>, mContext: Context, mMap: GoogleMap?): ClustManagerSingletone =
            instance ?: synchronized(this) {
                instance ?: ClustManagerSingletone(clusterManager, mContext, mMap).also {
                    instance = it
                }
            }
    }
}