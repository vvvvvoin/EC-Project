package com.example.firstkotlinapp.map

import android.R.attr
import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.firstkotlinapp.R
import com.example.firstkotlinapp.dataClass.MarkerDataVO
import com.example.firstkotlinapp.recycler.list.LinearLayoutManagerWrapper
import com.example.firstkotlinapp.recycler.list.MarkerEditImageAdapter
import com.google.android.gms.maps.model.LatLng
import com.google.firebase.auth.FirebaseAuth
import kotlinx.android.synthetic.main.activity_pop_up_edit2.*


class PopUpEditActivity2 : AppCompatActivity() {
    private val TAG = "PopUpEditActivity2"
    private val OPEN_GALLERY = 6000
    private var selectImage: Uri? = null

    private lateinit var auth : FirebaseAuth

    private var imageList = arrayListOf<Uri>()
    private lateinit var imageRecyclerView : RecyclerView
    private lateinit var recyclerViewAdapter : MarkerEditImageAdapter

    private var lat : Double = 0.0
    private var lng : Double = 0.0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_pop_up_edit2)

        auth = FirebaseAuth.getInstance()

        setSupportActionBar(marker_edit_toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setDisplayShowTitleEnabled(false)

        lat  = intent.getDoubleExtra("lat", 0.0)
        lng = intent.getDoubleExtra("lng", 0.0)


        imageRecyclerView = findViewById<RecyclerView>(R.id.popup_edit_image_recyclerview)
        val linearLayoutManager = LinearLayoutManagerWrapper(applicationContext, LinearLayoutManager.HORIZONTAL, false)
        imageRecyclerView.layoutManager = linearLayoutManager
        imageRecyclerView.setHasFixedSize(true)

        recyclerViewAdapter = MarkerEditImageAdapter(null, this)
        imageRecyclerView.adapter = recyclerViewAdapter



        popup_edit_image_button.setOnClickListener {
            val intent = Intent(Intent.ACTION_OPEN_DOCUMENT)

            intent. setDataAndType(android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI, "image/*")
            //다수선택 허용
            intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true)
            startActivityForResult(intent, OPEN_GALLERY)
        }

    }
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        when (requestCode) {
            OPEN_GALLERY -> let {
                if (resultCode == Activity.RESULT_OK ) {
                    imageList.clear()
                    if(data?.data != null){
                        imageList.add(data.data!!)
                    }else{
                        for (i in 0 until data?.clipData?.itemCount!!) {
                            imageList.add(data.clipData?.getItemAt(i)?.uri!!)
                        }
                    }
                    recyclerViewAdapter.changeItemList(imageList)
                    recyclerViewAdapter.notifyDataSetChanged()
                }else if(resultCode == Activity.RESULT_CANCELED){
                    Toast.makeText(this, "취소함", Toast.LENGTH_SHORT).show()
                }else{
                    Toast.makeText(this, "오류 발생", Toast.LENGTH_SHORT).show()
                }
            }

        }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.marker_edit_menu, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when(item.itemId){
            android.R.id.home -> {
                finish()
                return true
            }

            R.id.marker_edit_save -> {
                Toast.makeText(this, "저장버튼", Toast.LENGTH_SHORT).show()
                val subject = popup_edit_title_EditText.text.toString()
                val content = popup_edit_content_EditText.text.toString()
                val writer = auth.currentUser?.displayName!!
                val address = GeoCoder().getAddress(this, LatLng(lat, lng))

                val data = MarkerDataVO(0, subject, content, lat, lng, writer, address)
                val intent = Intent(this, TestActivity::class.java)
                intent.putExtra("marker_data", data)

                if(imageList.isNotEmpty()){
                    intent.putExtra("marker_image_list", imageList)
                    setResult(5003, intent)
                    finish()
                }else{
                    setResult(5001, intent)
                    finish()
                }

            }
            R.id.marker_edit_modifiy -> {
                Toast.makeText(this, "수정버튼", Toast.LENGTH_SHORT).show()
            }
            R.id.marker_edit_delete -> {
                Toast.makeText(this, "삭제버튼", Toast.LENGTH_SHORT).show()
            }

        }
        return super.onOptionsItemSelected(item)
    }
}
