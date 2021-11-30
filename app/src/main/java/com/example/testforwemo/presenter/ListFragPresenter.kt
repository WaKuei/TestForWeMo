package com.example.testforwemo.presenter

import com.example.testforwemo.model.ItemModel
import com.example.testforwemo.TITLE_TYPE
import com.example.testforwemo.database.ItemDatabase
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

class ListFragPresenter(
    private val mItemDataBase: ItemDatabase,
    private val mListener: OnListFragListener
) {

    interface OnListFragListener {
        fun updateList(data: ArrayList<ItemModel>)
        fun updateEmptyView(isEmpty:Boolean)
    }

    fun getListData() {
        GlobalScope.launch {
            val list = ArrayList<ItemModel>()
            val titleItem = ItemModel()
            titleItem.uiType = TITLE_TYPE
            list.add(titleItem)
            list.addAll(getItemFromDB())
            mListener.updateList(list)
        }
    }

    private fun getItemFromDB(): ArrayList<ItemModel> {
        val itemList = ArrayList<ItemModel>()
        val daoList = mItemDataBase.getTodoDao().getAll()
        if (daoList.isNotEmpty()) {
            for (itemDBModel in daoList) {
                itemList.add(ItemModel().parse(itemDBModel))
            }
        }
        mListener.updateEmptyView(itemList.size==0)
        return itemList
    }
}