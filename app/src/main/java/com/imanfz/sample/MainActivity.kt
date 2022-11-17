package com.imanfz.sample

import android.content.Intent
import com.imanfz.sample.databinding.ActivityMainBinding
import com.imanfz.utility.AppUpdateUtils
import com.imanfz.utility.NetworkStatus
import com.imanfz.utility.NetworkStatusUtils
import com.imanfz.utility.base.BaseActivity
import com.imanfz.utility.extension.*
import com.imanfz.utility.ui.ReadMoreTextView

class MainActivity : BaseActivity<ActivityMainBinding>() {

    private lateinit var appUpdateUtils: AppUpdateUtils

    override fun setupView() {
        super.setupView()
        appUpdateUtils = AppUpdateUtils(this, BuildConfig.DEBUG)
        logi("Hello World")
        binding.apply {
            imageView.loadImage("https://images-wixmp-ed30a86b8c4ca887773594c2.wixmp.com/f/faa48d2d-12c2-43d1-bf23-b5e99857825b/ddanutv-39cde392-7484-42ec-8b83-f00e58094746.png/v1/fill/w_800,h_450,q_80,strp/dance_of_lights_by_ellysiumn_ddanutv-fullview.jpg?token=eyJ0eXAiOiJKV1QiLCJhbGciOiJIUzI1NiJ9.eyJzdWIiOiJ1cm46YXBwOjdlMGQxODg5ODIyNjQzNzNhNWYwZDQxNWVhMGQyNmUwIiwiaXNzIjoidXJuOmFwcDo3ZTBkMTg4OTgyMjY0MzczYTVmMGQ0MTVlYTBkMjZlMCIsIm9iaiI6W1t7InBhdGgiOiJcL2ZcL2ZhYTQ4ZDJkLTEyYzItNDNkMS1iZjIzLWI1ZTk5ODU3ODI1YlwvZGRhbnV0di0zOWNkZTM5Mi03NDg0LTQyZWMtOGI4My1mMDBlNTgwOTQ3NDYucG5nIiwiaGVpZ2h0IjoiPD00NTAiLCJ3aWR0aCI6Ijw9ODAwIn1dXSwiYXVkIjpbInVybjpzZXJ2aWNlOmltYWdlLndhdGVybWFyayJdLCJ3bWsiOnsicGF0aCI6Ilwvd21cL2ZhYTQ4ZDJkLTEyYzItNDNkMS1iZjIzLWI1ZTk5ODU3ODI1YlwvZWxseXNpdW1uLTQucG5nIiwib3BhY2l0eSI6OTUsInByb3BvcnRpb25zIjowLjQ1LCJncmF2aXR5IjoiY2VudGVyIn19.3USm-rYv-0y6spWqrtA0V2B4-tpDl1qMHX25VTjDd94")
            btnLoading.setSafeOnClickListener {
                showLoading()
                delayOnLifecycle {
                    logi("dismiss")
                    hideLoading()
                }
            }
            readMoreTextView.changeListener = object : ReadMoreTextView.ChangeListener {
                override fun onStateChange(state: ReadMoreTextView.State) {
                    logd("ReadMoreTextView: $state")
                }
            }
            loadingButton.apply {
                setSafeOnClickListener {
                    showLoading()
                    delayOnLifecycle {
                        hideLoading()
                    }
                }
            }
            loadingButton2.apply {
                setSafeOnClickListener {
                    showLoading()
                    delayOnLifecycle {
                        hideLoading()
                    }
                }
            }
            btnMove.setSafeOnClickListener {
                startActivity(Intent(this@MainActivity, SecondActivity::class.java))
            }
        }
    }

    override fun setupObserver() {
        super.setupObserver()
        NetworkStatusUtils(this).observe(this) {
            when (it) {
                NetworkStatus.Available -> loge("Internet Connected!")
                else -> loge("No Internet!!!")
            }
        }
    }

    @Suppress("DEPRECATION")
    @Deprecated("Deprecated in Java")
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        appUpdateUtils.onActivityResult(requestCode, resultCode)
    }
}