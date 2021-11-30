package com.example.testforwemo.model

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class PhotoModel(val photoId: String?, val photoUri: String?, val uiType: Int):Parcelable
