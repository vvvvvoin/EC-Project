package com.example.firstkotlinapp.map.customView

import android.content.Context
import android.util.AttributeSet
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.*
import com.example.firstkotlinapp.R
import com.example.firstkotlinapp.map.fragment.SearchFragment

class SearchBar02CustomView(context: Context, attrs: AttributeSet) : LinearLayout(context, attrs){
    val TAG = "SearchCustom"
    private var search_editText: EditText
    private var search_back_arrow_button : ImageView
    private var search_layout : LinearLayout

    init {
        val inflater = context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        val view = inflater.inflate(R.layout.custom_search_bar02, this, true)
        search_editText = view.findViewById(R.id.search_editText)
        search_layout = view.findViewById(R.id.search_layout)
        search_back_arrow_button = view.findViewById(R.id.search_back_arrow_button)
        val searchFragment = SearchFragment()

    }

    fun getSearchTextView() : EditText{
        return this.search_editText
    }

    fun getBackArrowButton() : ImageView{
        return this.search_back_arrow_button
    }

    fun getLayout() : LinearLayout{
        return this.search_layout
    }
}
