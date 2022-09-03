package com.imanfz.androidutils.extension

import android.widget.EditText
import androidx.appcompat.widget.AppCompatEditText

/**
 * Created by Iman Faizal on 21/May/2022
 **/

fun EditText.getString(): String {
    return this.text.toString()
}

fun AppCompatEditText.getString(): String {
    return this.text.toString()
}