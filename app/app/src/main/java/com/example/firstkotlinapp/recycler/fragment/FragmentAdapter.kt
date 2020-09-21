package com.example.firstkotlinapp.recycler.fragment

import android.os.Parcelable
import android.util.Log
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.Lifecycle
import androidx.viewpager2.adapter.FragmentStateAdapter
import androidx.viewpager2.adapter.FragmentViewHolder
import com.example.firstkotlinapp.R
import com.example.firstkotlinapp.fragment.FirstFragment


class FragmentAdapter(
                    fragmentManager: FragmentManager,
                    lifecycle: Lifecycle,
                    list: List<Fragment>) : FragmentStateAdapter(fragmentManager, lifecycle) {
    private val TAG = "FragmentAdapter"
    private var listFragment: List<Fragment> = ArrayList()
    private val fm = fragmentManager


    init {
        this.listFragment = list
    }


    override fun getItemCount(): Int {
        return listFragment.size
    }

    override fun createFragment(position: Int): Fragment {
        return listFragment[position]
    }

    override fun onBindViewHolder(
        holder: FragmentViewHolder,
        position: Int,
        payloads: MutableList<Any>
    ) {
        super.onBindViewHolder(holder, position, payloads)
    }

}


/*
class FragmentAdapter(fragmentList: List<Fragment>) : FragmentStateAdapter(FragmentActivity()) {
    private val FRAGMENT_SIZE = 3

    override fun getItemCount(): Int {
        return this.FRAGMENT_SIZE
    }

    override fun createFragment(position: Int): Fragment {
         when (position) {
            0 ->  FirstFragment()
            1 ->  SecondFragment()
            2 ->  ThirdFragment()
        }
        return FirstFragment()
    }
}*/

/*
class FragmentAdapter(
    fa: FragmentActivity?,
    list: List<Fragment>
) :
    FragmentStateAdapter(fa!!) {
    private var listFragment: List<Fragment> = ArrayList()

    override fun createFragment(position: Int): Fragment {
        return listFragment[position]
    }

    override fun getItemCount(): Int {
        return 2
    }

    init {
        listFragment = list
    }
}*/
