package com.imanfz.utility.extension

import android.net.Uri
import android.widget.ImageView
import androidx.core.content.ContextCompat
import com.bumptech.glide.load.resource.bitmap.CenterCrop
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.bumptech.glide.request.RequestOptions
import com.google.android.material.imageview.ShapeableImageView
import com.imanfz.utility.GlideApp
import com.imanfz.utility.R
import com.imanfz.utility.convertDpToPixel
import com.imanfz.utility.model.baseListOperator

/**
 * Created by Iman Faizal on 21/May/2022
 **/

fun ImageView.loadImage(url: String) {
    if (url.isEmpty()) return

    GlideApp.with(this)
        .load(url)
        .into(this)
}

fun ImageView.loadImage(uri: Uri) {
    GlideApp.with(this)
        .load(uri)
        .into(this)
}

fun ImageView.loadImage(drawableId: Int) {
    GlideApp.with(this)
        .load(drawableId)
        .into(this)
}

fun ImageView.loadCircleImage(url: String) {
    GlideApp.with(this)
        .load(url)
        .apply(
            RequestOptions.circleCropTransform()
        )
        .into(this)
}

fun ImageView.loadCircleImage(uri: Uri) {
    GlideApp.with(this)
        .load(uri)
        .apply(
            RequestOptions.circleCropTransform()
        )
        .into(this)
}

fun ImageView.loadCircleImage(drawableId: Int) {
    GlideApp.with(this)
        .load(drawableId)
        .apply(
            RequestOptions.circleCropTransform()
        )
        .into(this)
}

fun ImageView.loadRoundedImage(url: String, radius: Int = 10) {
    GlideApp.with(this)
        .load(url)
        .transform(CenterCrop(), RoundedCorners(radius))
        .into(this)
}

fun ImageView.loadRoundedImage(uri: Uri, radius: Int = 10) {
    GlideApp.with(this)
        .load(uri)
        .transform(CenterCrop(), RoundedCorners(radius))
        .into(this)
}

fun ImageView.loadRoundedImage(drawableId: Int, radius: Int = 10) {
    GlideApp.with(this)
        .load(drawableId)
        .transform(CenterCrop(), RoundedCorners(radius))
        .into(this)
}

fun ShapeableImageView.setRadius(radius: Int) {
    shapeAppearanceModel = shapeAppearanceModel
        .toBuilder()
        .setAllCornerSizes(convertDpToPixel(radius.toFloat(), context))
        .build()
}

fun ImageView.checkOperator(text: String): Boolean {
    return if (text.length > 3) {
        val result = baseListOperator.firstOrNull { it.prefix.contains(text.substring(0, 4)) }
        if (result != null) {
            setImageDrawable(ContextCompat.getDrawable(context, result.icon))
            true
        } else {
            setImageDrawable(ContextCompat.getDrawable(context, R.drawable.ic_phone_android))
            false
        }
    } else {
        setImageDrawable(ContextCompat.getDrawable(context, R.drawable.ic_phone_android))
        false
    }
}
