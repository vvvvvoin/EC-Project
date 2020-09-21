package com.example.firstkotlinapp.map.fragment

import android.app.Activity
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.firstkotlinapp.App
import com.example.firstkotlinapp.R
import com.example.firstkotlinapp.recycler.list.LinearLayoutManagerWrapper
import com.example.firstkotlinapp.recycler.list.SearchHistoryAdapter
import kotlinx.android.synthetic.main.fragment_search_history.*
import java.util.prefs.Preferences

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [SearchHistoryFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class SearchHistoryFragment : Fragment() {
    // TODO: Rename and change types of parameters
    val TAG = "SearchHistoryFragment"
    lateinit var historyAdapter : SearchHistoryAdapter
    private var param1: String? = null
    private var param2: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        Log.d(TAG, "onCreate")
        super.onCreate(savedInstanceState)
        arguments?.let {
            param1 = it.getString(ARG_PARAM1)
            param2 = it.getString(ARG_PARAM2)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        Log.d(TAG, activity?.supportFragmentManager?.backStackEntryCount.toString())
        Log.d(TAG, childFragmentManager.backStackEntryCount.toString())
        val view = inflater.inflate(R.layout.fragment_search_history, container, false)
        val recyclerView = view.findViewById<RecyclerView>(R.id.search_history_recyclerView)
        val deleteButton : Button = view.findViewById(R.id.search_history_delete_button)
        val selectAllButton : Button = view.findViewById(R.id.search_history_item_allselect_button)

        //val testHistoryList = App.prefs.getHistoryList()

        val testHistoryList = arrayListOf<String>(
            "text1",
            "text2",
            "text3",
            "text4",
            "text5",
            "text6",
            "text7",
            "text8",
            "text9",
            "text10",
            "text11",
            "text12",
            "text13",
            "text14",
            "text15",
            "text16",
            "text17",
            "text18",
            "text19"
        )

        //val linearLayoutManager = LinearLayoutManager(container?.context, LinearLayoutManager.VERTICAL, false)
        val linearLayoutManager = LinearLayoutManagerWrapper(context!!, LinearLayoutManager.VERTICAL, false)

        val dividerItemDecoration = DividerItemDecoration(context, linearLayoutManager.orientation)
        val dividerDrawable = context?.resources?.getDrawable(R.drawable.search_history_recyclerview_divider, null)
        dividerItemDecoration.setDrawable(dividerDrawable!!) // !! 안쓰고 해결할 방법좀 찾아보자
        if(testHistoryList != null){
            historyAdapter = SearchHistoryAdapter(testHistoryList)
            recyclerView.adapter = historyAdapter
        }
        recyclerView.layoutManager = linearLayoutManager
        recyclerView.setHasFixedSize(true)
        //recyclerView.addItemDecoration(dividerItemDecoration)

        val showHistoryButton : TextView = view.findViewById(R.id.search_history_fragment_delete)
        /*deleteButton .setOnClickListener {
            activity?.supportFragmentManager?.beginTransaction()?.apply {
                val fragmentItemList = SearchHistoryListFragment.newInstance(testHistoryList)
                replace(R.id.search_history_fragment_frameLayout, fragmentItemList).
                addToBackStack(null).
                commit()
            }
        }*/
        showHistoryButton .setOnClickListener {
            showHistoryButton.visibility =  View.GONE
            deleteButton.visibility = View.VISIBLE
            selectAllButton.visibility = View.VISIBLE
        }
        selectAllButton.setOnClickListener {
            historyAdapter.setCheckAll()
        }
        deleteButton.setOnClickListener {
            historyAdapter.deleteIterm()
        }

        return view
    }

    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment SearchHistoryFragment.
         */
// TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            SearchHistoryFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }
}