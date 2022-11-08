package com.imanfz.utility.base

import android.content.Context
import androidx.recyclerview.widget.RecyclerView
import androidx.viewbinding.ViewBinding
import com.imanfz.utility.BaseModel

/**
 * Created by Iman Faizal on 31/Aug/2022
 **/

/**
 * A Simple [BaseViewHolder] providing easier support for ViewBinding
 **/
open class BaseViewHolder<MODEL: BaseModel, VB : ViewBinding>(
    val binding: VB
) : RecyclerView.ViewHolder(binding.root) {
    val context: Context = binding.root.context

    open fun bind(data: MODEL) {}
}