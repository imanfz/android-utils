package com.imanfz.sample

import android.content.Intent
import com.imanfz.sample.databinding.ActivityMainBinding
import com.imanfz.utility.NetworkStatus
import com.imanfz.utility.NetworkStatusUtils
import com.imanfz.utility.base.BaseActivity
import com.imanfz.utility.dialog.QRISDialog
import com.imanfz.utility.extension.*
import com.imanfz.utility.ui.ReadMoreTextView

class MainActivity : BaseActivity<ActivityMainBinding>() {
    override fun setupView() {
        super.setupView()
        longToast("Hello World")
        logi("Hello World")
        binding.apply {
            imageView.loadImage("https://picsum.photos/id/1039/6945/4635")
            btnLoading.setSafeOnClickListener {
                showLoading()
                delayOnLifecycle {
                    logi("dismiss")
                    hideLoading()
                }
            }
            btnQris.setSafeOnClickListener {
                QRISDialog.newInstance { result ->
                    shortToast("QRIS: $result")
                }.show(supportFragmentManager, TAG)
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
                NetworkStatus.Unavailable -> loge("No Internet!!!")
            }
        }
    }
}