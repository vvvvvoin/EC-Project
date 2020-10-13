package com.example.firstkotlinapp.map

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.content.pm.ActivityInfo
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.location.Location
import android.location.LocationManager
import android.net.Uri
import android.net.wifi.WifiManager
import android.os.Build
import android.os.Bundle
import android.os.Looper
import android.provider.Settings
import android.util.Log
import android.view.*
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.graphics.drawable.toBitmap
import androidx.core.net.toUri
import androidx.core.widget.NestedScrollView
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager2.widget.ViewPager2
import com.bumptech.glide.Glide
import com.example.firstkotlinapp.R
import com.example.firstkotlinapp.dataBase.AppDatabase
import com.example.firstkotlinapp.dataBase.MarkerDAO
import com.example.firstkotlinapp.dataClass.MarkerDataVO
import com.example.firstkotlinapp.map.customView.SearchBar01CustomView
import com.example.firstkotlinapp.map.fragment.MyMarkerFragment
import com.example.firstkotlinapp.map.fragment.MyMarkerFragment2
import com.example.firstkotlinapp.map.fragment.SearchFragment
import com.example.firstkotlinapp.map.fragment.SettingFragment
import com.example.firstkotlinapp.recycler.list.*
import com.example.firstkotlinapp.viewModel.MarkerViewModel
import com.example.firstkotlinapp.retrofit.OkHttpManager
import com.example.firstkotlinapp.viewModel.MyViewModel
import com.google.android.gms.location.*
import com.google.android.gms.maps.*
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.Marker
import com.google.android.gms.maps.model.MarkerOptions
import com.google.android.material.appbar.AppBarLayout
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.auth.FirebaseAuth
import com.google.maps.android.clustering.ClusterManager
import kotlinx.android.synthetic.main.activity_test.*
import kotlinx.android.synthetic.main.search_list_bottom_sheet_detail01.*
import kotlinx.android.synthetic.main.search_list_bottom_sheet_detail02.*
import kotlinx.android.synthetic.main.search_list_bottom_sheet_detail03.*
import kotlinx.android.synthetic.main.search_list_bottom_sheet_recyclerview.*
import kotlinx.android.synthetic.main.search_list_bottom_sheet_viewpager.*
import java.lang.Math.abs

class TestActivity : AppCompatActivity(), OnMapReadyCallback , ClusterManager.OnClusterItemClickListener<MarkerDataVO>, GoogleMap.OnMapClickListener, GoogleMap.OnCameraIdleListener{
    val TAG = "MapTestActivity"

    private var mMap: GoogleMap? = null
    private var currentMarker : Marker? = null
    private var latestLatLng : LatLng? = null
    private var latestLatLngFlag : Boolean = false
    private  var latestZoomLevel : Float = 0.0F

    private lateinit var auth : FirebaseAuth

    private lateinit var mFusedLocationClient: FusedLocationProviderClient
    private lateinit var locationRequest : LocationRequest

    private lateinit var mLayout : View
    private lateinit var mCurrentLocatiion : Location
    private lateinit var clusterManager: ClusterManager<MarkerDataVO>
    private lateinit var clusterRenderer : ClusterRenderer
    private lateinit var clusterRendererData : MarkerDataVO

    var needRequest:Boolean = false

    var REQUIRED_PERMISSIONS = arrayOf<String>(
        Manifest.permission.ACCESS_FINE_LOCATION,
        Manifest.permission.ACCESS_COARSE_LOCATION,
        Manifest.permission.READ_EXTERNAL_STORAGE
    )
    private val UPDATE_INTERVAL_MS = 500 // 0.5초
    private val FASTEST_UPDATE_INTERVAL_MS = 250 // 0.25초
    private val PERMISSIONS_REQUEST_CODE = 3000
    private val GPS_ENABLE_REQUEST_CODE = 2001
    private val MARKER_POPUP_REGISTER = 5000
    private val MARKER_POPUP_VEIW = 5005

    private var  CAMERA_DEFAULT_ZOOM = 15f

    private val WIFI_SERVER_SSID = "\"chroma prime\""
    private val WIFI_SERVER_ID = "88:36:6c:2a:3e:0a"

    private val markerDAO: MarkerDAO by lazy { AppDatabase.getInstance(this).markerDAO()}

    //private var asynchronizedUri: Uri? = null
    private lateinit var asynchronizedUri: Uri

    private lateinit var bottomNavigation : BottomNavigationView
    private lateinit var searchBar : SearchBar01CustomView

    private lateinit var bottomSheetRecyclerView : RecyclerView
    private lateinit var bottomSheetViewPager2 : ViewPager2

    private lateinit var model : MyViewModel
    private lateinit var model2 : MarkerViewModel

    private lateinit var searchFragment : SearchFragment
    private lateinit var sheetBehaviorDetail : BottomSheetBehavior<View>
    private lateinit var sheetBehaviorRecyclerView : BottomSheetBehavior<View>
    private lateinit var sheetBehaviorViewPager2 : BottomSheetBehavior<View>
    private lateinit var markerViewImageRecyclerView: RecyclerView

    private lateinit var  bottomSheetData01 : SearchResultData
    private lateinit var bottomSheetData02 : SearchResultData

    private  var markerViewImageAdapter : MarkerViewImageAdapter? = null
    private  var resultAdapter1 : SearchResultAdapter? = null
    private  var resultAdapter2 : SearchResultAdapter? = null


    @SuppressLint("ClickableViewAccessibility")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_test)


        auth = FirebaseAuth.getInstance()
        Log.d(TAG, "현재 유저는 = ${auth.currentUser?.displayName}")
        Log.d(TAG, "현재 유저는 = ${auth.currentUser?.email}")


        mLayout = findViewById(R.id.map_layout)
        searchBar = findViewById(R.id.searchCustom01)

        bottomNavigation = findViewById(R.id.bottomNavigationView)

        bottomSheetRecyclerView = findViewById<RecyclerView>(R.id.search_result_recyclerView)
        bottomSheetViewPager2 = findViewById<ViewPager2>(R.id.search_result_viewPager)
        markerViewImageRecyclerView = findViewById<RecyclerView>(R.id.bottom_sheet_detail_imageView)


        val linearLayoutManager = LinearLayoutManagerWrapper(applicationContext, LinearLayoutManager.VERTICAL, false)
        bottomSheetRecyclerView.layoutManager = linearLayoutManager
        bottomSheetRecyclerView.setHasFixedSize(true)

        sheetBehaviorRecyclerView = BottomSheetBehavior.from(search_list_bottomSheet_recycler)
        sheetBehaviorRecyclerView.state = BottomSheetBehavior.STATE_HIDDEN
        sheetBehaviorRecyclerView.addBottomSheetCallback(object : BottomSheetBehavior.BottomSheetCallback(){
            override fun onSlide(bottomSheet: View, slideOffset: Float) {          }
            override fun onStateChanged(bottomSheet: View, newState: Int) {
                when(newState){
                    BottomSheetBehavior.STATE_COLLAPSED -> {
                        sheetBehaviorViewPager2.state = BottomSheetBehavior.STATE_HALF_EXPANDED
                    }
                }
            }
        })

        sheetBehaviorViewPager2 = BottomSheetBehavior.from(search_list_bottomSheet_viewpager)
        sheetBehaviorViewPager2.state = BottomSheetBehavior.STATE_HIDDEN
        sheetBehaviorViewPager2.addBottomSheetCallback(object: BottomSheetBehavior.BottomSheetCallback(){
            override fun onSlide(bottomSheet: View, slideOffset: Float) {            }
            override fun onStateChanged(bottomSheet: View, newState: Int) {
                when(newState){
                    BottomSheetBehavior.STATE_HIDDEN -> {
                        if(sheetBehaviorRecyclerView.state == BottomSheetBehavior.STATE_COLLAPSED || sheetBehaviorRecyclerView.state == BottomSheetBehavior.STATE_HIDDEN){
                            initializationView()
                        }
                    }
                }
            }
        })

        sheetBehaviorDetail = BottomSheetBehavior.from(search_list_bottomSheet_detail)
        sheetBehaviorDetail.state = BottomSheetBehavior.STATE_HIDDEN
        sheetBehaviorDetail.addBottomSheetCallback(object : BottomSheetBehavior.BottomSheetCallback(){
            override fun onSlide(bottomSheet: View, slideOffset: Float) {            }
            override fun onStateChanged(bottomSheet: View, newState: Int) {
                when(newState){
                    BottomSheetBehavior.STATE_HIDDEN -> {
                        Log.d(TAG, "STATE_HIDDEN")
                        latestLatLngFlag = false
                        mMap?.animateCamera(CameraUpdateFactory.newLatLngZoom(latestLatLng, latestZoomLevel))
                        //markerViewImageAdapter?.clearList()
                        val bitmap = applicationContext.resources.getDrawable(R.drawable.ic_map_marker04, null)?.toBitmap()
                        clusterRenderer.getMarker(clusterRendererData)?.setIcon(BitmapDescriptorFactory.fromBitmap(Bitmap.createBitmap(bitmap!!)))
                        searchCustom01.visibility = View.VISIBLE
                        AppBarLayout.setExpanded(true)
                    }
                    BottomSheetBehavior.STATE_COLLAPSED -> {
                        Log.d(TAG, "STATE_COLLAPSED")
                        sheetBehaviorDetail.state = BottomSheetBehavior.STATE_HIDDEN
                        AppBarLayout.setExpanded(true)
                    }
                }
            }
        })
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setDisplayShowTitleEnabled(false)

        return_to_map.getSearchIcMapLayout().setOnClickListener {
            sheetBehaviorViewPager2.state = BottomSheetBehavior.STATE_HALF_EXPANDED
            sheetBehaviorRecyclerView.state = BottomSheetBehavior.STATE_HIDDEN
        }
        return_to_list.getSearchIcListLayout().setOnClickListener {
            sheetBehaviorViewPager2.state = BottomSheetBehavior.STATE_HIDDEN
            sheetBehaviorRecyclerView.state = BottomSheetBehavior.STATE_EXPANDED
        }

        val bottomSheetDetailScroll: NestedScrollView =  findViewById(R.id.search_list_bottomSheet_detail_scroll)
        bottomSheetDetailScroll.isSmoothScrollingEnabled = true
        bottom_sheet_detail_save_marker.setOnClickListener {
            // 선택한 마커가 mDB에 존재하는 여부에 따라 insert, update를 결정
            if(model2.mDBMarkDataList.value?.contains(clusterRendererData)!!){
                Toast.makeText(this, "내부 저장소에 데이터를 업데이트 합니다", Toast.LENGTH_SHORT).show()
                model2.mDBUpdate(clusterRendererData)
            }else{
                Toast.makeText(this, "내부 저장소에 데이터를 새로 업로드 합니다", Toast.LENGTH_SHORT).show()
                model2.mDBInsert(clusterRendererData)
            }
        }

//        var y1 = 0.0f
//        var y2 = 0.0f
//        var offSet = 0
//        AppBarLayout.addOnOffsetChangedListener(object : AppBarStateChangeListener() {
//            override fun onStateChanged(appBarLayout: AppBarLayout?, state: State?) {
//            }
//            override fun onOffsetChanged(appBarLayout: AppBarLayout, i: Int) {
//                super.onOffsetChanged(appBarLayout, i)
//                offSet = i
//                search_list_bottomSheet_detail_scroll.setOnTouchListener { view, motionEvent ->
//                    when (motionEvent.action) {
//                        MotionEvent.ACTION_DOWN -> {
//                            y1 = motionEvent.getY()
//                            return@setOnTouchListener false
//                        }
//                        MotionEvent.ACTION_UP -> {
//                            y2 = motionEvent.getY()
//                            if (y2 > y1) {
//                                if (offSet == 0) {
//                                    sheetBehaviorDetail.state = BottomSheetBehavior.STATE_HIDDEN
//                                }
//                            }
//                            return@setOnTouchListener false
//                        }
//                        else -> return@setOnTouchListener false
//                    }
//                }
//            }
//        })

        searchBar.getSearchXbtn().setOnClickListener {
            initializationView()
        }
        // 뷰페이저 page 넘길시 해당 아이템에 대한 위치로 이동시켜주기
        bottomSheetViewPager2.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback(){
            override fun onPageSelected(position: Int) {
                super.onPageSelected(position)
                mMap?.animateCamera(CameraUpdateFactory.newLatLng(LatLng(resultAdapter1!!.getItemList().get(position).lat, resultAdapter1!!.getItemList().get(position).lng)))
            }
        })
        val pageTranslationX = 70
        val pageTransformer = ViewPager2.PageTransformer { page: View, position: Float ->
            page.translationX = -pageTranslationX * position
            // Next line scales the item's height. You can remove it if you don't want this effect
            page.scaleY = 1 - (0.25f * abs(position))
        }
        bottomSheetViewPager2.setPageTransformer(pageTransformer)
        bottomSheetViewPager2.offscreenPageLimit = 2

        val markerViewImageLayoutManager = LinearLayoutManagerWrapper(applicationContext, LinearLayoutManager.HORIZONTAL, false)
        markerViewImageAdapter = MarkerViewImageAdapter(ArrayList(), this)
        markerViewImageRecyclerView.layoutManager = markerViewImageLayoutManager
        markerViewImageRecyclerView.setHasFixedSize(true)
        markerViewImageRecyclerView.adapter = markerViewImageAdapter

        resultAdapter1 = SearchResultAdapter(null, this@TestActivity)
        resultAdapter2 = SearchResultAdapter(null, this@TestActivity)
        bottomSheetRecyclerView.adapter = resultAdapter1
        bottomSheetViewPager2.adapter = resultAdapter2

        //liveData 값 받기
        model  = ViewModelProvider(this).get(MyViewModel::class.java)
        model2 = ViewModelProvider(this).get(MarkerViewModel::class.java)
        //////////////////// 새롭게 추가된 라이브 데이터 검색결과 반영시키기///////////////////////////////////////
        model.searchBarText.observe(this, Observer {
            Log.d(TAG, "observe 실행됨")
            searchBar.getSearchTextView().text = it
        })

        model2.searchData.observe(this, Observer {
            if (it.isEmpty()){
                Toast.makeText(this, "인터넷 연결을 확인해주세요", Toast.LENGTH_SHORT).show()
                return@Observer
            }
            searchBar.getSearchXbtn().visibility = View.VISIBLE
            bottomNavigation.visibility = View.GONE
            map_view_size_plus.visibility = View.GONE
            map_view_size_minus.visibility = View.GONE
            clusterManager.clearItems()

            for (element in it) {
                clusterManager.addItem(element)
            }

            bottomSheetData01 =
                SearchResultData(SearchRecyclerViewAdapterViewType().RESULT_BOTTOM_SHEET, it)
            bottomSheetData02 =
                SearchResultData(SearchRecyclerViewAdapterViewType().RESULT_VIEW_PAGER, it)

            resultAdapter1!!.changeItemList(bottomSheetData01)
            resultAdapter2!!.changeItemList(bottomSheetData02)

            resultAdapter1!!.notifyDataSetChanged()
            resultAdapter2!!.notifyDataSetChanged()
            clusterManager.cluster()

            sheetBehaviorViewPager2.state = BottomSheetBehavior.STATE_HALF_EXPANDED
            sheetBehaviorDetail.state = BottomSheetBehavior.STATE_HIDDEN
            search_list_bottomSheet_detail.visibility = View.GONE
        })

        model2.markerImageSize.observe(this, Observer {
            markerViewImageAdapter!!.setListSize(it)
        })

        // DetailView를 클릭했을 경우 변경된 liveData로 변경
        model2.markerImage.observe(this, Observer {
            if(it.seq == clusterRendererData.seq){
                markerViewImageAdapter!!.addImageItem(it.image)
            }
        })

        locationRequest = LocationRequest().setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY).setInterval(UPDATE_INTERVAL_MS.toLong()).setFastestInterval(FASTEST_UPDATE_INTERVAL_MS.toLong())
        val builder = LocationSettingsRequest.Builder()
        builder.addLocationRequest(locationRequest)
        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this)

        val mapFragment = supportFragmentManager.findFragmentById(R.id.map) as SupportMapFragment

        //비동기 데이터 처리하는 과정
//        if(getWifiInfo(this)) {
//            GlobalScope.launch(Dispatchers.IO) {
//                var aysncDataList = markerDAO.getAsyncList()
//                Log.d(TAG, "비동기 데이터 = ${aysncDataList}")
//                for(it in aysncDataList) {
//                    if(it.synchronization != "false"){
//                        var imageList = mutableListOf<MultipartBody.Part>()
//                        asynchronizedUri = it.synchronization.toUri()
//                        withContext(Dispatchers.IO ) {
//                            makeMultipartBody(asynchronizedUri, applicationContext)
//                        }.let {
//                            imageList.add(it)
//                        }
//                        OkHttpManager().putDataWithImage(it.seq, it.subject, it.content, it.lat, it.lng, it.writer, it.address, imageList)
//                        it.synchronization = "true"
//                        markerDAO.updateSync(it)
//                    }else if(it.synchronization == "false"){
//                        OkHttpManager().putDataWithImage(it.seq, it.subject, it.content, it.lat, it.lng, it.writer, it.address, null)
//                        it.synchronization = "true"
//                        markerDAO.updateSync(it)
//                    }
//
//                }
//                withContext(Dispatchers.Main){
//                    //여기서 다시 전체 데이터를 받아 클러스팅
//                    clusterManager.cluster()
//                }
//
//            }
//        }
        //////////////////////////////////////////////
        // mDB에 비동기 데이터 있다면 이 기능이 실행됨
        model2.mDBAsyncMarkDataList.observe(this, Observer {
            Log.d(TAG, "mDBAsyncMarkDataList = $it")
            for(async in it){
                if(async.synchronization == "false"){
                    model2.putDataWithImage(async.seq, async.subject, async.snippet, async.lat, async.lng, async.writer, async.address, null)
                }else{
                    val list = async.synchronization.split(", ").toTypedArray()
                    Log.d(TAG, "스플릿하여 나타낸 비동기 리스트는 = ${list}")
                    val selectImageUriList =  ArrayList<Uri>()
                    for(stringUri in list){
                        selectImageUriList.add(stringUri.toUri())
                    }
                    Log.d(TAG, "변환된 uri 리스트는 = ${selectImageUriList}")

                    model2.putDataWithImage(async.seq, async.subject, async.snippet, async.lat, async.lng, async.writer, async.address, selectImageUriList)
                }
            }
        })

        // mDB에 전체 데이터가 변화하면 이 기능이 실행됨
        model2.mDBMarkDataList.observe(this, Observer {
            Log.d(TAG, "mDBMarkDataList = $it")
            for(data in it){
                clusterManager.removeItem(data)
                clusterManager.addItem(data)
            }
            clusterManager.cluster()
        })

        searchFragment = SearchFragment.newInstance()
        searchCustom01.getSearchTextView().setOnClickListener {
            supportFragmentManager.beginTransaction().apply {
                replace(R.id.flFragment, searchFragment)
                addToBackStack("searchHistoryFragment")
                commit()
            }
        }

        bottomNavigation.setOnNavigationItemSelectedListener {
            val transaction = supportFragmentManager.beginTransaction()
            var selectTag = "tag"
            when(it.itemId){
                R.id.bottom_navigation_menu_01 -> {
                    transaction.addToBackStack("map")
                    transaction.commit()
                    return@setOnNavigationItemSelectedListener true
                }
                R.id.bottom_navigation_menu_02 -> {
                    transaction.replace(R.id.frameLayout_inside, MyMarkerFragment.newInstance(), "my_marker")
                    selectTag = "my_marker"
                }
                R.id.bottom_navigation_menu_03 -> {
                    transaction.replace(R.id.frameLayout_inside, MyMarkerFragment2.newInstance(), "my_marker2")
                    selectTag = "my_marker2"
                }
                R.id.bottom_navigation_menu_04 -> {
                    transaction.replace(R.id.frameLayout_inside, SettingFragment.newInstance(), "setting")
                    selectTag = "setting"
                }
            }
            Log.d(TAG, "최근 프래그먼트 태그 = ${supportFragmentManager.fragments.last().tag}")

            if(supportFragmentManager.fragments.last().tag != selectTag){
                transaction.addToBackStack(selectTag)
            }
            Log.d(TAG, "프래그먼트 수 = ${supportFragmentManager.backStackEntryCount}")
            transaction.commit()
            return@setOnNavigationItemSelectedListener true
        }


        searchCustom01.getHambugerButton().setOnClickListener {
            Toast.makeText(this, "햄버거", Toast.LENGTH_SHORT).show()
        }

        mapFragment.getMapAsync(this)
    }

    fun updateBottomMenu(navigation: BottomNavigationView) {
        val tag2: Fragment? = supportFragmentManager.findFragmentByTag("my_marker")
        val tag3: Fragment? = supportFragmentManager.findFragmentByTag("my_marker2")
        val tag4: Fragment? = supportFragmentManager.findFragmentByTag("setting")

        if(tag2 != null && tag2.isVisible()) navigation.getMenu().findItem(R.id.bottom_navigation_menu_02).setChecked(true)
        else if(tag3 != null && tag3.isVisible()) navigation.getMenu().findItem(R.id.bottom_navigation_menu_03).setChecked(true)
        else if(tag4 != null && tag4.isVisible()) navigation.getMenu().findItem(R.id.bottom_navigation_menu_04).setChecked(true)
        else navigation.getMenu().findItem(R.id.bottom_navigation_menu_01).setChecked(true)
    }


    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap
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

        clusterManager = ClusterManager(this, mMap)
        clusterRenderer = ClusterRenderer(this, mMap, clusterManager)

        //기존 구글맵의 카메라 cameraIdleListener를 정의하고 오버라이딩된 메소드에 clusterManager.cluster() 를 넣는다
        mMap?.setOnCameraIdleListener(this)
        mMap?.setOnMarkerClickListener (clusterManager)
        clusterManager.setOnClusterItemClickListener(this)

        mMap?.uiSettings?.isMyLocationButtonEnabled = true
        mMap?.animateCamera(CameraUpdateFactory.zoomTo(15f))
        mMap?.setOnMapClickListener (this)

        //서버와 연결중에 있으면 데이터를 받아 맵에 뿌린다
//        if (getWifiInfo(this)) {
//            Log.d(TAG, "서버연결확인 후 동기화 처리")
//              OkHttpManager().getDataList(markerDAO, clusterManager)
//        }else{
//            Log.d(TAG, "서버연결안됬지만 mobileDB를 이용해 처리")
//            GlobalScope.launch(Dispatchers.Main) {
//                val list = async(Dispatchers.IO) { markerDAO.getAll() }
//                for (marker: MarkerDataVO in list.await()) {
//                    clusterManager.addItem(marker)
//                }
//            }
//            clusterManager.cluster()
//        }
        // 초기 앱 실행시 마커보여주기 (이 기능은 추후 삭제될 예정, 검색된 데이터만 보여줄 예정이기 떄문)
        model2.getFirstLiveDataList()
        model2.firstData.observe(this, Observer {
            if(it.isEmpty()){
                Toast.makeText(this, "인터넷 연결을 확인해주세요.", Toast.LENGTH_SHORT).show()
                return@Observer
            }
            Toast.makeText(this, "MVVM패턴을 이용해 초기 전체 마커 표시", Toast.LENGTH_SHORT).show()
            clusterManager.clearItems()
            for(marker : MarkerDataVO in it){
                clusterManager.addItem(marker)
            }
            clusterManager.cluster()
        })
    }

    // boittomSheetDetail 좌측상단 뒤로가기 버튼 클릭 이벤트
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when(item.itemId){
            android.R.id.home -> {
                Log.d(TAG, "뒤로가기 버튼 눌림")
                sheetBehaviorDetail.state = BottomSheetBehavior.STATE_HIDDEN
                return true
            }
        }
        return super.onOptionsItemSelected(item)
    }

    // bottomSheetDetail 상세뷰 데이터 보여주고 카메라 에니메이트,
    // 클러스트 랜더러를 통해 마커 변화, 주변 UI 숨김
    fun showBottomSheetDetail(lat: Double?, lng: Double?, markerDataVO: MarkerDataVO?) {
        markerViewImageAdapter?.clearList()
        model2.getMarkerImageAddress(markerDataVO!!.seq)
        clusterRendererData = markerDataVO
        latestLatLngFlag = true
        search_list_bottomSheet_detail.visibility = View.VISIBLE
        sheetBehaviorDetail.state = BottomSheetBehavior.STATE_EXPANDED

        if (lat != null && lng != null) {
            mMap?.animateCamera(CameraUpdateFactory.newLatLngZoom(LatLng(lat  -  0.0015, lng  ), 17f))
            clusterRenderer.getMarker(markerDataVO)?.setIcon(BitmapDescriptorFactory.fromBitmap( applicationContext.resources.getDrawable(R.drawable.ic_map_marker03, null)?.toBitmap()))
        }
        bottom_sheet_detail_subject.text = markerDataVO.subject
        bottom_sheet_detail_content.text = markerDataVO.content
        bottom_sheet_detail_writer.text = markerDataVO.writer
        searchCustom01.visibility = View.GONE
    }

    // bottomSheetRecyclerView에서 아이템 클릭시 해당 마커로 이동과 동시에
    // ViewPager2 UI가 나타나고 클릭된 아이템의 position으로 viewPager2에 바로 보여줌
    fun moveCameraAndShowBottomSheetViewPager(position: Int, markerDataVO: MarkerDataVO){
        mMap?.animateCamera(CameraUpdateFactory.newLatLngZoom(LatLng(markerDataVO.lat, markerDataVO.lng), 14f))
        sheetBehaviorRecyclerView.state = BottomSheetBehavior.STATE_COLLAPSED
        bottomSheetViewPager2.setCurrentItem(position, true)
    }

    // 초기화면으로 UI 초기화
    fun initializationView(){
        bottomNavigation.visibility = View.VISIBLE
        searchCustom01.visibility = View.VISIBLE
        map_view_size_plus.visibility = View.VISIBLE
        map_view_size_minus.visibility = View.VISIBLE
        searchBar.getSearchTextView().text = ""
        searchBar.getSearchXbtn().visibility = View.INVISIBLE
        sheetBehaviorRecyclerView.state = BottomSheetBehavior.STATE_HIDDEN
        sheetBehaviorViewPager2.state = BottomSheetBehavior.STATE_HIDDEN
        sheetBehaviorDetail.state = BottomSheetBehavior.STATE_HIDDEN
    }

    override fun onBackPressed() {
        Log.d(TAG, "뒤로가기")


        // bottomSheetDetail이 확장될때 뒤로가기 버튼으로  닫음
        if(sheetBehaviorDetail.state == BottomSheetBehavior.STATE_EXPANDED){
            sheetBehaviorDetail.state = BottomSheetBehavior.STATE_HIDDEN
            return
        }
        // bottomSheetRecyclerView이 확장될때 뒤로가기 버튼으로  닫음
        if(sheetBehaviorRecyclerView.state == BottomSheetBehavior.STATE_EXPANDED){
            sheetBehaviorRecyclerView.state = BottomSheetBehavior.STATE_COLLAPSED
            return
        }
        // bottomSheetViewPager2이 확장될때와 목록보기만 보일 때에 뒤로가기 버튼으로  닫음
        if(sheetBehaviorViewPager2.state == BottomSheetBehavior.STATE_EXPANDED || sheetBehaviorViewPager2.state == BottomSheetBehavior.STATE_COLLAPSED){
            sheetBehaviorViewPager2.state = BottomSheetBehavior.STATE_HIDDEN
            return
        }
        super.onBackPressed()
        updateBottomMenu(bottomNavigation)
//        if(supportFragmentManager.backStackEntryCount != 0){
//            supportFragmentManager.popBackStack()
//            Log.d(TAG, "supportFragmentManager.popBackStack() 로 실행됨")
//        }else{
//            Log.d(TAG, "super.onBackPressed() 로 실행됨")
//
//        }
    }

    override fun onMapClick(latlng: LatLng) {
        Log.d(TAG, "구글맵에 클릭한 위치의 위도경도는 ${latlng}입니다")
        val alertDialog = AlertDialog.Builder(ContextThemeWrapper(this, R.style.Theme_AppCompat_Light_Dialog))
        alertDialog.setTitle("마커등록")
        val address : String  = GeoCoder().getAddress(this, latlng)
        alertDialog.setMessage("해당위치에 마커를 등록할까요? \n${address}")
        alertDialog.setPositiveButton("확인") { dialog, id ->
            Log.d(TAG, "등록")
            val intent = Intent(this, PopUpEditActivity2::class.java)
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
        showBottomSheetDetail(item?.lat, item?.lng, item)
        Toast.makeText(this, "해당 seq = ${item?.seq}", Toast.LENGTH_LONG).show()
        return true
    }

    override fun onCameraIdle() {
        Log.d(TAG, "구글맵 onCameraIdle 호출")
        Log.d(TAG, mMap?.cameraPosition?.bearing.toString())
        if(!latestLatLngFlag) {
            latestLatLng = mMap?.cameraPosition?.target
            latestZoomLevel = mMap?.cameraPosition?.zoom!!
        }
        clusterManager.cluster()
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
        mMap?.moveCamera(CameraUpdateFactory.newLatLngZoom(DEFAULT_LOCATION, 12f))
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

                        Toast.makeText(this, "${markerData.address} 에 마커를 등록했습니다.", Toast.LENGTH_SHORT).show()

                        model2.putDataWithImage(markerData.seq, markerData.title, markerData.snippet, markerData.lat, markerData.lng, markerData.writer, markerData.address, null)
//                        GlobalScope.launch(Dispatchers.IO) {
//                            if(getWifiInfo(this@TestActivity)){
//                                CoroutineScope(Dispatchers.IO).launch {
//                                    async {
//                                        markerDAO.insert(markerData)
//                                        seq = markerDAO.getSeq()
//                                    }.await().let {
//                                        Log.d(TAG, "가장 최근에 존재하는 seq는 ${it}")
//                                        OkHttpManager().putDataWithImage(seq+1, markerData.title, markerData.snippet, markerData.lat, markerData.lng, markerData.writer, markerData.address, null)
//                                    }
//                                }
//                                //var getData = OkHttpManager().putDataWithImage(markerData.title, markerData.snippet, markerData.lat, markerData.lng, markerData.writer, markerData.address, null)
//                                //getData를 받기전 다음 코드가 실행되며 무조건 else로 빠지는데 sync를 맞추면 해결할 수 있는데 방법을 아직 모른다
//                                /*if(getData != null)Log.d(TAG, "마커 DB에 저장 후 서버로에 저장하고 받은 ${getData}")
//                                else Log.d(TAG, "서버 연결실패 getData =  ${getData}")*/
//                            }else{
//                                markerData.synchronization = "false"
//                                markerData.seq = markerDAO.getSeq() + 1
//                                markerDAO.insert(markerData)
//                                Log.d(TAG, "마커 DB에만 저장 = ${markerData}")
//                            }
//                            clusterManager.addItem(markerData)
//                            withContext(Dispatchers.Main){
//                                clusterManager.cluster()
//                            }
//                        }
                    }
                    5002 -> let {
                        Log.d(TAG, "마커 등록 취소")
                        return
                    }
                    5003 -> let {
                        Log.d(TAG, "마커와 이미지 등록")
                        markerData = data?.getSerializableExtra("marker_data") as MarkerDataVO
                        val imageList = data.getParcelableArrayListExtra<Uri>("marker_image_list")

                        val takeFlags = Intent.FLAG_GRANT_READ_URI_PERMISSION or
                                Intent.FLAG_GRANT_WRITE_URI_PERMISSION
                        for(image in imageList!!){
                            contentResolver.takePersistableUriPermission(image, takeFlags)
                        }
                        model2.putDataWithImage(markerData.seq, markerData.title, markerData.snippet, markerData.lat, markerData.lng, markerData.writer, markerData.address, imageList)


//                        if(getWifiInfo(this)){
//                            imageList.add(makeMultipartBody(selectImage, this))
//
//                            CoroutineScope(Dispatchers.IO).launch {
//                                async(Dispatchers.IO) {
//                                    markerDAO.insert(markerData)
//                                    markerDAO.getSeq()
//                                }.await().let {
//                                    async {
//                                        Log.d(TAG, "가장 최근에 존재하는 seq는 ${it}")
//                                        markerData.seq = it
//                                        OkHttpManager().putDataWithImage(it, markerData.title, markerData.snippet, markerData.lat, markerData.lng, markerData.writer, markerData.address, imageList)
//                                        clusterManager.addItem(markerData)
//                                    }.await().let {
//                                        withContext(Dispatchers.Main) {
//                                            clusterManager.cluster()
//                                        }
//                                    }
//                                }
//                            }
//                        }else{
//                            val uriString = selectImage.toString()
//                            markerData.synchronization = uriString
//                            CoroutineScope(Dispatchers.IO).launch {
//                                async {
//                                    markerDAO.insert(markerData)
//                                    markerDAO.getSeq()
//                                }.await().let {
//                                    markerData.seq = it
//                                    clusterManager.addItem(markerData)
//                                    withContext(Dispatchers.Main){
//                                        clusterManager.cluster()
//                                    }
//                                }
//                            }
//                        }
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
                            model2.deleteMarker(markerData.seq)
                            model2.mDBDelete(markerData)
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
    override fun onDestroy() {
        auth.signOut()
        super.onDestroy()
    }


}



