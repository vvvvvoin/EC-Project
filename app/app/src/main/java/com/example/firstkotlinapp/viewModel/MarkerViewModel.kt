package com.example.firstkotlinapp.viewModel

import android.annotation.SuppressLint
import android.app.Application
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.util.Log
import androidx.core.graphics.drawable.toBitmap
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.firstkotlinapp.R
import com.example.firstkotlinapp.dataClass.MarkerDataVO
import com.example.firstkotlinapp.repository.RetrofitRepository
import com.example.firstkotlinapp.repository.RoomRepository
import com.example.firstkotlinapp.util.makeMultipartBody
import okhttp3.MultipartBody

class MarkerViewModel(application: Application) : AndroidViewModel(application) {
    private val TAG = "MarkerViewModel"
    private val app  = application
    private val retrofitRepository = RetrofitRepository()
    private val roomRepository = RoomRepository(application)
    private val resource = application.resources

    ///////////////////////////////////////////////// Room
    // 만약 버그가 발생하면 LiveData로 dao 리턴값을 변경하도록 한다
    val mDBMarkDataList: LiveData<List<MarkerDataVO>> = roomRepository.getList()
    val mDBAsyncMarkDataList: LiveData<List<MarkerDataVO>> = roomRepository.getListAsync()

    private val _mDBMarkDataOne =  MutableLiveData<MarkerDataVO>()
    val mDBMarkDataOne: LiveData<MarkerDataVO>
        get() = _mDBMarkDataOne

    fun getSeq(seq : Int){
        _mDBMarkDataOne.postValue(roomRepository.getOne(seq))
    }

    fun mDBInsert(markerDataVO: MarkerDataVO){
        roomRepository.insert(markerDataVO)
    }

    fun mDBUpdate(markerDataVO: MarkerDataVO){
        roomRepository.update(markerDataVO)
    }

    fun mDBDelete(markerDataVO: MarkerDataVO){
        roomRepository.delete(markerDataVO)
    }

    ///////////////////////////////////////////////// Retrofit
    private val _searchLiveData = MutableLiveData<List<MarkerDataVO>>()
    val searchData: LiveData<List<MarkerDataVO>>
        get() = _searchLiveData

    private val _firstData = MutableLiveData<List<MarkerDataVO>>()
    val firstData: LiveData<List<MarkerDataVO>>
        get() = _firstData

    private val _markerImage = MutableLiveData<Bitmap>()
    val markerImage: LiveData<Bitmap>
        get() = _markerImage

    suspend fun getSearchMarkerData(searchKeyWord: String) {
        _searchLiveData.postValue(retrofitRepository.getSearchMarkerData(searchKeyWord))
    }

    @SuppressLint("CheckResult")
    fun putDataWithImage(
        seq: Int,
        subject: String,
        snippet: String,
        lat: Double,
        lng: Double,
        writer: String,
        address: String,
        selectImageUri: Uri?
    ) {
        val asyncData : MarkerDataVO
        if(selectImageUri != null){
            val imageList = mutableListOf<MultipartBody.Part>()
            imageList.add(makeMultipartBody(selectImageUri, app))
            asyncData = MarkerDataVO(seq, subject, snippet, lat, lng, writer, address, selectImageUri.toString())
            retrofitRepository.putDataWithImage(seq, subject, snippet, lat, lng, writer, address, imageList).subscribe({
                Log.d(TAG, "데이터(비동기) 삽입 성공")
                mDBDelete(asyncData)
                mDBInsert(MarkerDataVO(it.seq, it.subject, it.snippet, it.lat, it.lng, it.writer, it.address, "true"))
            }, {
                Log.d(TAG, "데이터 삽입 실패, Room에 비동기 데이터로 전환합니다")
                if(mDBMarkDataList.value?.contains(asyncData)!!){
                    mDBUpdate(asyncData)
                }else{
                    mDBInsert(asyncData)
                }
            })
        }else{
            asyncData = MarkerDataVO(seq, subject, snippet, lat, lng, writer, address, "true")
            retrofitRepository.putDataWithImage(seq, subject, snippet, lat, lng, writer, address, null).subscribe({
                Log.d(TAG, "데이터(비동기) 삽입 성공")
                mDBDelete(asyncData)
                mDBInsert(MarkerDataVO(it.seq, it.subject, it.snippet, it.lat, it.lng, it.writer, it.address, "true"))
            }, {
                Log.d(TAG, "데이터 삽입 실패, Room에 비동기 데이터로 전환합니다")
                if(mDBMarkDataList.value?.contains(asyncData)!!){
                    mDBUpdate(asyncData)
                }else{
                    mDBInsert(asyncData)
                }
            })
        }
    }

    fun deleteMarker(seq: Int){
        retrofitRepository.deleteData(seq)
    }

    @SuppressLint("CheckResult")
    fun getMarkerImage(seq: Int){
        retrofitRepository.getMarkerImage(seq).subscribe({
            Log.d(TAG, "getMarkerImage 성공")
            Log.d(TAG, it.toString())
            _markerImage.postValue(BitmapFactory.decodeStream((it.byteStream())))
            //_markerImage.postValue(decodeSampledBitmapFromResource(it.byteStream(), 400, 400))
        },{
            Log.d(TAG, "getMarkerImage 오류 = ${it}")
            val bitmap = resource.getDrawable(R.drawable.ic_default_image, null).toBitmap()
            _markerImage.postValue(Bitmap.createBitmap(bitmap))
        })
    }

    @SuppressLint("CheckResult")
    fun getFirstLiveDataList() {
        retrofitRepository.getDataList().subscribe({
           _firstData.postValue(it)
        }, {
            _firstData.postValue(emptyList())
        })
    }

/*    private fun decodeSampledBitmapFromResource(
        res: InputStream,
        reqWidth: Int,
        reqHeight: Int
    ): Bitmap {
        // First decode with inJustDecodeBounds=true to check dimensions
        return BitmapFactory.Options().run {
            inJustDecodeBounds = true
            val bufferedInputStream = BufferedInputStream(res)

            bufferedInputStream.mark(bufferedInputStream.available())

            BitmapFactory.decodeStream(bufferedInputStream, null, this)

            bufferedInputStream.reset()
            // Calculate inSampleSize
            inSampleSize = calculateInSampleSize(this, reqWidth, reqHeight)
            // Decode bitmap with inSampleSize set
            inJustDecodeBounds = false

            BitmapFactory.decodeStream(bufferedInputStream, null, this)
        }!!
    }
    private fun calculateInSampleSize(options: BitmapFactory.Options, reqWidth: Int, reqHeight: Int): Int {
        // Raw height and width of image
        val (height: Int, width: Int) = options.run { outHeight to outWidth }
        var inSampleSize = 1

        if (height > reqHeight || width > reqWidth) {

            val halfHeight: Int = height / 2
            val halfWidth: Int = width / 2

            // Calculate the largest inSampleSize value that is a power of 2 and keeps both
            // height and width larger than the requested height and width.
            while (halfHeight / inSampleSize >= reqHeight && halfWidth / inSampleSize >= reqWidth) {
                inSampleSize *= 2
            }
        }
        return inSampleSize
    }*/
}