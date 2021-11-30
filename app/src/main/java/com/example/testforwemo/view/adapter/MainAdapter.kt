package com.example.testforwemo.view.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.CenterCrop
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.bumptech.glide.request.RequestOptions
import com.example.testforwemo.R
import com.example.testforwemo.databinding.ItemMainListBinding
import com.example.testforwemo.databinding.ItemTitleListBinding
import com.example.testforwemo.ITEM_TYPE
import com.example.testforwemo.model.ItemModel
import com.example.testforwemo.TITLE_TYPE
import java.text.SimpleDateFormat

class MainAdapter(private val mContext: Context, private val mListener: OnAdapterListener) :
    ListAdapter<ItemModel, RecyclerView.ViewHolder>(MainDiffCallback()) {

    interface OnAdapterListener {
        fun itemClick(item: ItemModel)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return when (viewType) {
            TITLE_TYPE -> TitleViewHolder(ItemTitleListBinding.inflate(LayoutInflater.from(mContext)))
            else -> ItemViewHolder(
                ItemMainListBinding.inflate(LayoutInflater.from(mContext)),
                mContext,
                mListener
            )
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when (getItem(position).uiType) {
            ITEM_TYPE -> (holder as ItemViewHolder).bind(getItem(position))
        }
    }

    override fun getItemViewType(position: Int): Int {
        return getItem(position).uiType
    }

    class ItemViewHolder(
        itemView: ItemMainListBinding,
        val context: Context,
        private val listener: OnAdapterListener
    ) : RecyclerView.ViewHolder(itemView.root) {
        private val mBinding = itemView
        fun bind(item: ItemModel) {
            mBinding.txtTitle.text = item.title

            if (item.photoList.size > 0) {
                val firstPhoto = item.photoList[0]
                Glide.with(context)
                    .load(firstPhoto.photoUri)
                    .apply(RequestOptions().transform(CenterCrop(), RoundedCorners(30)))
                    .error(R.drawable.ic_pic_default)
                    .into(mBinding.imgPhoto)
            } else {
                mBinding.imgPhoto.setImageResource(R.drawable.ic_pic_default)
            }

            val simpleDateFormat = SimpleDateFormat("yyyy/MM/dd HH:mm:ss")
            val timestamp = simpleDateFormat.format(item.updateTime)
            mBinding.txtUpdateTime.text = timestamp

            mBinding.layBase.setOnClickListener { listener.itemClick(item) }
        }
    }

    class TitleViewHolder(itemView: ItemTitleListBinding) : RecyclerView.ViewHolder(itemView.root)

    class MainDiffCallback : DiffUtil.ItemCallback<ItemModel>() {
        override fun areItemsTheSame(oldItem: ItemModel, newItem: ItemModel): Boolean {
            return oldItem.itemId == newItem.itemId
        }

        override fun areContentsTheSame(oldItem: ItemModel, newItem: ItemModel): Boolean {
            return oldItem == newItem
        }
    }
}