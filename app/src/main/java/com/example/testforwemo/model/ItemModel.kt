package com.example.testforwemo.model

import com.example.testforwemo.ITEM_TYPE
import com.example.testforwemo.database.ItemDBModel
import java.util.*
import kotlin.collections.ArrayList

class ItemModel {

    var itemId: String = ""
    var title: String = ""
    var updateTime: Date = Date()
    var photoList: ArrayList<PhotoModel> = ArrayList()
    var content: String = ""
    var latitude: Double = 0.0
    var longitude: Double = 0.0
    var uiType: Int = ITEM_TYPE
    var id: Int = -1

    fun parse(dbModel: ItemDBModel): ItemModel {
        val item = ItemModel()
        item.itemId = dbModel.itemId
        item.title = dbModel.title
        item.updateTime = dbModel.updateDate
        item.photoList = dbModel.photoList
        item.content = dbModel.content
        item.latitude = dbModel.latitude
        item.longitude = dbModel.longitude
        item.id = dbModel.id
        return item
    }

    fun parse(itemModel: ItemModel): ItemDBModel {
        val dbModel = ItemDBModel(
            itemModel.itemId,
            itemModel.title,
            itemModel.updateTime,
            itemModel.photoList,
            itemModel.content,
            itemModel.latitude,
            itemModel.longitude
        )
        dbModel.id = itemModel.id
        return dbModel
    }
}