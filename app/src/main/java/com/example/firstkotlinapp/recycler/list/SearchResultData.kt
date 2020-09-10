package com.example.firstkotlinapp.recycler.list

import com.example.firstkotlinapp.dataClass.MarkerDataVO
import java.io.Serializable

data class SearchResultData (
    val viewType : Int,
    var data : List<MarkerDataVO>
): Serializable