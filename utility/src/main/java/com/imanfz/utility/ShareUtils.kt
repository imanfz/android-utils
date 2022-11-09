package com.imanfz.utility

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.telephony.PhoneNumberUtils
import com.imanfz.utility.extension.isPackageInstalled
import com.imanfz.utility.extension.longToast

@Suppress("unused")
object ShareUtils {

    /**
     * Default
     *
     * @param context
     * @param text
     * @param uriImage
     */
    fun default(
        context: Context,
        text: String? = null,
        uriImage: String? = null
    ) {
        try {
            if (text == null && uriImage == null) return
            Intent(Intent.ACTION_SEND).apply {
                putExtra(Intent.EXTRA_SUBJECT, "Receipt Payment")
                uriImage?.let {
                    putExtra(Intent.EXTRA_STREAM, Uri.parse(it))
                }
                text?.let {
                    putExtra(Intent.EXTRA_TEXT, it)
                }
                type = if (uriImage != null) "image/*" else "text/plain"
                context.startActivity(Intent.createChooser(this, "Share via..."))
            }
        } catch (e: Exception) {
            context.longToast(e.localizedMessage ?: "Share failed, please try again.")
        }
    }

    /**
     * Send to whatsapp
     *
     * @param context
     * @param message
     * @param phoneNumber nullable
     * @param uriImage nullable
     */
    fun sendToWhatsapp(
        context: Context,
        message: String,
        phoneNumber: String? = null,
        uriImage: String? = null
    ) {
        try {
            // Checking whether whatsapp is installed or not
            if (!context.isPackageInstalled("com.whatsapp")) {
                context.longToast("Please install whatsapp first.")
            } else {
                // Creating intent with action send
                Intent(Intent.ACTION_SEND).apply {
                    uriImage?.let {
                        putExtra(Intent.EXTRA_STREAM, Uri.parse(it))
                    }
                    phoneNumber?.let { // send to specific number
                        // convert 0 in first phone number to country code
                        val phone = if (it.startsWith("0")) {
                            it.replaceFirst("0", "62")
                        } else it
                        putExtra(
                            "jid",
                            "${PhoneNumberUtils.stripSeparators(phone)}@s.whatsapp.net"
                        )
                    }
                    putExtra(Intent.EXTRA_TEXT, message)
                    setPackage("com.whatsapp")
                    type = if (uriImage != null) "image/*" else "text/plain"
                    context.startActivity(this)
                }
            }
        } catch (e: Exception) {
            context.longToast(e.localizedMessage ?: "Send failed, please try again.")
        }
    }

    /**
     * Send to mail
     *
     * @param context
     * @param listEmail
     * @param subject nullable
     * @param message
     * @param uriImage nullable
     */
    fun sendToMail(
        context: Context,
        listEmail: ArrayList<String>,
        subject: String? = null,
        message: String,
        uriImage: String? = null
    ) {
        try {
            // Creating intent with action send
            Intent(Intent.ACTION_SEND).apply {
                putExtra(Intent.EXTRA_EMAIL, listEmail)
                subject?.let {
                    putExtra(Intent.EXTRA_SUBJECT, it)
                }
                uriImage?.let {
                    putExtra(Intent.EXTRA_STREAM, Uri.parse(it))
                }
                putExtra(Intent.EXTRA_TEXT, message)
                type = if (uriImage != null) "image/*" else "text/plain"
                context.startActivity(Intent.createChooser(this, "Sending email..."))
            }
        } catch (e: Exception) {
            context.longToast(e.localizedMessage ?: "Send failed, please try again.")
        }
    }

}