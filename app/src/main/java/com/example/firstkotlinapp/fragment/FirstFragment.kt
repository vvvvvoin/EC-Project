package com.example.firstkotlinapp.fragment

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import com.example.firstkotlinapp.R

class FirstFragment : Fragment() {
    // TODO: Rename and change types of parameters
    private var param1: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            param1 = it.getString(RETROFIT_DATA)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_first, container, false)
        val retrofit_tv = view.findViewById<TextView>(R.id.first_fragment_tv)
        retrofit_tv.text = this.param1

        return view
    }

    companion object {
        private const val RETROFIT_DATA = ""

        @JvmStatic
        fun newInstance(param1: String) =
            FirstFragment().apply {
                arguments = Bundle().apply {
                    putString(RETROFIT_DATA, param1)
                }
            }
    }
}