package com.example.firstkotlinapp.recycler.list

import android.content.Context
import android.util.Log
import android.util.SparseBooleanArray
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.core.util.putAll
import androidx.recyclerview.widget.RecyclerView
import com.example.firstkotlinapp.R
import com.google.android.flexbox.FlexDirection
import com.google.android.flexbox.FlexWrap
import com.google.android.flexbox.FlexboxLayout
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch


class SearchCategoryAdapter(val categoryList : ArrayList<SearchCategoryData>, val context : Context?) : RecyclerView.Adapter<RecyclerView.ViewHolder>(){
    val TAG = "SearchCategoryAdapter"

    private val textViewSelectedItems = SparseBooleanArray(0)
    override fun getItemCount(): Int {
        return categoryList.size
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.search_history_item, parent, false)
        if(viewType == SearchCategoryViewType().CATEGORY_TYPE){
            return SearchCategoryType(LayoutInflater.from(parent.context).inflate(R.layout.search_category_item, parent, false))
        }else { //if(viewType == SearchCategoryViewType().CATEGORY_ELEMENT)
            return SearchCategoryElement(LayoutInflater.from(parent.context).inflate(R.layout.search_category_item2, parent, false))
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when (holder) {
            is SearchCategoryType -> {
                (holder).textView.text = categoryList.get(position).value
            }
            is SearchCategoryElement ->{
                (holder).textView.text = categoryList.get(position).value
                (holder).imageView.let {
                    if(categoryList.get(position).value.contains("음식")){
                        it.setImageResource(R.drawable.ic_search_category_food)
                    }else if(categoryList.get(position).value.contains("영화")){
                        it.setImageResource(R.drawable.ic_search_category_cinema)
                    }
                }
            }
        }
    }

    override fun getItemViewType(position: Int): Int {
        return categoryList.get(position).viewType
    }

    inner class SearchCategoryType(itemview : View) : RecyclerView.ViewHolder(itemview){
        val textView: TextView = itemview.findViewById(R.id.search_history_category_type_textView)
        val imageView : ImageView = itemView.findViewById(R.id.search_history_category_type_imageview)
        val itemLayout : FlexboxLayout = itemview.findViewById(R.id.search_history_category_type_layout)

    }

    inner class SearchCategoryElement(itemview : View) : RecyclerView.ViewHolder(itemview){
        val textView: TextView = itemview.findViewById(R.id.search_history_category_element_textView)
        val imageView : ImageView = itemView.findViewById(R.id.search_history_category_element_imageview)
        val itemLayout : FlexboxLayout = itemview.findViewById(R.id.search_history_category_element_layout)

        init {
            textView.setOnClickListener {
                Toast.makeText(context, "선택한 카테고리는 ${textView.text} 입니다", Toast.LENGTH_SHORT).show()
            }

        }
    }
}