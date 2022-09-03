package com.imanfz.utils

import android.content.ContentValues.TAG
import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.util.Log
import java.io.IOException
import java.net.InetSocketAddress
import java.net.Socket

/**
 * Created by Iman Faizal on 21/May/2022
 **/

fun isConnectionOn(context: Context): Boolean {
    val connectivityManager =
        context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
    val networkCapabilities = connectivityManager.activeNetwork ?: return false
    val actNw =
        connectivityManager.getNetworkCapabilities(networkCapabilities) ?: return false
    return when {
        actNw.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) -> true
        actNw.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR) -> true
        actNw.hasTransport(NetworkCapabilities.TRANSPORT_ETHERNET) -> true
        else -> false
    }
}

fun isInternetAvailable(): Boolean {
    return try {
        val timeoutMs = 1500
        val sock = Socket()
        val socketAddress = InetSocketAddress("8.8.8.8", 53)

        sock.connect(socketAddress, timeoutMs)
        sock.close()

        true
    } catch (e: IOException) {
        Log.e(TAG, "isInternetAvailable: ${e.localizedMessage}")
        false
    }
}