package com.example.firstkotlinapp.map

import android.Manifest
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Location
import android.location.LocationManager
import android.net.Uri
import android.net.wifi.WifiManager
import android.os.Bundle
import android.os.Looper
import android.provider.Settings
import android.util.Log
import android.view.ContextThemeWrapper
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.net.toUri
import com.example.firstkotlinapp.R
import com.example.firstkotlinapp.dataBase.AppDatabase
import com.example.firstkotlinapp.dataBase.MarkerDAO
import com.example.firstkotlinapp.dataClass.MarkerDataVO
import com.example.firstkotlinapp.map.fragment.SearchFragment
import com.example.firstkotlinapp.map.fragment.SearchHistoryFragment
import com.example.firstkotlinapp.retrofit.OkHttpManager
import com.example.firstkotlinapp.util.makeMultipartBody
import com.google.android.gms.location.*
import com.google.android.gms.maps.*
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.Marker
import com.google.android.gms.maps.model.MarkerOptions
import com.google.android.material.snackbar.Snackbar
import com.google.maps.android.clustering.ClusterManager
import kotlinx.android.synthetic.main.activity_test.*
import kotlinx.coroutines.*
import okhttp3.MultipartBody

class TestActivity : AppCompatActivity(), OnMapReadyCallback , ClusterManager.OnClusterItemClickListener<MarkerDataVO>, GoogleMap.OnMapClickListener, GoogleMap.OnCameraIdleListener{
    val TAG = "MapTestActivity"

    private var mMap: GoogleMap? = null
    private var currentMarker : Marker? = null

    private lateinit var mFusedLocationClient: FusedLocationProviderClient
    private lateinit var locationRequest : LocationRequest

    private lateinit var mLayout : View
    private lateinit var mCurrentLocatiion : Location
    lateinit var clusterManager: ClusterManager<MarkerDataVO>

    var needRequest:Boolean = false

    var REQUIRED_PERMISSIONS = arrayOf<String>(
        Manifest.permission.ACCESS_FINE_LOCATION,
        Manifest.permission.ACCESS_COARSE_LOCATION,
        Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.MANAGE_DOCUMENTS
    )
    private val UPDATE_INTERVAL_MS = 500 // 0.5초
    private val FASTEST_UPDATE_INTERVAL_MS = 250 // 0.25초
    private val PERMISSIONS_REQUEST_CODE = 3000
    private val GPS_ENABLE_REQUEST_CODE = 2001
    private val MARKER_POPUP_REGISTER = 5000
    private val MARKER_POPUP_VEIW = 5005

    private var  CAMERA_DEFAULT_ZOOM = 15f

    private val WIFI_SERVER_SSID = ""
    private val WIFI_SERVER_ID = ""

    private val markerDAO: MarkerDAO by lazy { AppDatabase.getInstance(this).markerDAO()}

    //private var asynchronizedUri: Uri? = null
    private lateinit var asynchronizedUri: Uri

    private lateinit var searchFragment : SearchFragment

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_test)

        mLayout = findViewById(R.id.map_layout)

        locationRequest = LocationRequest().setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY).setInterval(UPDATE_INTERVAL_MS.toLong()).setFastestInterval(FASTEST_UPDATE_INTERVAL_MS.toLong())
        val builder = LocationSettingsRequest.Builder()
        builder.addLocationRequest(locationRequest)

        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this)

        val mapFragment = supportFragmentManager.findFragmentById(R.id.map) as SupportMapFragment


        //비동기 데이터 처리하는 과정
        if(getWifiInfo(this)) {
            GlobalScope.launch(Dispatchers.IO) {
                var aysncDataList = markerDAO.getAsyncList()
                Log.d(TAG, "비동기 데이터 = ${aysncDataList}")
                for(it in aysncDataList) {
                    if(it.synchronization != "false"){
                        var imageList = mutableListOf<MultipartBody.Part>()
                        asynchronizedUri = it.synchronization.toUri()
                        withContext(Dispatchers.IO ) {
                            makeMultipartBody(asynchronizedUri, applicationContext)
                        }.let {
                            imageList.add(it)
                        }
                        OkHttpManager().putDataWithImage(it.seq, it.subject, it.content, it.lat, it.lng, it.writer, it.address, imageList)
                        it.synchronization = "true"
                        markerDAO.updateSync(it)
                    }else if(it.synchronization == "false"){
                        OkHttpManager().putDataWithImage(it.seq, it.subject, it.content, it.lat, it.lng, it.writer, it.address, null)
                        it.synchronization = "true"
                        markerDAO.updateSync(it)
                    }

                }
                withContext(Dispatchers.Main){
                    //여기서 다시 전체 데이터를 받아 클러스팅
                    clusterManager.cluster()
                }
            }
        }
        searchFragment = SearchFragment.newInstance()
        searchCustom01.getSearchTextView().setOnClickListener {
            supportFragmentManager.beginTransaction().apply {
                replace(R.id.flFragment, searchFragment)
                addToBackStack("searchHistoryFragment")
                commit()
            }
        }
        searchCustom01.getHambugerButton().setOnClickListener {
            Toast.makeText(this, "햄버거", Toast.LENGTH_SHORT).show()
        }

        mapFragment.getMapAsync(this)
    }



    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap
        var cameraUpdate : CameraUpdate
        map_view_size_plus.setOnClickListener {
            mMap?.animateCamera(CameraUpdateFactory.zoomIn())
        }
        map_view_size_minus.setOnClickListener {
           mMap?.animateCamera(CameraUpdateFactory.zoomOut())
        }

        find_my_location_btn.setOnClickListener {
            if(find_my_location_btn.isSelected){
                find_my_location_btn.isSelected = false
                mFusedLocationClient.removeLocationUpdates(locationCallback)
            }else{
                find_my_location_btn.isSelected = true
                startLocationUpdates()
            }

        }

        setDefaultLocation()

        val hasFineLocationPermission = ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
        val hasCoarseLocationPermission = ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION)

        if (hasFineLocationPermission == PackageManager.PERMISSION_GRANTED &&
            hasCoarseLocationPermission == PackageManager.PERMISSION_GRANTED   ) {

        }else {  //2. 퍼미션 요청을 허용한 적이 없다면 퍼미션 요청이 필요합니다. 2가지 경우(3-1, 4-1)가 있습니다.

            // 3-1. 사용자가 퍼미션 거부를 한 적이 있는 경우에는
            if (ActivityCompat.shouldShowRequestPermissionRationale(this, REQUIRED_PERMISSIONS[0])) {
                // 3-2. 요청을 진행하기 전에 사용자가에게 퍼미션이 필요한 이유를 설명해줄 필요가 있습니다.
                Snackbar.make(mLayout, "이 앱을 실행하려면 위치 접근 권한이 필요합니다.",
                    Snackbar.LENGTH_INDEFINITE).setAction("확인") {
                    ActivityCompat.requestPermissions(this, REQUIRED_PERMISSIONS, PERMISSIONS_REQUEST_CODE)
                }.show();

            } else {
                // 4-1. 사용자가 퍼미션 거부를 한 적이 없는 경우에는 퍼미션 요청을 바로 합니다.
                // 요청 결과는 onRequestPermissionResult에서 수신됩니다.
                ActivityCompat.requestPermissions( this, REQUIRED_PERMISSIONS, PERMISSIONS_REQUEST_CODE);
            }
        }

        clusterManager = ClusterManager<MarkerDataVO>(this, mMap)
        var clusterRenderer = ClusterRenderer(this, mMap, clusterManager)

        mMap?.setOnCameraIdleListener(clusterManager)

        //mMap?.setOnCameraIdleListener(this)
        //이게 카메라 움직이고 멈췄을대를 감지하는 기능인데
        // 한번에 두개를 적용 못시키는거 같다
        // 클러스트매니저랑 같이 쓸 방법을 찾아야 한다

        mMap?.setOnMarkerClickListener (clusterManager)
        clusterManager.setOnClusterItemClickListener(this)

        mMap?.uiSettings?.isMyLocationButtonEnabled = true
        mMap?.animateCamera(CameraUpdateFactory.zoomTo(15f))
        mMap?.setOnMapClickListener (this)

        //서버와 연결중에 있으면 데이터를 받아 맵에 뿌린다
        if (getWifiInfo(this)) {
            Log.d(TAG, "서버연결확인 후 동기화 처리")
              OkHttpManager().getDataList(markerDAO, clusterManager)
        }else{
            Log.d(TAG, "서버연결안됬지만 mobileDB를 이용해 처리")
            GlobalScope.launch(Dispatchers.Main) {
                val list = async(Dispatchers.IO) { markerDAO.getAll() }
                for (marker: MarkerDataVO in list.await()) {
                    clusterManager.addItem(marker)
                }
            }
            clusterManager.cluster()
        }

       
    }

    override fun onBackPressed() {
        Log.d(TAG, "뒤로가기")
        if(supportFragmentManager.backStackEntryCount != 0){
            supportFragmentManager.popBackStack()
            Log.d(TAG, "supportFragmentManager.popBackStack() 로 실행됨")
        }else{
            Log.d(TAG, "super.onBackPressed() 로 실행됨")
            super.onBackPressed()
        }

    }

    override fun onMapClick(latlng: LatLng) {
        Log.d(TAG, "구글맵에 클릭한 위치의 위도경도는 ${latlng}입니다")
        val alertDialog = AlertDialog.Builder(ContextThemeWrapper(this, R.style.Theme_AppCompat_Light_Dialog))
        alertDialog.setTitle("마커등록")
        val address : String  = GeoCoder().getAddress(this, latlng)
        alertDialog.setMessage("해당위치에 마커를 등록할까요? \n${address}")

        alertDialog.setPositiveButton("확인") { dialog, id ->
            Log.d(TAG, "등록")
            val intent = Intent(this, PopUpEditActivity::class.java)
            intent.putExtra("lat", latlng.latitude)
            intent.putExtra("lng", latlng.longitude)
            startActivityForResult(intent, MARKER_POPUP_REGISTER)
            dialog.dismiss()
        }
        alertDialog.setNegativeButton("취소") { dialog, id ->
            dialog.dismiss()
        }
        alertDialog.show()
    }

    override fun onClusterItemClick(item: MarkerDataVO?): Boolean {
        val intent = Intent(this, PopUpViewActivity::class.java)
        intent.putExtra("view_data", item)
        startActivityForResult(intent, MARKER_POPUP_VEIW)
        Toast.makeText(this, "해당 seq = ${item?.seq}", Toast.LENGTH_LONG).show()
        return true
    }

    override fun onCameraIdle() {
        Log.d(TAG, "구글맵 onCameraIdle 호출")
    }

    private fun startLocationUpdates() {
        if (!checkLocationServicesStatus()) {
            Log.d( TAG, "startLocationUpdates : call showDialogForLocationServiceSetting" )
            showDialogForLocationServiceSetting()
        } else {
            val hasFineLocationPermission = ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION )
            val hasCoarseLocationPermission = ContextCompat.checkSelfPermission(this,Manifest.permission.ACCESS_COARSE_LOCATION )
            if (hasFineLocationPermission != PackageManager.PERMISSION_GRANTED ||  hasCoarseLocationPermission != PackageManager.PERMISSION_GRANTED ) {
                Log.d(TAG, "startLocationUpdates : 퍼미션 안가지고 있음")
                return
            }
            Log.d(TAG,"startLocationUpdates : call mFusedLocationClient.requestLocationUpdates")
            mFusedLocationClient.requestLocationUpdates( locationRequest, locationCallback, Looper.myLooper())
            if (checkPermission()) mMap?.isMyLocationEnabled = true
        }
    }

    private fun checkPermission(): Boolean {
        val hasFineLocationPermission = ContextCompat.checkSelfPermission( this, Manifest.permission.ACCESS_FINE_LOCATION)
        val hasCoarseLocationPermission = ContextCompat.checkSelfPermission( this, Manifest.permission.ACCESS_COARSE_LOCATION)
        return hasFineLocationPermission == PackageManager.PERMISSION_GRANTED && hasCoarseLocationPermission == PackageManager.PERMISSION_GRANTED
    }

    private fun setDefaultLocation() {
        //디폴트 위치, Seoul
        val DEFAULT_LOCATION = LatLng(37.56, 126.97)
        val markerTitle = "위치정보 가져올 수 없음"
        val markerSnippet = "위치 퍼미션과 GPS 활성 요부 확인하세요"
        currentMarker?.remove()
        val markerOptions = MarkerOptions().let{
            it.position(DEFAULT_LOCATION)
            it.title(markerTitle)
            it.snippet(markerSnippet)
            it.draggable(true)
            it.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED))
        }
        currentMarker = mMap?.addMarker(markerOptions)
        val cameraUpdate = CameraUpdateFactory.newLatLngZoom(DEFAULT_LOCATION, 15f)
        mMap?.moveCamera(cameraUpdate)
    }

    private fun showDialogForLocationServiceSetting() {
        val builder: AlertDialog.Builder = AlertDialog.Builder(this)
        builder.setTitle("위치 서비스 비활성화")
        builder.setMessage("앱을 사용하기 위해서는 위치 서비스가 필요합니다."+ "위치 설정을 수정하실래요?")
        builder.setCancelable(true)
        builder.setPositiveButton("설정") { dialog, id ->
            val callGPSSettingIntent = Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS)
            startActivityForResult(callGPSSettingIntent, GPS_ENABLE_REQUEST_CODE)
        }
        builder.setNegativeButton("취소"
        ) { dialog, id -> dialog.cancel() }
        builder.create().show()
    }

    private var locationCallback: LocationCallback = object : LocationCallback() {
        override fun onLocationResult(locationResult: LocationResult) {
            super.onLocationResult(locationResult)
            val locationList: List<Location> = locationResult.locations
            if (locationList.isNotEmpty()) {
                var location = locationList[locationList.size - 1]
                location = locationList[0];
                val currentPosition = LatLng(location.latitude, location.longitude)
                Log.d(TAG, "onLocationResult : ${currentPosition}")
                //현재 위치에 마커 생성하고 이동
                setCurrentLocation(location)
                mCurrentLocatiion = location
            }
        }
    }

    fun setCurrentLocation( location: Location) {
        val currentLatLng = LatLng(location.latitude, location.longitude)
        val cameraUpdate: CameraUpdate = CameraUpdateFactory.newLatLng(currentLatLng)
        mMap?.animateCamera(cameraUpdate)
    }

    private fun checkLocationServicesStatus(): Boolean {
        val locationManager: LocationManager = getSystemService(Context.LOCATION_SERVICE) as LocationManager
        return (locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER) || locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER))
    }


    override fun onRequestPermissionsResult(permsRequestCode: Int, permissions: Array<String>, grandResults: IntArray) {
        if (permsRequestCode == PERMISSIONS_REQUEST_CODE && grandResults.size == REQUIRED_PERMISSIONS.size) {
            // 요청 코드가 PERMISSIONS_REQUEST_CODE 이고, 요청한 퍼미션 개수만큼 수신되었다면
            var check_result = true
            // 모든 퍼미션을 허용했는지 체크합니다.
            for (result in grandResults) {
                if (result != PackageManager.PERMISSION_GRANTED) {
                    check_result = false
                    break
                }
            }

            if (check_result) {
                // 퍼미션을 허용했다면 위치 업데이트를 시작합니다.
                startLocationUpdates()
            } else {
                // 거부한 퍼미션이 있다면 앱을 사용할 수 없는 이유를 설명해주고 앱을 종료합니다.2 가지 경우가 있습니다.
                if (ActivityCompat.shouldShowRequestPermissionRationale(this,REQUIRED_PERMISSIONS[0])|| ActivityCompat.shouldShowRequestPermissionRationale(this, REQUIRED_PERMISSIONS[1])) {
                    // 사용자가 거부만 선택한 경우에는 앱을 다시 실행하여 허용을 선택하면 앱을 사용할 수 있습니다.
                    Snackbar.make(
                        mLayout, "퍼미션이 거부되었습니다. 앱을 다시 실행하여 퍼미션을 허용해주세요. ",
                        Snackbar.LENGTH_INDEFINITE
                    ).setAction("확인") { finish() }.show()
                } else {
                    // "다시 묻지 않음"을 사용자가 체크하고 거부를 선택한 경우에는 설정(앱 정보)에서 퍼미션을 허용해야 앱을 사용할 수 있습니다.
                    Snackbar.make(
                        mLayout, "퍼미션이 거부되었습니다. 설정(앱 정보)에서 퍼미션을 허용해야 합니다. ",
                        Snackbar.LENGTH_INDEFINITE
                    ).setAction("확인") { finish() }.show()
                }
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent? ) {
        super.onActivityResult(requestCode, resultCode, data)
        when (requestCode) {
            GPS_ENABLE_REQUEST_CODE ->
                //사용자가 GPS 활성 시켰는지 검사
                if (checkLocationServicesStatus()) {
                    if (checkLocationServicesStatus()) {
                        Log.d(TAG, "onActivityResult : GPS 활성화 되있음")
                        needRequest = true
                        return
                    }
                }

            MARKER_POPUP_REGISTER -> {
                var markerData : MarkerDataVO
                when(resultCode) {
                        5001 -> let {
                        Log.d(TAG, "새로운 마커 등록")
                        markerData = data?.getSerializableExtra("marker_data") as MarkerDataVO
                        var seq: Int = 0

                        Toast.makeText(this, "${markerData.address} 에 마커를 등록했습니다.", Toast.LENGTH_SHORT).show()
                        GlobalScope.launch(Dispatchers.IO) {
                            if(getWifiInfo(this@TestActivity)){
                                CoroutineScope(Dispatchers.IO).launch {
                                    async {
                                        markerDAO.insert(markerData)
                                        seq = markerDAO.getSeq()
                                    }.await().let {
                                        Log.d(TAG, "가장 최근에 존재하는 seq는 ${it}")
                                        OkHttpManager().putDataWithImage(seq+1, markerData.title, markerData.snippet, markerData.lat, markerData.lng, markerData.writer, markerData.address, null)
                                    }
                                }
                                //var getData = OkHttpManager().putDataWithImage(markerData.title, markerData.snippet, markerData.lat, markerData.lng, markerData.writer, markerData.address, null)
                                //getData를 받기전 다음 코드가 실행되며 무조건 else로 빠지는데 sync를 맞추면 해결할 수 있는데 방법을 아직 모른다
                                /*if(getData != null)Log.d(TAG, "마커 DB에 저장 후 서버로에 저장하고 받은 ${getData}")
                                else Log.d(TAG, "서버 연결실패 getData =  ${getData}")*/
                            }else{
                                markerData.synchronization = "false"
                                markerData.seq = markerDAO.getSeq() + 1
                                markerDAO.insert(markerData)
                                Log.d(TAG, "마커 DB에만 저장 = ${markerData}")
                            }
                            clusterManager.addItem(markerData)
                            withContext(Dispatchers.Main){
                                clusterManager.cluster()
                            }
                        }
                    }
                    5002 -> let {
                        Log.d(TAG, "마커 등록 취소")
                        return
                    }
                    5003 -> let {
                        Log.d(TAG, "마커와 이미지 등록")
                        markerData = data?.getSerializableExtra("marker_data") as MarkerDataVO
                        val selectImage = data.getParcelableExtra<Uri>("file_path") as Uri

                        val takeFlags = Intent.FLAG_GRANT_READ_URI_PERMISSION or
                                Intent.FLAG_GRANT_WRITE_URI_PERMISSION
                        contentResolver.takePersistableUriPermission(selectImage, takeFlags)

                        var imageList = mutableListOf<MultipartBody.Part>()
                        if(getWifiInfo(this)){
                            imageList.add(makeMultipartBody(selectImage, this))

                            CoroutineScope(Dispatchers.IO).launch {
                                async(Dispatchers.IO) {
                                    markerDAO.insert(markerData)
                                    markerDAO.getSeq()
                                }.await().let {
                                    async {
                                        Log.d(TAG, "가장 최근에 존재하는 seq는 ${it}")
                                        markerData.seq = it
                                        OkHttpManager().putDataWithImage(it, markerData.title, markerData.snippet, markerData.lat, markerData.lng, markerData.writer, markerData.address, imageList)
                                        clusterManager.addItem(markerData)
                                    }.await().let {
                                        withContext(Dispatchers.Main) {
                                            clusterManager.cluster()
                                        }
                                    }
                                }
                            }
                        }else{
                            val uriString = selectImage.toString()
                            markerData.synchronization = uriString
                            CoroutineScope(Dispatchers.IO).launch {
                                async {
                                    markerDAO.insert(markerData)
                                    markerDAO.getSeq()
                                }.await().let {
                                    markerData.seq = it
                                    clusterManager.addItem(markerData)
                                    withContext(Dispatchers.Main){
                                        clusterManager.cluster()
                                    }
                                }
                            }
                        }
                        /*val parcelFileDescriptor = contentResolver.openFileDescriptor(selectImage, "r", null) ?: return
                        val inputStream = FileInputStream(parcelFileDescriptor.fileDescriptor)
                        val file = File(cacheDir, contentResolver.getFileNmae(selectImage))
                        val outputStream = FileOutputStream(file)
                        inputStream.copyTo(outputStream)
                        val requestBody: RequestBody =  file.asRequestBody("multipart/form-data".toMediaTypeOrNull())
                        val multipartBody = MultipartBody.Part.createFormData("image", file.name, requestBody)

                        //다수의 이미지 처리를 위한 리스트

                        imageList.add(multipartBody)

                        *//*val requestBody: RequestBody = MultipartBody.Builder()
                            .setType(MultipartBody.FORM)
                            .addFormDataPart(
                                "files",
                                file.name,
                                file.asRequestBody(MultipartBody.FORM)
                            )
                            .build()*//*

                        //OkHttpManager().putData(markerData.title, markerData.snippet, markerData.lat, markerData.lng, markerData.writer, markerData.address)
                        //OkHttpManager().uploadImage(multipartBody)

                        CoroutineScope(Dispatchers.IO).launch {
                            async(Dispatchers.IO) {
                                markerDAO.getSeq()
                            }.await().let {
                                async {
                                    Log.d(TAG, "가장 최근에 존재하는 seq는 ${it}")
                                    markerData.seq = it + 1
                                    OkHttpManager().putDataWithImage(it+1, markerData.title, markerData.snippet, markerData.lat, markerData.lng, markerData.writer, markerData.address, imageList)
                                    clusterManager.addItem(markerData)
                                }.await().let {
                                    withContext(Dispatchers.Main) {
                                        clusterManager.cluster()
                                    }
                                }
                            }
                        }*/
                    }//5003 end
                }
            }
            MARKER_POPUP_VEIW -> {
                var markerData : MarkerDataVO
                when(resultCode){
                    5006 -> let {
                        Log.d(TAG, "팝업 뷰 후 정상종료됨")
                    }
                    5007 -> let {
                        Log.d(TAG, "팝업 뷰 후 수정처리되고 정상종료됨")
                        OkHttpManager().getDataList(markerDAO, clusterManager)
                    }
                    5008 -> let {
                        Log.d(TAG, "팝업 뷰 후 삭제처리되고 정상종료됨")
                        if (data != null) {
                            markerData = data.getSerializableExtra("delete_data") as MarkerDataVO
                            OkHttpManager().deleteData(markerData.seq)
                            CoroutineScope(Dispatchers.IO).launch {
                                markerDAO.delete(markerData)
                            }
                            clusterManager.removeItem(markerData)
                            clusterManager.cluster()
                        }
                    }
                }
            }

        }
    }

    fun getWifiInfo(context: Context) : Boolean{
        val wifiManager = context.getSystemService(Context.WIFI_SERVICE) as WifiManager
        val info = wifiManager.connectionInfo
        val ssid = info.ssid
        val id = info.bssid
        return ssid == WIFI_SERVER_SSID && id == WIFI_SERVER_ID
    }

    override fun onStart() {
        super.onStart()
        Log.d(TAG, "onStart")
        if (checkPermission()) {
            if(checkLocationServicesStatus()) {
                Log.d(TAG, "GPS활성화 후 다시 인에이블 되는지 확인")
                if (mMap != null) mMap?.isMyLocationEnabled = true
            }
        }
    }


    override fun onStop() {
        super.onStop()
//        if (mFusedLocationClient != null) {
//            Log.d(TAG, "onStop : call stopLocationUpdates")
//            //mFusedLocationClient.removeLocationUpdates(locationCallback)
//        }
    }

}



