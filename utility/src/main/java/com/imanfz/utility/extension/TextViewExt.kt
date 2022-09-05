package com.imanfz.utility.extension

import android.os.Build
import android.text.Html
import android.text.Spannable
import android.text.SpannableString
import android.text.style.ForegroundColorSpan
import android.view.View
import android.widget.AutoCompleteTextView
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.core.content.res.ResourcesCompat
import com.imanfz.utility.R

@Suppress("DEPRECATION")
fun TextView.fromHtmlText(text: String) {
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
        setText(Html.fromHtml(text, Html.FROM_HTML_MODE_COMPACT))
    } else {
        setText(Html.fromHtml(text));
    }
}

fun TextView.setColorOfSubstring(substring: String, color: Int) {
    try {
        val spannable = SpannableString(text)
        val start = text.indexOf(substring)
        spannable.setSpan(ForegroundColorSpan(ContextCompat.getColor(context, color)), start, start + substring.length, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
        text = spannable
    } catch (e: Exception) {
        e.printStackTrace()
    }
}

fun AutoCompleteTextView.setupDropdownUI() {
//        dropDownVerticalOffset = 10
//        setDropDownBackgroundResource(R.drawable.bg_circle_white_r20)

    onFocusChangeListener =
        View.OnFocusChangeListener { _, hasFocus ->
            if (hasFocus) {
                showDropDown()
                this.setCompoundDrawablesWithIntrinsicBounds(
                    null,
                    null,
                    ResourcesCompat.getDrawable(resources, R.drawable.ic_arrow_up, this.context.theme),
                    null
                )
            } else {
                dismissDropDown()
            }
        }

    setOnDismissListener {
        this.setCompoundDrawablesWithIntrinsicBounds(
            null,
            null,
            ResourcesCompat.getDrawable(resources, R.drawable.ic_arrow_down, this.context.theme),
            null
        )
    }

    setOnClickListener {
        showDropDown()
        this.setCompoundDrawablesWithIntrinsicBounds(
            null,
            null,
            ResourcesCompat.getDrawable(resources, R.drawable.ic_arrow_up, this.context.theme),
            null
        )
    }
}