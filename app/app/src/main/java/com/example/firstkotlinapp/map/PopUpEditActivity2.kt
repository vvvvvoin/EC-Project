package com.example.firstkotlinapp.map

import android.app.Activity
import android.content.Intent
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.firstkotlinapp.R
import com.example.firstkotlinapp.recycler.list.LinearLayoutManagerWrapper
import com.example.firstkotlinapp.recycler.list.MarkerEditImageAdapter
import kotlinx.android.synthetic.main.activity_pop_up_edit2.*
import kotlinx.android.synthetic.main.marker_popup_edit.*

class PopUpEditActivity2 : AppCompatActivity() {
    private val TAG = "PopUpEditActivity2"
    private val OPEN_GALLERY = 6000
    private var imageList : ArrayList<Uri>? = null
    private var selectImage: Uri? = null

    private lateinit var imageRecyclerView : RecyclerView
    private lateinit var recyclerViewAdapter : MarkerEditImageAdapter
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_pop_up_edit2)

        setSupportActionBar(marker_edit_toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setDisplayShowTitleEnabled(false)

        imageRecyclerView = findViewById<RecyclerView>(R.id.popup_edit_image_recyclerview)

        val linearLayoutManager = LinearLayoutManagerWrapper(applicationContext, LinearLayoutManager.HORIZONTAL, false)
        imageRecyclerView.layoutManager = linearLayoutManager
        imageRecyclerView.setHasFixedSize(true)

        recyclerViewAdapter = MarkerEditImageAdapter(null, this)
        imageRecyclerView.adapter



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
        when(requestCode){
            OPEN_GALLERY -> let{
                if(resultCode == Activity.RESULT_OK){
                    // 다수의 uri 리스트에 넣기

                    //imageList.add()


                    // 바뀐 이미지리스트 업데이트 시키기
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
                Log.d(TAG, "뒤로가기 버튼 눌림")
                finish()
                return true
            }
        }
        return super.onOptionsItemSelected(item)
    }
}