package com.example.testforwemo.presenter

import com.example.testforwemo.database.ItemDatabase
import com.example.testforwemo.model.ItemModel
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

class InfoFragPresenter(
    private val mItemDataBase: ItemDatabase,
    private val mListener: OnInfoFragListener
) {
    var mItemModel = ItemModel()

    interface OnInfoFragListener {
        fun updateView(item: ItemModel)
        fun deleteFinish()
    }

    fun getItemData(itemId: String) {
        GlobalScope.launch {
            mItemModel = ItemModel().parse(mItemDataBase.getTodoDao().queryByItemId(itemId))
            mListener.updateView(mItemModel)
        }
    }

    fun deleteItemData() {
        GlobalScope.launch {
            mItemDataBase.getTodoDao().delete(ItemModel().parse(mItemModel))
            mListener.deleteFinish()
        }
    }
}