package com.example.firstkotlinapp.dataBase

import android.content.Context
import android.content.SharedPreferences
import android.util.Log
import org.json.JSONArray

class MySharedPreferences(context : Context){
    val TAG = "MySharedPreferences"
    val PREFERENCES_NAME = ""
    val PREF_KEY_SEARCH_HISTORY_LIST = "SEARCH_HISTORY"
    val prefs: SharedPreferences = context.getSharedPreferences(PREFERENCES_NAME, 0)

     var jsonArray : JSONArray? = null
     var returnArrayList : ArrayList<String>? = null

    fun getHistoryList() : ArrayList<String>? {
        val prefsData = prefs.getString(PREF_KEY_SEARCH_HISTORY_LIST, "")
        Log.d(TAG, "저장된 데이터는 ${prefsData}")
        if ( prefsData.isNullOrEmpty()) {
            var jsonArray = JSONArray(prefsData)
            for (i in 0..jsonArray.length()) {
                jsonArray.optString(i)?.let { returnArrayList?.add(it) }
            }
            return returnArrayList
        }
        return returnArrayList
    }

    fun addHistoryList(value : String){
        val lateData = getHistoryList()
        if(lateData != null) {
            for (i in 0..lateData.size) {
                jsonArray?.put(lateData.get(i))
            }
        }else{
            Log.d(TAG, "제이슨에 들어갈 value = ${value}")
            var jsonArray = JSONArray(value)
            Log.d(TAG, "제이슨에 들어가고 제이슨 어레이는= ${jsonArray.toString()}")
        }
        prefs.edit().putString(PREF_KEY_SEARCH_HISTORY_LIST, jsonArray.toString()).apply()
    }

}