package com.example.firstkotlinapp.recycler.list

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import androidx.core.graphics.drawable.toBitmap
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.Glide.with
import com.bumptech.glide.Priority
import com.example.firstkotlinapp.R
import com.example.firstkotlinapp.util.MyGlideApp
import com.google.android.flexbox.FlexboxLayout
import kotlinx.android.synthetic.main.marker_popup_edit.*
import kotlinx.android.synthetic.main.search_list_bottom_sheet_detail03.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext


class MarkerViewImageAdapter(var imageBitmapList: ArrayList<Bitmap?>?, val context : Context) : RecyclerView.Adapter<RecyclerView.ViewHolder>(){
    val TAG = "MarkerViewImageAdapter"
    private var cnt : Int = 0
    override fun getItemCount(): Int {
        return imageBitmapList?.size ?:  return 0
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
            return MarkerImageItem(LayoutInflater.from(parent.context).inflate(R.layout.marker_view_image_item, parent, false))
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when (holder) {
            is MarkerImageItem -> {
                if(imageBitmapList?.get(position) == null){
                    Glide.with(context).load(R.drawable.ic_loading).into((holder).imageView)
                }else{
                    Glide.with(context).load(imageBitmapList?.get(position)).into((holder).imageView)
                }
            }
        }
    }

    fun setListSize(size : Int){
        for(i in 0 until size){
            imageBitmapList?.add(null)
        }
        notifyDataSetChanged()
    }

    fun addImageItem(bitmap: Bitmap){
        imageBitmapList?.set(cnt, bitmap)
        notifyItemChanged(cnt)
        cnt++
    }

    fun clearList(){
        cnt = 0
        this.imageBitmapList?.clear()
        notifyDataSetChanged()
    }

    inner class MarkerImageItem(itemview : View) : RecyclerView.ViewHolder(itemview){
        val itemLayout : LinearLayout = itemview.findViewById(R.id.marker_view_layout)
        val imageView : ImageView = itemView.findViewById(R.id.marker_view_imageView)
    }

}