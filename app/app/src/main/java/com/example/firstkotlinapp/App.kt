package com.example.firstkotlinapp

import android.app.Application
import com.example.firstkotlinapp.dataBase.MySharedPreferences

class App : Application() {
    companion object{
        lateinit var prefs : MySharedPreferences
    }

    override fun onCreate() {
        prefs = MySharedPreferences(applicationContext)
        super.onCreate()
    }
}