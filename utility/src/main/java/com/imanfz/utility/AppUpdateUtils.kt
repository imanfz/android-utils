package com.imanfz.utility

import android.app.Activity
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.LifecycleOwner
import com.google.android.play.core.appupdate.AppUpdateInfo
import com.google.android.play.core.appupdate.AppUpdateManager
import com.google.android.play.core.appupdate.AppUpdateManagerFactory
import com.google.android.play.core.appupdate.testing.FakeAppUpdateManager
import com.google.android.play.core.install.InstallStateUpdatedListener
import com.google.android.play.core.install.model.ActivityResult
import com.google.android.play.core.install.model.AppUpdateType
import com.google.android.play.core.install.model.InstallStatus
import com.google.android.play.core.install.model.UpdateAvailability
import com.imanfz.utility.extension.*

/**
 * Created by Iman Faizal on 08/Apr/2022
 **/

class AppUpdateUtils(
    private val parentActivity: Activity,
    private val debugMode: Boolean = false
): LifecycleEventObserver {

    private var appUpdateManager: AppUpdateManager

    private val appUpdateRequestCode = 500
    private var currentType = AppUpdateType.FLEXIBLE
    private val listener = InstallStateUpdatedListener { state ->
        when(state.installStatus()) {
            InstallStatus.FAILED, InstallStatus.UNKNOWN -> popupSnackBarForTryUpdate()
            InstallStatus.DOWNLOADING -> {
                val bytesDownloaded = state.bytesDownloaded()
                val totalBytesToDownload = state.totalBytesToDownload()
                // Show update progress bar.
                logd("Bytes Downloaded: $bytesDownloaded")
                logd("Total Bytes To Download: $totalBytesToDownload")
            }
            InstallStatus.DOWNLOADED -> popupSnackBarForCompleteUpdate()
            InstallStatus.PENDING -> logd("Install pending")
            InstallStatus.CANCELED -> logd("Install cancelled")
            InstallStatus.INSTALLED -> logd("Installed")
            InstallStatus.INSTALLING -> logd("Installing")
            else -> {}
        }
    }

    init {
        parentActivity.addLifecycleObserver(this)
        appUpdateManager = if (debugMode) {
            FakeAppUpdateManager(parentActivity.applicationContext).apply {
                setUpdateAvailable(2, currentType) // app version code
                setTotalBytesToDownload(20000000L)
            }
        } else AppUpdateManagerFactory.create(parentActivity.applicationContext)
    }

    private fun checkUpdate() {
        appUpdateManager.apply {
            // Before starting an update, register a listener for updates.
            registerListener(listener)

            // Start an update.
            appUpdateInfo.addOnSuccessListener { info ->
                logd("checkUpdateAvailability: ${info.updateAvailability() == UpdateAvailability.UPDATE_AVAILABLE}")

                // Check if update is available
                if (info.updateAvailability() == UpdateAvailability.UPDATE_AVAILABLE) { // UPDATE IS AVAILABLE
                    if (debugMode) {
                        if (info.updateAvailability() == UpdateAvailability.UPDATE_AVAILABLE) {
                            testUpdate()
                        }
                    } else {
                        val clientVersionStalenessDays = info.clientVersionStalenessDays()
                        clientVersionStalenessDays?.let {
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
                        }
                    }
                }
            }
        }
    }

    private fun startUpdate(info: AppUpdateInfo, type: Int) {
        appUpdateManager.startUpdateFlowForResult(info, type, parentActivity, appUpdateRequestCode)
        currentType = type
    }

    private fun onResume() {
        appUpdateManager.appUpdateInfo.addOnSuccessListener { info ->
            if (currentType == AppUpdateType.FLEXIBLE) {
                // If the update is downloaded but not installed, notify the user to complete the update.
                if (info.installStatus() == InstallStatus.DOWNLOADED) {
                    popupSnackBarForCompleteUpdate()
                }
            } else if (currentType == AppUpdateType.IMMEDIATE) {
                // for AppUpdateType.IMMEDIATE only, already executing updater
                if (info.updateAvailability() == UpdateAvailability.DEVELOPER_TRIGGERED_UPDATE_IN_PROGRESS) {
                    startUpdate(info, AppUpdateType.IMMEDIATE)
                }
            }
        }
    }

    fun onActivityResult(requestCode: Int, resultCode: Int) {
        logd("onActivityResult: Update flow result code: $resultCode" )
        if (requestCode == appUpdateRequestCode) {
            when (resultCode) {
                AppCompatActivity.RESULT_OK -> {
                    logd(parentActivity.getString(R.string.update_downloaded))
                }
                AppCompatActivity.RESULT_CANCELED -> {
                    logd(parentActivity.getString(R.string.update_cancelled))
                }
                ActivityResult.RESULT_IN_APP_UPDATE_FAILED -> {
                    //if you want to request the update again just call checkUpdate()
                    appUpdateManager.unregisterListener(listener)
                    //  handle update failure
                    popupSnackBarForTryUpdate()
                    logd(parentActivity.getString(R.string.update_app_failed))
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

    private fun onDestroy() {
        appUpdateManager.unregisterListener(listener)
    }

    override fun onStateChanged(source: LifecycleOwner, event: Lifecycle.Event) {
        when(event) {
            Lifecycle.Event.ON_START -> checkUpdate()
            Lifecycle.Event.ON_RESUME -> onResume()
            Lifecycle.Event.ON_DESTROY -> onDestroy()
            else -> {}
        }
    }

    private fun testUpdate() {
        val fakeAppUpdate = appUpdateManager as FakeAppUpdateManager
        if (currentType == AppUpdateType.IMMEDIATE) {
            if (fakeAppUpdate.isImmediateFlowVisible) {
                fakeAppUpdate.userAcceptsUpdate()
                fakeAppUpdate.downloadStarts()
                fakeAppUpdate.downloadCompletes()
                popupSnackBarForCompleteUpdate()
            }
        } else {
            if (fakeAppUpdate.isConfirmationDialogVisible) {
                fakeAppUpdate.userAcceptsUpdate()
                fakeAppUpdate.downloadStarts()
                fakeAppUpdate.downloadCompletes()
                fakeAppUpdate.completeUpdate()
                fakeAppUpdate.installCompletes()
            }
        }
    }

}