package com.imanfz.utility.dialog

import android.app.Dialog
import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.Window
import com.imanfz.utility.R

/**
 * Created by Iman Faizal on 30/Aug/2022
 **/

class LoadingDialog(
    context: Context
) : Dialog(context) {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        requestWindowFeature(Window.FEATURE_NO_TITLE)
        window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
//        window?.setDimAmount(0F)
        setContentView(R.layout.fragment_loading_dialog)
        setCancelable(false)
    }

}