package com.imanfz.utils.base

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import androidx.viewbinding.ViewBinding
import com.imanfz.utils.extension.getBinding

/**
 * Created by Iman Faizal on 31/Aug/2022
 **/

open class BaseDialogFragment<B : ViewBinding> : DialogFragment() {

    private var _binding: B? = null
    val binding: B
        get() = _binding ?: throw RuntimeException(
            "Should only use binding after onCreateView and before onDestroyView"
        )

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = getBinding(inflater, container)
        return binding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    open fun setupView() {}

    open fun setupObserver() {}

    open fun setupListener() {}
}