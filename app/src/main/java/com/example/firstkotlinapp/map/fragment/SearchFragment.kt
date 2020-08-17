package com.example.firstkotlinapp.map.fragment

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import androidx.activity.OnBackPressedCallback
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.widget.ViewPager2
import com.example.firstkotlinapp.MainActivity
import com.example.firstkotlinapp.R
import com.example.firstkotlinapp.map.TestActivity
import com.example.firstkotlinapp.map.customView.SearchBar02CustomView
import com.example.firstkotlinapp.recycler.fragment.FragmentAdapter
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator


// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [SearchFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class SearchFragment : Fragment(){
    val TAG = "SearchFragment"
    private lateinit var callback: OnBackPressedCallback

    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
//            param1 = it.getString(ARG_PARAM1)
//            param2 = it.getString(ARG_PARAM2)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_search, container, false)
        val searchCategoryFragment = SearchCatecoryFragment.newInstance("1", "2")
        val searchHistoryFragment = SearchHistoryFragment.newInstance("3", "4")
        val searchFragmentList = arrayListOf<Fragment>(searchHistoryFragment, searchCategoryFragment)
        val fragmentAdapter =FragmentAdapter(childFragmentManager, lifecycle, searchFragmentList)
        val viewPager = view.findViewById<ViewPager2>(R.id.search_viewPager).apply {
            this.adapter = fragmentAdapter
            this.isSaveEnabled = false  //stackOverFlow : https://stackoverflow.com/questions/59486504/fragment-no-longer-exists-for-key-fragmentstateadapter-with-viewpager2
        }

        val tablayout  = view.findViewById<TabLayout>(R.id.search_tabLayout)
        TabLayoutMediator(tablayout, viewPager,
            TabLayoutMediator.TabConfigurationStrategy { tab, position ->
                when (position) {
                    0 -> tab.text = "검색기록"
                    1 -> tab.text = "카테고리"
                    //1 -> tab.customView = createTabView("Cust_Tab2")
                }
            }).attach()

        val searchCustom02 = view.findViewById<SearchBar02CustomView>(R.id.searchCustom02)
        searchCustom02.getBackArrowButton().setOnClickListener {
            childFragmentManager.popBackStack()
            activity?.onBackPressed()
        }

        val searchEditText = view.findViewById<EditText>(R.id.search_editText)
        val inputMethodManager = container?.context?.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        inputMethodManager.showSoftInput(searchEditText, 2)

        return view
    }

    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment SearchFragment.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance() =
            SearchFragment().apply {
                arguments = Bundle().apply {
//                    putString(ARG_PARAM1, param1)
                }
            }
    }


}