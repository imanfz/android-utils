package com.imanfz.utility

import androidx.recyclerview.widget.DiffUtil

/**
 * Created by Iman Faizal on 15/Sep/2022
 **/

interface BaseModel {
    fun isItemSameWith(value: BaseModel): Boolean = this == value
    fun isContentSameWith(value: BaseModel): Boolean = this == value
}

class ItemDiffCallback<T: BaseModel> : DiffUtil.ItemCallback<T>() {
    override fun areItemsTheSame(oldItem: T, newItem: T): Boolean {
        return oldItem.isItemSameWith(newItem)
    }

    override fun areContentsTheSame(oldItem: T, newItem: T): Boolean {
        return oldItem.isContentSameWith(newItem)
    }
}

