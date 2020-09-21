package com.example.firstkotlinapp.map

import android.content.Context
import android.graphics.Bitmap
import androidx.core.graphics.drawable.toBitmap
import com.example.firstkotlinapp.R
import com.example.firstkotlinapp.dataClass.MarkerDataVO
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.MarkerOptions
import com.google.maps.android.clustering.ClusterManager
import com.google.maps.android.clustering.view.DefaultClusterRenderer


class ClusterRenderer(val context: Context?, map: GoogleMap?, clusterManager: ClusterManager<MarkerDataVO>?): DefaultClusterRenderer<MarkerDataVO>(context, map, clusterManager) {

    init {
        clusterManager?.renderer = this
    }

    override fun setOnClusterItemClickListener(listener: ClusterManager.OnClusterItemClickListener<MarkerDataVO>?) {
        super.setOnClusterItemClickListener(listener)
    }

    override fun onBeforeClusterItemRendered(item: MarkerDataVO, markerOptions: MarkerOptions) {
        when(item.snippet){
            "1" -> {
                markerOptions?.icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_marker1))
                markerOptions?.visible(true)
            }
            "2" -> {
                markerOptions?.icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_marker2))
                markerOptions?.visible(true)
            }
            "3" -> {
                markerOptions?.icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_marker3))
                markerOptions?.visible(true)
            }
            else -> {
                //markerOptions?.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED))

                val bitmap = context?.resources?.getDrawable(R.drawable.ic_map_marker04, null)?.toBitmap()
                markerOptions?.icon(BitmapDescriptorFactory.fromBitmap(Bitmap.createBitmap(bitmap!!)))
                markerOptions?.visible(true)

            }
        }
    }
}