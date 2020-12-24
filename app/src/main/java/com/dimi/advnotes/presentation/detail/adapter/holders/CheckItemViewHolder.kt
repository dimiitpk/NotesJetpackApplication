package com.dimi.advnotes.presentation.detail.adapter.holders

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.MotionEvent
import androidx.core.widget.doOnTextChanged
import androidx.lifecycle.LifecycleOwner
import androidx.recyclerview.widget.RecyclerView
import com.dimi.advnotes.databinding.ListItemCheckItemBinding
import com.dimi.advnotes.domain.model.CheckItem
import com.dimi.advnotes.presentation.common.base.BaseViewHolder
import com.dimi.advnotes.presentation.common.bindings.strikeThrough
import com.dimi.advnotes.presentation.common.bindings.visibleElseInvisible
import com.dimi.advnotes.presentation.common.extensions.showKeyboard
import com.dimi.advnotes.presentation.detail.DetailViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi

@SuppressLint("ClickableViewAccessibility")
@ExperimentalCoroutinesApi
class CheckItemViewHolder(
    private val lifecycleOwner: LifecycleOwner? = null,
    private val dragAction: (RecyclerView.ViewHolder) -> Unit,
    inflater: LayoutInflater
) : BaseViewHolder<ListItemCheckItemBinding>(
    binding = ListItemCheckItemBinding.inflate(inflater)
) {

    init {
        binding.apply {
            setClickListener {
                checkItem?.let {
                    viewModel?.removeCheckItem(it)
                }
            }
            checkbox.setOnClickListener {
                checkItem?.let {
                    textInputEditText.strikeThrough(it.checked)
                }
            }
            textInputEditText.apply {
                setOnFocusChangeListener { _, hasFocus ->
                    deleteItem.visibleElseInvisible = hasFocus
                }
                doOnTextChanged { text, _, _, _ ->
                    checkbox.isEnabled = text?.isNotEmpty() ?: false
                }
            }
            dragIndicator.setOnTouchListener { v, event ->
                if (event.actionMasked == MotionEvent.ACTION_DOWN)
                    dragAction(this@CheckItemViewHolder)
                true
            }
        }
    }

    fun bind(vM: DetailViewModel, item: CheckItem) {
        binding.apply {
            viewModel = vM
            checkItem = item
            lifecycleOwner = this@CheckItemViewHolder.lifecycleOwner
            executePendingBindings()

            checkbox.isEnabled = item.text.isNotEmpty()
        }

        if (item.focus) {
            item.focus = false
            requestInputFieldFocus()
        }
    }

    private fun requestInputFieldFocus() {
        binding.textInputEditText.postDelayed({
            binding.textInputEditText.apply {
                if (requestFocus()) {
                    showKeyboard()
                    setSelection(text?.length ?: 0)
                }
            }
        }, 50)
    }
}
