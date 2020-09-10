package com.example.firstkotlinapp.recycler.list

import android.util.SparseBooleanArray
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.firstkotlinapp.R
import com.example.firstkotlinapp.dataClass.MarkerDataVO
import com.example.firstkotlinapp.map.TestActivity


class SearchResultAdapter( var resultList: SearchResultData?,  val testActivity: TestActivity) : RecyclerView.Adapter<RecyclerView.ViewHolder>(){
    val TAG = "SearchHistoryAdapter"
    private val textViewSelectedItems = SparseBooleanArray(0)
    override fun getItemCount(): Int {
        return resultList?.data?.size ?: return 0
    }

    override fun getItemViewType(position: Int): Int {
        return resultList?.viewType ?: return 0
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        //val view = LayoutInflater.from(parent.context).inflate(R.layout.search_result_bottom_sheet_item, parent, false)
        if(viewType == SearchRecyclerViewAdapterViewType().RESULT_BOTTOM_SHEET){
            return SearchResultBottomsheet( LayoutInflater.from(parent.context).inflate(R.layout.search_result_bottom_sheet_item, parent, false))
        }else{ //viewType == SearchRecyclerViewAdapterViewType().RESULT_VIEW_PAGER
            return SearchResultViewPager( LayoutInflater.from(parent.context).inflate(R.layout.search_result_view_pager_item, parent, false))
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when (holder) {
            is SearchResultBottomsheet -> {
                (holder).subjectTextView.text = resultList?.data?.get(position)?.subject
                (holder).contentTextView.text = resultList?.data?.get(position)?.content
                (holder).linearLayout.setOnClickListener {
                    testActivity.moveCameraAndShowBottomSheetViewPager(position, resultList?.data?.get(position)!!
                    )
                }
            }
            is SearchResultViewPager -> {
                (holder).subjectTextView.text = resultList?.data?.get(position)?.subject
                (holder).contentTextView.text = resultList?.data?.get(position)?.content
                (holder).linearLayout.setOnClickListener {
                    testActivity.showBottomSheetDetail(resultList?.data?.get(position)?.lat, resultList?.data?.get(position)?.lng,  resultList?.data?.get(position)
                    )
                }
            }
        }
    }
    fun getItemList() : List<MarkerDataVO> {
        return resultList?.data!!
    }

    fun changeItemList(data : SearchResultData) {
        this.resultList = data
    }

    private inner class SearchResultBottomsheet(itemview : View) : RecyclerView.ViewHolder(itemview){
        val subjectTextView: TextView = itemview.findViewById(R.id.search_result_bottom_sheet_subject_textView)
        val contentTextView: TextView = itemview.findViewById(R.id.search_result_bottom_sheet_content_textView)
        val linearLayout : LinearLayout = itemview.findViewById(R.id.search_result_bottom_sheet_item_layout)
    }

    private inner class SearchResultViewPager(itemview : View) : RecyclerView.ViewHolder(itemview){
        val subjectTextView: TextView = itemview.findViewById(R.id.search_result_view_pager_subject_textView)
        val contentTextView: TextView = itemview.findViewById(R.id.search_result_view_pager_content_textView)
        val linearLayout : LinearLayout = itemview.findViewById(R.id.search_result_view_pager_item_layout)
    }

}