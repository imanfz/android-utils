package com.imanfz.utility

import android.annotation.SuppressLint
import android.content.Context
import android.os.Build
import com.imanfz.utility.extension.logi
import com.imanfz.utility.extension.longToast
import java.io.BufferedReader
import java.io.File
import java.io.InputStreamReader

/**
 * Created by Iman Faizal on 02/Sep/2022
 **/

@Suppress("unused")
class RootUtils(val context: Context) {

    fun isDeviceRooted(): Boolean {
        return  checkRootMethod1() || checkRootMethod2() || checkRootMethod3() || searchForMagisk()
    }

    private fun checkRootMethod1(): Boolean {
        val buildTags = Build.TAGS
        return buildTags != null && buildTags.contains("test-keys")
    }

    private fun checkRootMethod2(): Boolean {
        val paths = arrayOf(
            "/system/app/Superuser.apk",
            "/sbin/su",
            "/system/bin/su",
            "/system/xbin/su",
            "/data/local/xbin/su",
            "/data/local/bin/su",
            "/system/sd/xbin/su",
            "/system/bin/failsafe/su",
            "/data/local/su",
            "/su/bin/su"
        )
        for (path in paths) {
            if (File(path).exists()) return true
        }
        return false
    }

    private fun checkRootMethod3(): Boolean {
        var process: Process? = null
        return try {
            process = Runtime.getRuntime().exec(arrayOf("/system/xbin/which", "su"))
            val inputStream = BufferedReader(InputStreamReader(process.inputStream))
            inputStream.readLine() != null
        } catch (t: Throwable) {
            false
        } finally {
            process?.destroy()
        }
    }

    private fun isEmulator(): Boolean {
        var isEmulator = false
        for (item in Build.SUPPORTED_ABIS) {
            if (item.contains("x86")) {
                isEmulator = true
                break
            }
        }
        return isEmulator
    }

    // Detect Magisk Hide
    @SuppressLint("QueryPermissionsNeeded")
    private fun searchForMagisk(): Boolean {
        var result = false
        val installedPackages = context.packageManager.getInstalledPackages(0)
        for (i in installedPackages.indices) {
            val info = installedPackages[i]
            val appInfo = info.applicationInfo
            val nativeLibraryDir = appInfo.nativeLibraryDir
//            val packageName = appInfo.packageName
            logi("Magisk Detection - Checking App: $nativeLibraryDir")
            val f = File("$nativeLibraryDir/libstub.so")
            if (f.exists()) {
                context.longToast("Magisk was Detected!")
                result = true
            }
        }

        return result
    }
}