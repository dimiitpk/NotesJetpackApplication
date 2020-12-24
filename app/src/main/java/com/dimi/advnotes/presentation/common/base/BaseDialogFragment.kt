package com.dimi.advnotes.presentation.common.base

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.LayoutRes
import androidx.annotation.StyleRes
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import androidx.fragment.app.DialogFragment
import com.dimi.advnotes.presentation.MainActivity
import com.dimi.advnotes.presentation.common.UIController
import java.lang.ClassCastException

abstract class BaseDialogFragment<B : ViewDataBinding>(
    @LayoutRes private val layoutId: Int
) : DialogFragment() {

    private var _viewBinding: B? = null

    /**
     * Usage available from onViewCreated until onDestroyView(before super call)
     */
    val viewBinding
        get() = _viewBinding!!

    lateinit var uiController: UIController

    abstract fun onInitDataBinding()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _viewBinding = DataBindingUtil.inflate(inflater, layoutId, container, false)
        viewBinding.lifecycleOwner = viewLifecycleOwner
        isCancelable = false
        return viewBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        onInitDataBinding()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setStyle(
            STYLE_NO_TITLE,
            0
        )
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        activity?.let { activity ->
            if (activity is MainActivity) {
                try {
                    uiController = context as UIController
                } catch (e: ClassCastException) {
                    e.printStackTrace()
                }
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _viewBinding = null
    }
}
