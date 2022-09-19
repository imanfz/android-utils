package com.imanfz.utility

import android.content.ContentValues.TAG
import android.content.Context
import android.net.ConnectivityManager
import android.net.Network
import android.net.NetworkCapabilities
import android.net.NetworkRequest
import android.util.Log
import androidx.lifecycle.LiveData
import com.imanfz.utility.extension.logd
import com.imanfz.utility.extension.loge
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
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
        Socket().apply {
            connect(InetSocketAddress("8.8.8.8", 53), timeoutMs)
            close()
        }

        true
    } catch (e: IOException) {
        e.printStackTrace()
        Log.e(TAG, "isInternetAvailable: ${e.localizedMessage}")
        false
    }
}

@Suppress("unused")
class NetworkStatusUtils(context: Context) : LiveData<NetworkStatus>() {

    private val validateNetworkConnections : ArrayList<Network> = ArrayList()
    private var connectivityManager: ConnectivityManager =
        context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
    private lateinit var connectivityManagerCallback: ConnectivityManager.NetworkCallback

    private fun announceStatus(){
        if (validateNetworkConnections.isNotEmpty()){
            postValue(NetworkStatus.Available)
            logd("Network Connection Established \n$validateNetworkConnections")
        } else {
            postValue(NetworkStatus.Unavailable)
            loge("No Internet Connection!")
        }
    }

    private fun getConnectivityManagerCallback() =
        object : ConnectivityManager.NetworkCallback(){
            override fun onAvailable(network: Network) {
                super.onAvailable(network)
                val networkCapability = connectivityManager.getNetworkCapabilities(network)
                val hasNetworkConnection = networkCapability?.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET)?:false
                if (hasNetworkConnection){
                    determineInternetAccess(network)
                }
            }

            override fun onLost(network: Network) {
                super.onLost(network)
                validateNetworkConnections.remove(network)
                announceStatus()
            }

            override fun onCapabilitiesChanged(network: Network, networkCapabilities: NetworkCapabilities) {
                super.onCapabilitiesChanged(network, networkCapabilities)
                if (networkCapabilities.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET)){
                    determineInternetAccess(network)
                } else {
                    validateNetworkConnections.remove(network)
                }
                announceStatus()
            }
        }

    private fun determineInternetAccess(network: Network) {
        CoroutineScope(Dispatchers.IO).launch{
            if (isInternetAvailable()){
                withContext(Dispatchers.Main){
                    validateNetworkConnections.add(network)
                    announceStatus()
                }
            }
        }
    }

    override fun onActive() {
        super.onActive()
        connectivityManagerCallback = getConnectivityManagerCallback()
        val networkRequest = NetworkRequest
            .Builder()
            .addCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET)
            .build()
        connectivityManager.registerNetworkCallback(networkRequest, connectivityManagerCallback)
    }

    override fun onInactive() {
        super.onInactive()
        connectivityManager.unregisterNetworkCallback(connectivityManagerCallback)
    }
}

sealed class NetworkStatus {
    object Available : NetworkStatus()
    object Unavailable : NetworkStatus()
}