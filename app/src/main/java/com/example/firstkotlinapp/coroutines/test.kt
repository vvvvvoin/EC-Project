package com.example.firstkotlinapp.coroutines

fun main() {
    // KotlinTest
    val list  = mutableListOf<String>("a", "b", "c", "d", "e")
    list[0] = list.get(0) + list.get(1)
    print(list)
}
