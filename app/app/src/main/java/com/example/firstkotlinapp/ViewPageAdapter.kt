package com.example.firstkotlinapp

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class ViewPageAdapter(val text : List<String>) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.viewpager_item, parent, false)
        return ViewPagerViewHolder(view)
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
       when(holder){
           is ViewPagerViewHolder -> {
               (holder).textView.setText(text[position])
               (holder).bind(position)
           }
       }

    }

    override fun getItemCount(): Int {
        return text.size
    }

    inner class ViewPagerViewHolder(itemview : View) : RecyclerView.ViewHolder(itemview){
        val textView = itemview.findViewById<TextView>(R.id.viewpager_tv)

        fun bind(position: Int){
            textView.append("\n this is RecyclerViewAdapter ${position} page")
        }
    }

}



/*태스트를 위한 클래스*/
/*class ViewPageAdapter(val text : List<String>) : RecyclerView.Adapter<ViewPageAdapter.ViewPagerViewHolder>() {
    inner class ViewPagerViewHolder(itemview : View) : RecyclerView.ViewHolder(itemview)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewPagerViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.viewpager_item, parent, false)
        return ViewPagerViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewPagerViewHolder, position: Int) {
        val currentText = text[position]
        holder.itemView.viewpager_tv.setText(currentText)
    }

    override fun getItemCount(): Int {
        return text.size
    }
}*/