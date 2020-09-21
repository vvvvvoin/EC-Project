package com.example.firstkotlinapp.map.customView

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import com.example.firstkotlinapp.R

class SearchIcMapCustomView (context: Context, attrs: AttributeSet) : LinearLayout(context, attrs){
    private var layout : LinearLayout
    private var textView: TextView
    private var imageView : ImageView

    init {
        val inflater = context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        val view = inflater.inflate(R.layout.custom_search_ic_map, this, true)

        layout = view.findViewById(R.id.custom_search_ic_map_layout)
        textView = view.findViewById(R.id.custom_search_ic_map_textview)
        imageView = view.findViewById(R.id.custom_search_ic_map_imageview)
    }
    fun getSearchIcMapLayout() : LinearLayout{
        return this.layout
    }
}