package com.example.firstkotlinapp.viewModel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.firstkotlinapp.dataClass.MarkerDataVO

class MyViewModel : ViewModel() {
    val searchBarText: MutableLiveData<String> by lazy {
        MutableLiveData<String>()
    }

    val searchData : MutableLiveData<List<MarkerDataVO>>by lazy {
        MutableLiveData<List<MarkerDataVO>>()
    }

    fun setSearchBarText(content : String){
        searchBarText.value = content

    }

}
