package com.example.firstkotlinapp.recycler.list

import java.io.Serializable


data class SearchCategoryData(
    var viewType: Int = 0,
    var value: String = ""
    ): Serializable


