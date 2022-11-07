package com.imanfz.utility

import android.app.Activity
import androidx.appcompat.app.AppCompatActivity
import com.google.android.play.core.appupdate.AppUpdateInfo
import com.google.android.play.core.appupdate.AppUpdateManager
import com.google.android.play.core.appupdate.AppUpdateManagerFactory
import com.google.android.play.core.appupdate.testing.FakeAppUpdateManager
import com.google.android.play.core.install.InstallState
import com.google.android.play.core.install.InstallStateUpdatedListener
import com.google.android.play.core.install.model.ActivityResult
import com.google.android.play.core.install.model.AppUpdateType
import com.google.android.play.core.install.model.InstallStatus
import com.google.android.play.core.install.model.UpdateAvailability
import com.imanfz.utility.extension.logi
import com.imanfz.utility.extension.snackBarWithAction

/**
 * Created by Iman Faizal on 08/Apr/2022
 **/

class AppUpdateUtils(activity: Activity) {

    private val appUpdateManager: AppUpdateManager
    private val appUpdateRequestCode = 500
    private val parentActivity: Activity
    private var currentType = AppUpdateType.FLEXIBLE
    private val installStateUpdatedListener = object : InstallStateUpdatedListener {
        override fun onStateUpdate(state: InstallState) {
            if (state.installStatus() == InstallStatus.DOWNLOADED) {
                popupSnackBarForCompleteUpdate()
            } else if (state.installStatus() == InstallStatus.DOWNLOADING) {
                val bytesDownloaded = state.bytesDownloaded()
                val totalBytesToDownload = state.totalBytesToDownload()
                // Show update progress bar.
                logi("Bytes Downloaded: $bytesDownloaded")
                logi("Total Bytes To Download: $totalBytesToDownload")
            }
        }
    }

    init {
        parentActivity = activity
        appUpdateManager = if (BuildConfig.DEBUG) {
            FakeAppUpdateManager(parentActivity).apply {
                setUpdateAvailable(2) // app version code
            }
        } else {
            AppUpdateManagerFactory.create(parentActivity)
        }
        checkUpdate()
    }

    private fun checkUpdate() {
        appUpdateManager.appUpdateInfo.addOnSuccessListener { info ->
            logi("checkUpdateAvailable: ${info.updateAvailability() == UpdateAvailability.UPDATE_AVAILABLE}")
            if (BuildConfig.DEBUG) {
                if (info.updateAvailability() == UpdateAvailability.UPDATE_AVAILABLE) {
                    startUpdate(info, currentType)

                 /*   // for test update
                    val fakeAppUpdate = appUpdateManager as FakeAppUpdateManager
                    fakeAppUpdate.userAcceptsUpdate()
                    fakeAppUpdate.downloadStarts()
                    fakeAppUpdate.downloadCompletes()
                    if (fakeAppUpdate.isImmediateFlowVisible) {
                         popupSnackBarForCompleteUpdate()
                    } else {
                        fakeAppUpdate.installCompletes()
                    }*/
                }
            } else {
                val clientVersionStalenessDays = info.clientVersionStalenessDays()
                clientVersionStalenessDays?.let {
                    // Check if update is available
                    if (info.updateAvailability() == UpdateAvailability.UPDATE_AVAILABLE) { // UPDATE IS AVAILABLE
                        when (info.updatePriority()) {
                            5 -> { // Priority: 5 (Immediate update flow)
                                if (info.isUpdateTypeAllowed(AppUpdateType.IMMEDIATE)) {
                                    startUpdate(info, AppUpdateType.IMMEDIATE)
                                }
                            }
                            4 -> { // Priority: 4
                                if (it >= 5 && info.isUpdateTypeAllowed(AppUpdateType.IMMEDIATE)) {
                                    // Trigger IMMEDIATE flow
                                    startUpdate(info, AppUpdateType.IMMEDIATE)
                                } else if (it >= 3 && info.isUpdateTypeAllowed(AppUpdateType.FLEXIBLE)) {
                                    // Trigger FLEXIBLE flow
                                    startUpdate(info, AppUpdateType.FLEXIBLE)
                                }
                            }
                            3 -> { // Priority: 3
                                if (it >= 30 && info.isUpdateTypeAllowed(AppUpdateType.IMMEDIATE)) {
                                    // Trigger IMMEDIATE flow
                                    startUpdate(info, AppUpdateType.IMMEDIATE)
                                } else if (it >= 15 && info.isUpdateTypeAllowed(AppUpdateType.FLEXIBLE)) {
                                    // Trigger FLEXIBLE flow
                                    startUpdate(info, AppUpdateType.FLEXIBLE)
                                }
                            }
                            2 -> { // Priority: 2
                                if (it >= 90 && info.isUpdateTypeAllowed(AppUpdateType.IMMEDIATE)) {
                                    // Trigger IMMEDIATE flow
                                    startUpdate(info, AppUpdateType.IMMEDIATE)
                                } else if (it >= 30 && info.isUpdateTypeAllowed(AppUpdateType.FLEXIBLE)) {
                                    // Trigger FLEXIBLE flow
                                    startUpdate(info, AppUpdateType.FLEXIBLE)
                                }
                            }
                            1 -> { // Priority: 1
                                // Trigger FLEXIBLE flow
                                startUpdate(info, AppUpdateType.FLEXIBLE)
                            }
                            else -> { // Priority: 0
                                // Do not show in-app update
                            }
                        }
                    } else {
                        // UPDATE IS NOT AVAILABLE
                    }
                }
            }
        }

        appUpdateManager.registerListener(installStateUpdatedListener)
    }

    private fun startUpdate(info: AppUpdateInfo, type: Int) {
        appUpdateManager.startUpdateFlowForResult(info, type, parentActivity, appUpdateRequestCode)
        currentType = type
    }

    fun onResume() {
        appUpdateManager.appUpdateInfo.addOnSuccessListener { info ->
            if (currentType == AppUpdateType.FLEXIBLE) {
                // If the update is downloaded but not installed, notify the user to complete the update.
                if (info.installStatus() == InstallStatus.DOWNLOADED)
                    popupSnackBarForCompleteUpdate()
            } else if (currentType == AppUpdateType.IMMEDIATE) {
                // for AppUpdateType.IMMEDIATE only, already executing updater
                if (info.updateAvailability() == UpdateAvailability.DEVELOPER_TRIGGERED_UPDATE_IN_PROGRESS) {
                    startUpdate(info, AppUpdateType.IMMEDIATE)
                }
            }
        }
    }

    fun onActivityResult(requestCode: Int, resultCode: Int) {
        logi("onActivityResult: $requestCode $resultCode" )
        if (requestCode == appUpdateRequestCode) {
            when (resultCode) {
                AppCompatActivity.RESULT_OK -> {
                    logi(parentActivity.getString(R.string.update_downloaded))
                }
                AppCompatActivity.RESULT_CANCELED -> {
                    logi(parentActivity.getString(R.string.update_cancelled))
                }
                ActivityResult.RESULT_IN_APP_UPDATE_FAILED -> {
                    //if you want to request the update again just call checkUpdate()
                    appUpdateManager.unregisterListener(installStateUpdatedListener)
                    popupSnackBarForTryUpdate()
                    logi(parentActivity.getString(R.string.update_app_failed))
                    //  handle update failure
                }
            }
        }
    }

    private fun popupSnackBarForCompleteUpdate() {
        parentActivity.apply {
            snackBarWithAction(
                getString(R.string.update_downloaded),
                getString(R.string.restart)
            ) { appUpdateManager.completeUpdate() }
        }
    }

    private fun popupSnackBarForTryUpdate() {
        parentActivity.apply {
            snackBarWithAction(
                getString(R.string.update_app_failed),
                getString(R.string.try_update)
            ) { checkUpdate() }
        }
    }

    fun onDestroy() {
        appUpdateManager.unregisterListener(installStateUpdatedListener)
    }

}