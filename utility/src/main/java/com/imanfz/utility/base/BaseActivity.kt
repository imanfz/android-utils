package com.imanfz.utility.base

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.viewbinding.ViewBinding
import com.imanfz.utility.dialog.LoadingDialog
import com.imanfz.utility.extension.getBinding

/**
 * Created by Iman Faizal on 30/Aug/2022
 **/

abstract class BaseActivity<B: ViewBinding>: AppCompatActivity() {

    lateinit var binding : B
    private lateinit var loadingDialog: LoadingDialog

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if (::binding.isInitialized.not()) {
            binding = getBinding()
            setContentView(binding.root)
        }
        loadingDialog = LoadingDialog(this)
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

    fun showLoading() {
        if (!loadingDialog.isShowing) loadingDialog.show()
    }

    fun hideLoading() {
        if (loadingDialog.isShowing) loadingDialog.dismiss()
    }


}