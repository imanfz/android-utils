package com.imanfz.sample

import com.imanfz.sample.databinding.ActivityMainBinding
import com.imanfz.utility.base.BaseActivity
import com.imanfz.utility.extension.logi
import com.imanfz.utility.extension.longToast

class MainActivity : BaseActivity<ActivityMainBinding>() {
    override fun setupView() {
        super.setupView()
        longToast("Hello World")
        logi("Hello World")
    }
}