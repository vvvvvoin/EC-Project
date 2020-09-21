package com.example.firstkotlinapp.dataClass

import com.google.gson.annotations.SerializedName
import java.util.Date

data class BoardVO (
    val  seq: Int,
    val  title: String,
    val  writer: String,
    val  content: String,
    val regDate: String,
    val  cnt: Int
)