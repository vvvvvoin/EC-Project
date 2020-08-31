package com.example.firstkotlinapp.map.customView

import android.content.Context
import android.util.AttributeSet
import android.util.Log
import android.view.LayoutInflater
import android.widget.*
import com.example.firstkotlinapp.R
import com.example.firstkotlinapp.map.fragment.SearchFragment

class SearchBar01CustomView(context: Context, attrs: AttributeSet) : LinearLayout(context, attrs){
    val TAG = "SearchCustom"
    private var search_textView: TextView
    private var hambuger_button : ImageView
    private var search_layout : LinearLayout
    private var search_x_button : ImageView

    init {
        val inflater = context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        val view = inflater.inflate(R.layout.custom_search_bar01, this, true)

        search_textView = view.findViewById(R.id.search_textView)
        search_layout = view.findViewById(R.id.search_layout)
        hambuger_button = view.findViewById(R.id.search_hambuger_button)
        search_x_button = view.findViewById(R.id.search_x_button)
        val searchFragment = SearchFragment()

        search_layout.setOnClickListener {
            Toast.makeText(context, "${context.toString()}, ${context.packageCodePath}", Toast.LENGTH_SHORT).show()
            Log.d(TAG, "${context.toString()}, ${context.packageCodePath}")
        }

    }

    fun getSearchXbtn() : ImageView {
        return this.search_x_button
    }

    fun getSearchTextView() : TextView{
        return this.search_textView
    }

    fun getHambugerButton() : ImageView{
        return this.hambuger_button
    }
}
