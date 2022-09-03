package com.imanfz.utility.extension

import android.net.Uri
import android.widget.ImageView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.CenterCrop
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.bumptech.glide.request.RequestOptions
import com.google.android.material.imageview.ShapeableImageView
import com.imanfz.utility.convertDpToPixel

/**
 * Created by Iman Faizal on 21/May/2022
 **/

fun ImageView.loadImage(url: String) {
    if (url.isEmpty()) return

    Glide.with(this)
        .load(url)
        .into(this)
}

fun ImageView.loadImage(uri: Uri) {
    Glide.with(this)
        .load(uri)
        .into(this)
}

fun ImageView.loadImage(drawableId: Int) {
    Glide.with(this)
        .load(drawableId)
        .into(this)
}

fun ImageView.loadCircleImage(url: String) {
    Glide.with(this)
        .load(url)
        .apply(
            RequestOptions.circleCropTransform()
        )
        .into(this)
}

fun ImageView.loadCircleImage(uri: Uri) {
    Glide.with(this)
        .load(uri)
        .apply(
            RequestOptions.circleCropTransform()
        )
        .into(this)
}

fun ImageView.loadCircleImage(drawableId: Int) {
    Glide.with(this)
        .load(drawableId)
        .apply(
            RequestOptions.circleCropTransform()
        )
        .into(this)
}

fun ImageView.loadRoundedImage(url: String, radius: Int = 10) {
    Glide.with(this)
        .load(url)
        .transform(CenterCrop(), RoundedCorners(radius))
        .into(this)
}

fun ImageView.loadRoundedImage(uri: Uri, radius: Int = 10) {
    Glide.with(this)
        .load(uri)
        .transform(CenterCrop(), RoundedCorners(radius))
        .into(this)
}

fun ImageView.loadRoundedImage(drawableId: Int, radius: Int = 10) {
    Glide.with(this)
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