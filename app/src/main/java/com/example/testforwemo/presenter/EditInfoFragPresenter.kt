package com.example.testforwemo.presenter

import android.net.Uri
import com.example.testforwemo.ADD_PHOTO_TYPE
import com.example.testforwemo.PHOTO_ITEM_TYPE
import com.example.testforwemo.database.ItemDBModel
import com.example.testforwemo.database.ItemDatabase
import com.example.testforwemo.model.ItemModel
import com.example.testforwemo.model.PhotoModel
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import timber.log.Timber
import java.util.*

class EditInfoFragPresenter(
    private val mItemDataBase: ItemDatabase,
    private val mIsEdit: Boolean,
    private val mItemId: String?,
    private val mListener: OnEditInfoFragListener
) {
    var mPhotoList: ArrayList<PhotoModel> = ArrayList()
    var mItemModel = ItemModel()

    interface OnEditInfoFragListener {
        fun updateView(item: ItemModel)
        fun updatePhotosView(photos: ArrayList<PhotoModel>)
        fun insertFinish()
        fun updateFinish()
    }

    fun getItemData() {
        GlobalScope.launch {
            if (mIsEdit && mItemId != null) {
                mItemModel = ItemModel().parse(mItemDataBase.getTodoDao().queryByItemId(mItemId))
                if (mItemModel.photoList.size > 0) {
                    mPhotoList.clear()
                    mPhotoList.addAll(mItemModel.photoList)
                }
            }
            mListener.updateView(mItemModel)
            setPhotoListView()
        }
    }

    fun addPhoto(uri: Uri) {
        mPhotoList.add(PhotoModel(UUID.randomUUID().toString(), uri.toString(), PHOTO_ITEM_TYPE))
        setPhotoListView()
    }

    fun removePhoto(photo: PhotoModel) {
        if (mPhotoList.size > 0) {
            val it: MutableIterator<PhotoModel> = mPhotoList.iterator()
            while (it.hasNext()) {
                val model: PhotoModel = it.next()
                if (model.photoId.equals(photo.photoId)) {
                    it.remove()
                }
            }
        }
        setPhotoListView()
    }

    fun setPhotoListView() {
        val photos: ArrayList<PhotoModel> = ArrayList()
        photos.addAll(mPhotoList)
        if (mPhotoList.size < 3) photos.add(PhotoModel(null, null, ADD_PHOTO_TYPE))
        mListener.updatePhotosView(photos)
    }

    fun insertData(title: String, content: String, latitude: Double, longitude: Double) {
        Timber.d("insertData()")
        GlobalScope.launch {
            mItemDataBase.getTodoDao().insert(
                ItemDBModel(
                    UUID.randomUUID().toString(),
                    title,
                    Calendar.getInstance().time,
                    mPhotoList,
                    content,
                    latitude,
                    longitude
                )
            )
            mListener.insertFinish()
        }
    }

    fun updateData(title: String, content: String, latitude: Double, longitude: Double) {
        Timber.d("updateData()")
        GlobalScope.launch {
            val itemDBModel = ItemDBModel(
                mItemId!!,
                title,
                Calendar.getInstance().time,
                mPhotoList,
                content,
                latitude,
                longitude
            )
            itemDBModel.id = mItemModel.id
            mItemDataBase.getTodoDao().update(itemDBModel)
            mListener.updateFinish()
        }
    }

}