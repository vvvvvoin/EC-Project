package com.example.firstkotlinapp.map.customView

import android.content.Context
import android.util.AttributeSet
import android.util.Log
import android.view.LayoutInflater
import android.view.MenuItem
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import com.example.firstkotlinapp.R
import com.google.android.material.bottomsheet.BottomSheetBehavior

class MarkerEditCategoryButton (context: Context, attrs: AttributeSet) : LinearLayout(context, attrs){
    private var layout : LinearLayout
    private var textView: TextView

    init {
        val inflater = context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        val view = inflater.inflate(R.layout.custom_marker_edit_ic_category, this, true)

        layout = view.findViewById(R.id.custom_marker_edit_ic_category_layout)
        textView = view.findViewById(R.id.custom_marker_edit_ic_category_textView)
    }
    fun getMarkerEditCategoryButtonLayout() : LinearLayout{
        return this.layout
    }


}