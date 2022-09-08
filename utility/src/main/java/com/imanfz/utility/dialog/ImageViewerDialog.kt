package com.imanfz.utility.dialog

import android.content.DialogInterface
import android.net.Uri
import android.os.Bundle
import android.webkit.URLUtil
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.commit
import com.imanfz.utility.base.BaseDialogFragment
import com.imanfz.utility.databinding.FragmentImageViewerDialogBinding
import com.imanfz.utility.extension.TAG
import com.imanfz.utility.extension.loadImage
import com.imanfz.utility.extension.loge
import com.imanfz.utility.extension.setSafeOnClickListener

class ImageViewerDialog: BaseDialogFragment<FragmentImageViewerDialogBinding>() {

    companion object {
        const val EXTRA_IMAGE = "extra.image"

        @JvmStatic
        fun newInstance(image: String) = ImageViewerDialog().apply {
            arguments = Bundle().apply {
                putString(EXTRA_IMAGE, image)
            }
        }
    }

    private lateinit var image: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.apply {
            image = getString(EXTRA_IMAGE, "")
        }
    }

    override fun setupView() {
        super.setupView()
        isCancelable = false
        binding.apply {
            btnClose.setSafeOnClickListener {
                dismissAllowingStateLoss()
            }

            imageView.apply {
                if (URLUtil.isValidUrl(image)) loadImage(image)
                else loadImage(Uri.parse(image))
            }

        }
    }

    override fun show(manager: FragmentManager, tag: String?) {
        try {
            manager.commit(true) {
                setReorderingAllowed(true)
                add(this@ImageViewerDialog, tag ?: this@ImageViewerDialog.TAG)
                addToBackStack(null)
            }
        } catch (ignored: IllegalStateException) {
            loge("show: ${ignored.localizedMessage}" )
        }
    }

    override fun onDismiss(dialog: DialogInterface) {
        try {
            childFragmentManager.apply {
                if (backStackEntryCount > 0) popBackStack()
            }
            parentFragmentManager.apply {
                val fragment = findFragmentByTag(this@ImageViewerDialog.TAG)
                if (fragment != null) {
                    commit(true) {
                        remove(fragment)
                    }
                    if (backStackEntryCount > 0) popBackStack()
                }
            }
        } catch (ignored: IllegalStateException) {
            loge("onDismiss: ${ignored.localizedMessage}" )
        }
    }
}