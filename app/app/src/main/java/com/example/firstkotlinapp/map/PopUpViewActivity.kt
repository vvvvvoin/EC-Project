package com.example.firstkotlinapp.map

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.ContextThemeWrapper
import android.view.MotionEvent
import android.view.View
import android.view.Window
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import com.example.firstkotlinapp.R
import com.example.firstkotlinapp.dataClass.MarkerDataVO
import com.example.firstkotlinapp.retrofit.OkHttpManager
import kotlinx.android.synthetic.main.marker_popup_view.*


class PopUpViewActivity : AppCompatActivity() {
    val TAG = "PopUpViewActivity"

    private lateinit var viewData : MarkerDataVO
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        supportRequestWindowFeature(Window.FEATURE_NO_TITLE)
        setContentView(R.layout.marker_popup_view)
        var checkModified : Boolean = false
       viewData = intent.getSerializableExtra("view_data") as MarkerDataVO
        popup_title_textView.text = viewData.title
        popup_writer_textView.text = viewData.writer
        popup_content_textView.text = viewData.snippet
        popup_address_textView.text = viewData.address

        //OkHttpManager().getImage(viewData.seq ,popup_View_imageView)

        back.setOnClickListener {
            val intent = Intent(this, TestActivity::class.java)
            if(!checkModified) {
                setResult(5006, intent)
            }else{
                setResult(5007, intent)
            }
            finish()
        }

        delete.setOnClickListener {
            val alertDialog = AlertDialog.Builder(ContextThemeWrapper(this, R.style.Theme_AppCompat_Light_Dialog))
            alertDialog.setTitle("마커삭제")
            alertDialog.setMessage("해당마커를 삭제할까요?")
            alertDialog.setPositiveButton("확인") { dialog, id ->
                Log.d(TAG, "마커삭제")
                val intent = Intent(this, TestActivity::class.java)
                intent.putExtra("delete_data", viewData)
                setResult(5008, intent)
                finish()
                dialog.dismiss()
            }
            alertDialog.setNegativeButton("취소") { dialog, id ->
                dialog.dismiss()
            }
            alertDialog.show()
        }


        if(TestActivity().getWifiInfo(this)) {
            modified.setOnClickListener {
                popup_title_textView.visibility = View.GONE
                popup_content_textView.visibility = View.GONE
                modified.visibility = View.GONE
                popup_title_transform.visibility = View.VISIBLE
                popup_content_transform.visibility = View.VISIBLE
                confirm.visibility = View.VISIBLE

                popup_title_transform.append(popup_title_textView.text)
                popup_content_transform.append(popup_content_textView.text)

                confirm.setOnClickListener {
                    viewData.subject = popup_title_transform.text.toString()
                    viewData.content = popup_content_transform.text.toString()

                    OkHttpManager().updateData(viewData.seq, viewData.subject, viewData.content)
                    checkModified = true
                    Log.d(TAG, viewData.toString())
                    popup_title_textView.text = viewData.subject
                    popup_content_textView.text = viewData.content
                    popup_title_textView.visibility = View.VISIBLE
                    popup_content_textView.visibility = View.VISIBLE
                    modified.visibility = View.VISIBLE
                    popup_title_transform.visibility = View.GONE
                    popup_content_transform.visibility = View.GONE
                    confirm.visibility = View.GONE
                }
            }
        }else{
            modified.isClickable = false
            modified.setOnClickListener {
                Toast.makeText(this, "서버와 연결이 안돼 수정할 수 없습니다.", Toast.LENGTH_SHORT).show()
            }
        }
    }

    override fun onTouchEvent(event: MotionEvent): Boolean {
        //팝업 외부 클릭 금지
        if(event.action == MotionEvent.ACTION_OUTSIDE){
            return false;
        }
        return true;
    }

    override fun onBackPressed() {
        //백버튼 막기
        return
    }
}