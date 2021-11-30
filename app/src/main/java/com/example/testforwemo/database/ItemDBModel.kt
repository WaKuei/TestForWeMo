package com.example.testforwemo.database

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.example.testforwemo.model.PhotoModel
import java.util.*

@Entity(tableName = ItemDBModel.TABLE_NAME)
class ItemDBModel(
    @ColumnInfo(name = COLUMN_ITEM_ID) var itemId: String,
    @ColumnInfo(name = COLUMN_TITLE) var title: String,
    @ColumnInfo(name = COLUMN_UPDATE_TIME) var updateDate: Date,
    @ColumnInfo(name = COLUMN_PHOTO_LIST) var photoList: ArrayList<PhotoModel>,
    @ColumnInfo(name = COLUMN_CONTENT) var content: String,
    @ColumnInfo(name = COLUMN_LATITUDE) var latitude: Double,
    @ColumnInfo(name = COLUMN_LONGITUDE) var longitude: Double
) {

    companion object {
        const val TABLE_NAME = "items_table"

        const val COLUMN_ID = "id"
        const val COLUMN_ITEM_ID = "itemId"
        const val COLUMN_TITLE = "title"
        const val COLUMN_UPDATE_TIME = "updateTime"
        const val COLUMN_PHOTO_LIST = "photoList"
        const val COLUMN_CONTENT = "content"
        const val COLUMN_LATITUDE = "latitude"
        const val COLUMN_LONGITUDE = "longitude"
    }

    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = COLUMN_ID)
    var id: Int = 0
}
