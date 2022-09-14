package com.imanfz.utility

import androidx.recyclerview.widget.DiffUtil

/**
 * Created by Iman Faizal on 15/Sep/2022
 **/

interface BaseModel {
    fun contains(someValue: String): Boolean = false
    fun isItemSameWith(value: BaseModel): Boolean = false
    fun isContentSameWith(value: BaseModel): Boolean = false
}

class ItemDiffCallback<T: BaseModel> : DiffUtil.ItemCallback<T>() {
    override fun areItemsTheSame(oldItem: T, newItem: T): Boolean {
        return oldItem.isItemSameWith(newItem)
    }

    override fun areContentsTheSame(oldItem: T, newItem: T): Boolean {
        return oldItem.isContentSameWith(newItem)
    }
}

