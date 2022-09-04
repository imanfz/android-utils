package com.imanfz.utility.extension

import android.text.Spannable
import android.text.SpannableString
import android.text.SpannableStringBuilder
import android.text.style.*
import android.view.View
import java.text.DecimalFormat
import java.text.DecimalFormatSymbols
import java.text.SimpleDateFormat
import java.util.*

/**
 * Created by Iman Faizal on 21/May/2022
 **/

fun String.toDate(format: String, locale: Locale = Locale.getDefault()): Date? {
    return SimpleDateFormat(format, locale).parse(this)
}

fun String.getFirstName(): String {
    trim().apply {
        return if (isBlank() || !contains(" ")) this
        else split(" ").first().toString()
    }
}

fun String.getLastName(): String {
    var result = ""
    trim().split(" ").filter {
        isNotEmpty()
    }.forEachIndexed { index, s ->
        if (index > 0) {
            result += "$s "
        }
    }
    return result.dropLast(1)
}

fun String.getInitials(limit: Int = 2): String {
    val buffer = StringBuffer()
    trim().split(" ").filter {
        it.isNotEmpty()
    }.joinTo(
        buffer = buffer,
        limit = limit,
        separator = "",
        truncated = "",
    ) { s ->
        s.first().uppercase()
    }
    return buffer.toString()
}

fun String.getNumber() : String {
    return this.filter { it.isDigit() }
}

fun String.toCurrency(
    locale: Locale = Locale("in", "ID"),
    currencyCode: String = "IDR",
    withSymbols: Boolean = false,
    minFraction: Int = 2
) : String {
    val decimalFormat =
        if (withSymbols) DecimalFormat.getCurrencyInstance(locale) as DecimalFormat
        else DecimalFormat.getNumberInstance(locale) as DecimalFormat

    decimalFormat.decimalFormatSymbols = DecimalFormatSymbols(locale).apply {
        this.currency = Currency.getInstance(currencyCode)
        if (locale.language.equals("in")) currencySymbol = "Rp. "
    }

    decimalFormat.minimumFractionDigits = minFraction

    return decimalFormat.format(this.toDouble())
}

fun String.toSpannable() = SpannableStringBuilder(this)

fun SpannableStringBuilder.spanText(span: Any): SpannableStringBuilder {
    setSpan(span, 0, length, SpannableString.SPAN_EXCLUSIVE_EXCLUSIVE)
    return this
}

fun String.relativeSize(size: Float): SpannableStringBuilder {
    val span = RelativeSizeSpan(size)
    return toSpannable().spanText(span)
}

fun String.superscript(): SpannableStringBuilder {
    val span = SuperscriptSpan()
    return toSpannable().spanText(span)
}

fun String.strike(): SpannableStringBuilder {
    val span = StrikethroughSpan()
    return toSpannable().spanText(span)
}

fun String.underlined() : SpannableStringBuilder {
    val span = UnderlineSpan()
    return toSpannable().spanText(span)
}

fun SpannableString.withClickableSpan(clickablePart: String, onClickListener: () -> Unit): SpannableString {
    val clickableSpan = object : ClickableSpan() {
        override fun onClick(widget: View) = onClickListener.invoke()
    }
    val clickablePartStart = indexOf(clickablePart)
    setSpan(clickableSpan,
        clickablePartStart,
        clickablePartStart + clickablePart.length,
        Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
    return this
}

fun String.toDate(
    format: String = "yyyy-MM-dd HH:mm:ss"
): Date? {
    val dateFormat = SimpleDateFormat(format, Locale.getDefault())
    return dateFormat.parse(this)
}

fun String.convertDate(formatSource: String? = null, formatResult: String): String {
    val date = if (formatSource == null) toDate() else toDate(formatSource)
    val formatter = SimpleDateFormat(formatResult, Locale.getDefault())
    return if (date != null) formatter.format(date) else ""

}