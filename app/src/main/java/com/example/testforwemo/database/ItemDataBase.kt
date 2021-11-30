package com.example.testforwemo.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters

@Database(entities = [(ItemDBModel::class)], version = 1)
@TypeConverters(Converter::class)
abstract class ItemDatabase : RoomDatabase() {

    companion object {
        private const val DATABASE_NAME = "item_database"

        // For Singleton instantiation
        @Volatile
        private var instance: ItemDatabase? = null

        fun getInstance(context: Context): ItemDatabase {
            return instance ?: synchronized(this) {
                instance ?: buildDatabase(context).also { instance = it }
            }
        }

        private fun buildDatabase(context: Context): ItemDatabase {
            return Room.databaseBuilder(context, ItemDatabase::class.java, DATABASE_NAME).build()
        }
    }

    abstract fun getTodoDao(): ItemDBDao

}