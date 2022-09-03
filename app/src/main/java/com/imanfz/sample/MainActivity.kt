package com.imanfz.sample

import com.imanfz.sample.databinding.ActivityMainBinding
import com.imanfz.utils.base.BaseActivity
import com.imanfz.utils.extension.logi
import com.imanfz.utils.extension.longToast

class MainActivity : BaseActivity<ActivityMainBinding>() {
    override fun setupView() {
        super.setupView()
        longToast("Hello World")
        logi("Hello World")
    }
}