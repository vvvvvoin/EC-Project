package com.example.firstkotlinapp.recycler.list

import android.content.Context
import android.graphics.BitmapFactory
import android.net.Uri
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.firstkotlinapp.R
import com.google.android.flexbox.FlexboxLayout
import kotlinx.android.synthetic.main.marker_popup_edit.*
import kotlinx.android.synthetic.main.search_list_bottom_sheet_detail03.*


class MarkerEditImageAdapter(private var imageUriList: ArrayList<Uri>?, val context : Context) : RecyclerView.Adapter<RecyclerView.ViewHolder>(){
    val TAG = "MarkerEditImageAdapter"


    override fun getItemCount(): Int {
        return imageUriList?.size ?:  return 0
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
            return MarkerImageItem(LayoutInflater.from(parent.context).inflate(R.layout.marker_edit_image_item, parent, false))
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when (holder) {
            is MarkerImageItem -> {
                for(image in imageUriList!!){
                    val input  = context.contentResolver?.openInputStream(image)
                    val bitmap = BitmapFactory.decodeStream(input)
                    Glide.with(context).load(bitmap).into(holder.imageView)
                }
            }
        }

    }

    inner class MarkerImageItem(itemview : View) : RecyclerView.ViewHolder(itemview){
        val itemLayout : FlexboxLayout = itemview.findViewById(R.id.marker_edit_layout)
        val imageView : ImageView = itemView.findViewById(R.id.marker_edit_imageView)
    }

    fun changeItemList(data : ArrayList<Uri>?) {
        this.imageUriList = data
    }
}