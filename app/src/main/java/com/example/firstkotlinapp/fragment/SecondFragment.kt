package com.example.firstkotlinapp.fragment

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import com.example.firstkotlinapp.R
import com.example.firstkotlinapp.retrofit.OkHttpManager
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers

private const val ARG_PARAM1 = "param1"

class SecondFragment : Fragment() {
    private val TAG = "SecondFragment"
    private var param1: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            param1 = it.getString(ARG_PARAM1)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_second, container, false)
        val text = view.findViewById<EditText>(R.id.title_editText).text.toString()
        val writer = view.findViewById<EditText>(R.id.writer_editText).text.toString()
        val content = view.findViewById<EditText>(R.id.content_editText).text.toString()

        /*view.findViewById<Button>(R.id.upload_button).setOnClickListener {
            OkHttpManager.KotlinOKHttpRetrofitRxJavaManager.getInstance().setData(text, writer, content)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({
                    Log.d(TAG, "성공")
                    //Log.d(TAG, it.toString())
                }, {
                    Log.d(TAG, "성공")
                    //Log.d(TAG, it.toString())
                })
        }*/
        return view
    }

    companion object {

        @JvmStatic
        fun newInstance(param1: String) =
            ThirdFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                }
            }
    }
}