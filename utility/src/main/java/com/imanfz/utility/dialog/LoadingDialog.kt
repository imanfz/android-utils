package com.imanfz.utility.dialog

import android.app.Dialog
import android.content.Context
import android.content.DialogInterface
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.Window
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import com.imanfz.utility.R
import com.imanfz.utility.databinding.FragmentLoadingDialogBinding

/**
 * Created by Iman Faizal on 30/Aug/2022
 **/

class LoadingDialog(
    context: Context
) : Dialog(context) {

    private lateinit var binding: FragmentLoadingDialogBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        requestWindowFeature(Window.FEATURE_NO_TITLE)
        window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
//        window?.setDimAmount(0F)
        binding = FragmentLoadingDialogBinding.inflate(LayoutInflater.from(context))
        setContentView(binding.root)
        setCancelable(false)
    }

    override fun onStart() {
        super.onStart()
        binding.loading.startAnimation(AnimationUtils.loadAnimation(context, R.anim.spinner_animation).apply {
            repeatCount = Animation.INFINITE
        })
    }

    override fun setOnDismissListener(listener: DialogInterface.OnDismissListener?) {
        binding.loading.clearAnimation()
        super.setOnDismissListener(listener)
    }

}