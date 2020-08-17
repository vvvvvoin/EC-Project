package com.example.firstkotlinapp.map

import android.app.Activity
import android.content.Intent
import android.graphics.BitmapFactory
import android.net.Uri

import android.os.Bundle
import android.os.Environment
import android.util.Log
import android.view.MotionEvent
import android.view.Window
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.firstkotlinapp.R
import com.example.firstkotlinapp.dataClass.MarkerDataVO
import com.google.android.gms.maps.model.LatLng
import kotlinx.android.synthetic.main.marker_popup_edit.*


class PopUpEditActivity : AppCompatActivity() {
    private val TAG = "PopUpEditActivity"
    private val OPEN_GALLERY = 6000

    private var selectImage: Uri? = null
    private var filePath : String? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        supportRequestWindowFeature(Window.FEATURE_NO_TITLE)
        setContentView(R.layout.marker_popup_edit)

        val lat  = intent.getDoubleExtra("lat", 0.0)
        val lng = intent.getDoubleExtra("lng", 0.0)

        register.setOnClickListener {
            val title = popup_title_editText.text.toString()
            val content = popup_content_editText.text.toString()
            val writer = popup_writer_editText.text.toString()
            val address = GeoCoder().getAddress(this, LatLng(lat, lng))
            val data = MarkerDataVO(0, title, content, lat, lng, writer, address)
            val intent = Intent(this, TestActivity::class.java)
            intent.putExtra("marker_data", data)
            if(selectImage != null){
                intent.putExtra("file_path", selectImage)
                setResult(5003, intent)
                finish()
            }else{
                setResult(5001, intent)
                finish()
            }

        }
        cancel.setOnClickListener {
            val intent = Intent(this, TestActivity::class.java)
            setResult(5002, intent)
            finish()
        }

        popup_imageUpload_button.setOnClickListener {
            val intent = Intent(Intent.ACTION_OPEN_DOCUMENT)
            intent.setType("image/*")
            //다수선택 허용
            //intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true)
            startActivityForResult(Intent.createChooser(intent, "Select Picture"), OPEN_GALLERY)
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        when(requestCode){
            OPEN_GALLERY -> let{
                Log.d(TAG, "onActivityResult")
                if(resultCode == Activity.RESULT_OK){
                    selectImage = data?.data
                    val input = selectImage?.let {
                        contentResolver.openInputStream(it)
                    }

                    val bitmap = BitmapFactory.decodeStream(input)
                    popup_imageView.setImageBitmap(bitmap)

                    Log.d(TAG, "uri = ${selectImage.toString()}")
                    Log.d(TAG, "input.toString() = ${input.toString()}")
                    Log.d(TAG, "path : ${selectImage?.path}")

                    val path = this.filesDir.absolutePath
                    Log.d(TAG, "path : ${path}")


                }else if(resultCode == Activity.RESULT_CANCELED){
                    Toast.makeText(this, "취소함", Toast.LENGTH_SHORT).show()
                }else{
                    Toast.makeText(this, "오류 발생", Toast.LENGTH_SHORT).show()
                }
            }

        }
    }


    override fun onTouchEvent(event: MotionEvent): Boolean {
        //팝업 외부 클릭 금지
        if(event.action ==MotionEvent.ACTION_OUTSIDE){
            return false;
        }
        return true;
    }

    override fun onBackPressed() {
        //백버튼 막기
        return
    }
}