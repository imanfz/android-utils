package com.imanfz.sample

import com.imanfz.sample.databinding.ActivityMainBinding
import com.imanfz.utility.base.BaseActivity
import com.imanfz.utility.dialog.QRISDialog
import com.imanfz.utility.extension.*

class MainActivity : BaseActivity<ActivityMainBinding>() {
    override fun setupView() {
        super.setupView()
        longToast("Hello World")
        logi("Hello World")
        binding.apply {
            imageView.loadImage("https://picsum.photos/id/1039/6945/4635")
            btnLoading.setSafeOnClickListener {
                showLoading()
                delayOnLifecycle() {
                    logi("dismiss")
                    hideLoading()
                }
            }
            btnQris.setSafeOnClickListener {
                QRISDialog().apply {
                    show(supportFragmentManager, TAG)
                }
            }
        }
    }
}