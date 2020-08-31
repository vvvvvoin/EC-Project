package com.example.firstkotlinapp.viewModel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class MyViewModel : ViewModel() {
    val searchBarText: MutableLiveData<String> by lazy {
        MutableLiveData<String>()
    }
    fun setSearchBarText(content : String){
        searchBarText.value = content
    }

}
