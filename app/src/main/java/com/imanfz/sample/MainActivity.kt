package com.imanfz.sample

import com.imanfz.androidutils.base.BaseActivity
import com.imanfz.androidutils.extension.logi
import com.imanfz.androidutils.extension.longToast
import com.imanfz.sample.databinding.ActivityMainBinding

class MainActivity : BaseActivity<ActivityMainBinding>() {
    override fun setupView() {
        super.setupView()
        longToast("Hello World")
        logi("Hello World")
    }
}