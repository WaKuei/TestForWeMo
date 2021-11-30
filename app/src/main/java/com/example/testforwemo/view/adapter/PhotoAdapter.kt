package com.example.testforwemo.view.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.CenterCrop
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.bumptech.glide.request.RequestOptions
import com.example.testforwemo.ADD_PHOTO_TYPE
import com.example.testforwemo.PHOTO_ITEM_TYPE
import com.example.testforwemo.R
import com.example.testforwemo.databinding.ItemAddPhotoOfListBinding
import com.example.testforwemo.databinding.ItemPhotoOfListBinding
import com.example.testforwemo.model.PhotoModel

class PhotoAdapter(
    private val mContext: Context,
    private val mListener: OnAdapterListener,
    private val mIsEdit: Boolean
) : ListAdapter<PhotoModel, RecyclerView.ViewHolder>(MainDiffCallback()) {

    interface OnAdapterListener {
        fun addClick()
        fun deleteItemClick(item: PhotoModel)
        fun itemClick(item: PhotoModel)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return when (viewType) {
            ADD_PHOTO_TYPE -> AddViewHolder(
                ItemAddPhotoOfListBinding.inflate(
                    LayoutInflater.from(
                        mContext
                    )
                ), mListener, mIsEdit
            )
            else -> ItemViewHolder(
                ItemPhotoOfListBinding.inflate(LayoutInflater.from(mContext)),
                mContext,
                mListener,
                mIsEdit
            )
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when (getItem(position).uiType) {
            PHOTO_ITEM_TYPE -> (holder as ItemViewHolder).bind(getItem(position))
            else -> (holder as AddViewHolder).bind()
        }
    }

    override fun getItemViewType(position: Int): Int {
        return getItem(position).uiType
    }

    class ItemViewHolder(
        itemView: ItemPhotoOfListBinding,
        val context: Context,
        private val listener: OnAdapterListener,
        private val isEdit: Boolean
    ) : RecyclerView.ViewHolder(itemView.root) {
        private val binding = itemView
        fun bind(item: PhotoModel) {

            Glide.with(context)
                .load(item.photoUri.toString()) // Uri of the picture
                .apply(RequestOptions().transform(CenterCrop(), RoundedCorners(30)))
                .error(R.drawable.ic_pic_default)
                .into(binding.imgPhoto)

            binding.imgDelete.setOnClickListener {
                if (isEdit) listener.deleteItemClick(item)
            }
            binding.imgPhoto.setOnClickListener {
                if (isEdit) listener.itemClick(item)
            }

            if (isEdit) binding.imgDelete.visibility = View.VISIBLE
            else binding.imgDelete.visibility = View.GONE
        }
    }

    class AddViewHolder(
        itemView: ItemAddPhotoOfListBinding,
        private val listener: OnAdapterListener,
        private val isEdit: Boolean
    ) : RecyclerView.ViewHolder(itemView.root) {
        private val binding = itemView
        fun bind() {
            binding.layBase.setOnClickListener {
                if (isEdit) listener.addClick()
            }
        }
    }

    class MainDiffCallback : DiffUtil.ItemCallback<PhotoModel>() {
        override fun areItemsTheSame(oldItem: PhotoModel, newItem: PhotoModel): Boolean {
            return oldItem.photoId.equals(newItem.photoId)
        }

        override fun areContentsTheSame(oldItem: PhotoModel, newItem: PhotoModel): Boolean {
            return oldItem == newItem
        }
    }
}