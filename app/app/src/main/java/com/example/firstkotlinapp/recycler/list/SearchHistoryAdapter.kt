package com.example.firstkotlinapp.recycler.list

import android.util.Log
import android.util.SparseBooleanArray
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.core.util.putAll
import androidx.recyclerview.widget.RecyclerView
import com.example.firstkotlinapp.R
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch


class SearchHistoryAdapter(val historyList : ArrayList<String>) : RecyclerView.Adapter<RecyclerView.ViewHolder>(){
    val TAG = "SearchHistoryAdapter"
    private val textViewSelectedItems = SparseBooleanArray(0)
    override fun getItemCount(): Int {
        return historyList.size
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.search_history_item, parent, false)

        return ViewPagerViewHolder(view)
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when (holder) {
            is ViewPagerViewHolder -> {
                (holder).textView.text = historyList.get(position)
                if (textViewSelectedItems.get(position)) {
                    (holder).imageView.isSelected = true
                } else {
                    (holder).imageView.isSelected = false
                }

            }
        }
    }
    fun refresh(){
        for(index  in 0..historyList.size){
            notifyItemChanged(index)
        }
        notifyItemRangeChanged(0, this@SearchHistoryAdapter.itemCount)
    }

    fun setCheckAll(){
        for(check  in 0..historyList.size){
            textViewSelectedItems.put(check, true)
        }
        CoroutineScope(Dispatchers.IO).launch {
            notifyItemRangeChanged(0, this@SearchHistoryAdapter.itemCount)
        }
    }

    fun deleteIterm(){
        val list = HashMap<Int, String>()
        for(check  in 0 until historyList.size){
            if(textViewSelectedItems.get(check)){
                list[check] = historyList[check]
            }
        }
        Log.d(TAG, list.toString())
        Log.d(TAG, historyList.toString())
        for(str in list){
            historyList.remove(str.value)
            notifyItemRemoved(str.key)
        }
        textViewSelectedItems.clear()
        CoroutineScope(Dispatchers.IO).launch {
            notifyItemRangeChanged(0, this@SearchHistoryAdapter.itemCount)
        }
    }
    inner class ViewPagerViewHolder(itemview : View) : RecyclerView.ViewHolder(itemview){
        val textView: TextView = itemview.findViewById(R.id.search_history_textView)
        val imageView : ImageView = itemView.findViewById(R.id.search_history_checkbox)
        val itemLayout : LinearLayout = itemview.findViewById(R.id.search_history_item_layout)

        init {
            itemLayout.setOnClickListener {
                val position = bindingAdapterPosition
                if (textViewSelectedItems[position]) {
                    textViewSelectedItems.put(position, false)
                    notifyItemChanged(position)
                } else {
                    //비활성화 된 경우, 거짓이였던 경우
                    textViewSelectedItems.put(position, true)
                    notifyItemChanged(position)
                }
            }
        }

    }
}