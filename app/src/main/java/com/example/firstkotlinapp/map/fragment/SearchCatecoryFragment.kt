package com.example.firstkotlinapp.map.fragment

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.firstkotlinapp.R
import com.example.firstkotlinapp.recycler.list.SearchCategoryAdapter
import com.example.firstkotlinapp.recycler.list.SearchCategoryData
import com.example.firstkotlinapp.recycler.list.SearchRecyclerViewAdapterViewType
import com.google.android.flexbox.FlexboxLayoutManager

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [SearchCatecoryFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class SearchCatecoryFragment : Fragment() {
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
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
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_search_catecory, container, false)

        val categoryList = ArrayList<SearchCategoryData>()
        categoryList.add(SearchCategoryData(SearchRecyclerViewAdapterViewType().CATEGORY_TYPE, "즐겨찾기"))
        categoryList.add(SearchCategoryData(SearchRecyclerViewAdapterViewType().CATEGORY_ELEMENT, "집"))
        categoryList.add(SearchCategoryData(SearchRecyclerViewAdapterViewType().CATEGORY_ELEMENT, "음식"))
        categoryList.add(SearchCategoryData(SearchRecyclerViewAdapterViewType().CATEGORY_ELEMENT, "영화관"))
        categoryList.add(SearchCategoryData(SearchRecyclerViewAdapterViewType().CATEGORY_ELEMENT, "약국"))
        categoryList.add(SearchCategoryData(SearchRecyclerViewAdapterViewType().CATEGORY_ELEMENT, "운동"))
        categoryList.add(SearchCategoryData(SearchRecyclerViewAdapterViewType().CATEGORY_TYPE, "음식"))
        categoryList.add(SearchCategoryData(SearchRecyclerViewAdapterViewType().CATEGORY_ELEMENT, "한식요리"))
        categoryList.add(SearchCategoryData(SearchRecyclerViewAdapterViewType().CATEGORY_ELEMENT, "양식"))
        categoryList.add(SearchCategoryData(SearchRecyclerViewAdapterViewType().CATEGORY_ELEMENT, "중식"))
        categoryList.add(SearchCategoryData(SearchRecyclerViewAdapterViewType().CATEGORY_ELEMENT, "일식"))
        categoryList.add(SearchCategoryData(SearchRecyclerViewAdapterViewType().CATEGORY_TYPE, "엔터테이먼트"))
        categoryList.add(SearchCategoryData(SearchRecyclerViewAdapterViewType().CATEGORY_ELEMENT, "영화관"))
        categoryList.add(SearchCategoryData(SearchRecyclerViewAdapterViewType().CATEGORY_ELEMENT, "게임방"))
        categoryList.add(SearchCategoryData(SearchRecyclerViewAdapterViewType().CATEGORY_ELEMENT, "놀이"))

        val  categoryAdapter = SearchCategoryAdapter(categoryList, context)
        val recyclerView : RecyclerView = view.findViewById(R.id.search_category_recyclerView)
        val layoutManager = FlexboxLayoutManager(context)
//        layoutManager.flexDirection = FlexDirection.ROW
//        layoutManager.justifyContent = JustifyContent.FLEX_END

        recyclerView.adapter = categoryAdapter
        recyclerView.layoutManager = layoutManager
        recyclerView.setHasFixedSize(true)

        return view
    }

    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment SearchCatecoryFragment.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            SearchCatecoryFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }
}