package com.imanfz.sample

import androidx.lifecycle.coroutineScope
import com.imanfz.sample.databinding.ActivityMainBinding
import com.imanfz.utility.base.BaseActivity
import com.imanfz.utility.dialog.QRISDialog
import com.imanfz.utility.extension.*
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class MainActivity : BaseActivity<ActivityMainBinding>() {
    override fun setupView() {
        super.setupView()
        longToast("Hello World")
        logi("Hello World")
        binding.apply {
            imageView.loadImage("https://picsum.photos/id/1039/6945/4635")
            btnLoading.setSafeOnClickListener {
                showLoading()
                lifecycle.coroutineScope.apply {
                    launch {
                        delay(3000)
                        hideLoading()
                    }
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