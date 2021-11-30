package com.example.testforwemo.database

import androidx.room.*

@Dao
interface ItemDBDao {

    @Query("SELECT * FROM ${ItemDBModel.TABLE_NAME} ORDER BY ${ItemDBModel.COLUMN_UPDATE_TIME} DESC")
    fun getAll(): List<ItemDBModel>


    @Query("select * from " + ItemDBModel.TABLE_NAME + " where itemId LIKE :itemId LIMIT 1")
    fun queryByItemId(itemId: String): ItemDBModel

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(item: ItemDBModel): Long

    @Update
    fun update(item: ItemDBModel): Int


    @Delete
    fun delete(item: ItemDBModel): Int
}