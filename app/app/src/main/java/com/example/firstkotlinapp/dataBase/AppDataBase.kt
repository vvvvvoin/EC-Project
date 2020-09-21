package com.example.firstkotlinapp.dataBase

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import com.example.firstkotlinapp.dataClass.MarkerDataVO
//import com.example.firstkotlinapp.dataClass.MarkerEntity
import com.example.firstkotlinapp.retrofit.OkHttpManager

@Database(entities = [MarkerDataVO::class], version = 1)
abstract class AppDatabase : RoomDatabase() {
    abstract fun markerDAO(): MarkerDAO

    companion object {
        private val DB_NAME = "marker-db"
        private var instance: AppDatabase? = null

        fun getInstance(context: Context): AppDatabase {
            return instance ?: synchronized(this) {
                instance ?: buildDatabase(context)
            }
        }

        private fun buildDatabase(context: Context): AppDatabase {
            return Room.databaseBuilder(context.applicationContext, AppDatabase::class.java, DB_NAME)
                .addCallback(object : RoomDatabase.Callback() {
                    override fun onCreate(db: SupportSQLiteDatabase) {
                        super.onCreate(db)
                    }
                }).build()
        }
    }

}