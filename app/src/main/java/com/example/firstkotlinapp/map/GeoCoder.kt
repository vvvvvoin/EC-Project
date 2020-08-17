package com.example.firstkotlinapp.map

import android.content.Context
import android.location.Address
import android.location.Geocoder
import android.widget.Toast
import com.google.android.gms.maps.model.LatLng
import java.io.IOException
import java.util.*

class GeoCoder {
    fun getAddress(context: Context, latlng: LatLng): String {
        //지오코더... GPS를 주소로 변환
        val geocoder = Geocoder(context, Locale.getDefault())
        val addresses: List<Address>
        addresses = try {
            geocoder.getFromLocation(
                latlng.latitude,
                latlng.longitude,
                1
            )
        } catch (ioException: IOException) {
            //네트워크 문제
            Toast.makeText(context, "지오코더 서비스 사용불가", Toast.LENGTH_LONG).show()
            return "지오코더 서비스 사용불가"
        } catch (illegalArgumentException: IllegalArgumentException) {
            Toast.makeText(context, "잘못된 GPS 좌표", Toast.LENGTH_LONG).show()
            return "잘못된 GPS 좌표"
        }
        return if (addresses == null || addresses.isEmpty()) {
            Toast.makeText(context, "주소 미발견", Toast.LENGTH_LONG).show()
            "주소 미발견"
        } else {
            val address: Address = addresses[0]
            address.getAddressLine(0).toString()
        }
    }
}