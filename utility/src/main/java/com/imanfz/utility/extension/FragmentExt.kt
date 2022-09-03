package com.imanfz.utility.extension

import android.app.Activity
import android.os.Bundle
import androidx.fragment.app.Fragment

/**
 * Created by Iman Faizal on 30/Aug/2022
 **/

// Fragment related
inline fun <reified T: Any> Fragment.getValue(
    label: String,
    defaultValue: T? = null
) = lazy {
    val value = arguments?.get(label)
    if (value is T) value else defaultValue
}

inline fun <reified T: Any> Fragment.getValueNonNull(
    label: String,
    defaultValue: T? = null
) = lazy {
    val value = arguments?.get(label)
    requireNotNull(if (value is T) value else defaultValue) { label }
}

inline fun <reified T: Activity> Fragment.startActivityAndClearTask(
    extras: Bundle? = null
) {
    requireContext().startActivityAndClearTask<T>(extras)
}

inline fun <reified T: Activity> Fragment.startActivityClearTop(
    extras: Bundle? = null
) {
    requireContext().startActivityClearTop<T>(extras)
}

inline fun <reified T: Activity> Fragment.startActivity(
    extras: Bundle? = null
) {
    requireContext().startActivity<T>(extras)
}

fun Fragment.finish() {
    requireActivity().finish()
}

fun Fragment.showDialog(
    title: String,
    message: String,
    okClicked: (() -> Unit?)? = null,
    cancelClicked: (() -> Unit?)? = null
) {
    requireContext().showBasicDialog(title, message, okClicked, cancelClicked)
}

fun Fragment.copyTextToClipboard(text: String, msg: String) {
    requireContext().copyTextToClipboard(text, msg)
}

fun Fragment.shortToast(message: String) {
    requireContext().shortToast(message)
}

fun Fragment.longToast(message: String) {
    requireContext().longToast(message)
}

fun Fragment.hasPermission(permission: String): Boolean {
    return requireContext().hasPermission(permission)
}

fun Fragment.hasPermission(permission: Array<String>): Boolean {
    return requireContext().hasPermission(permission)
}

fun Fragment.hideKeyboard() {
    requireActivity().hideKeyboard()
}

fun Fragment.showKeyboard() {
    requireActivity().showKeyboard()
}

fun Fragment.longSnack(text: String) {
    view?.longSnack(text)
}

fun Fragment.shortSnack(text: String) {
    view?.shortSnack(text)
}

/*
fun Fragment.showDialogFailSingle(
    desc: String,
    positiveText: String = getString(R.string.ok),
    positiveClick: (() -> Unit)? = null
) {
    FailedDialog(requireContext())
        .single(desc, positiveText, positiveClick)
        .show()
}

fun Fragment.showDialogFailDouble(
    desc: String,
    positiveText: String = getString(R.string.ok),
    negativeText: String = getString(R.string.back),
    positiveClick: (() -> Unit)? = null,
    negativeClick: (() -> Unit)? = null
) {
    FailedDialog(requireContext())
        .double(desc, positiveText, negativeText, positiveClick, negativeClick)
        .show()
}
*/
