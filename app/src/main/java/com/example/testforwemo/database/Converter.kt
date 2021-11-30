package com.example.testforwemo.database

import androidx.room.TypeConverter
import com.example.testforwemo.model.PhotoModel
import java.util.*
import com.google.gson.Gson

import com.google.gson.reflect.TypeToken

class Converter {

    @TypeConverter
    fun fromTimestamp(value: Long?): Date? {
        return value?.let { Date(it) }
    }

    @TypeConverter
    fun dateToTimestamp(date: Date?): Long? {
        return date?.time
    }

    @TypeConverter
    fun fromString(value: String?): ArrayList<PhotoModel?>? {
        val listType = object : TypeToken<ArrayList<PhotoModel?>?>() {}.type
        return Gson().fromJson(value, listType)
    }

    @TypeConverter
    fun fromPhotoArrayList(list: ArrayList<PhotoModel?>?): String? {
        return Gson().toJson(list)
    }

}