package com.imanfz.utility.base

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.viewbinding.ViewBinding
import com.imanfz.utility.extension.getBinding

/**
 * Created by Iman Faizal on 30/Aug/2022
 **/

abstract class BaseActivity<B: ViewBinding>: AppCompatActivity() {

    lateinit var binding : B

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if (::binding.isInitialized.not()) {
            binding = getBinding()
            setContentView(binding.root)
        }
        setupView()
        setupObserver()
        setupListener()
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return true
    }

    open fun setupView() {}

    open fun setupObserver() {}

    open fun setupListener() {}

}