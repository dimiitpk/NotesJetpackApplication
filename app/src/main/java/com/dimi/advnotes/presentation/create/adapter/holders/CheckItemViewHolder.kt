package com.dimi.advnotes.presentation.create.adapter.holders

import android.content.Context
import android.view.LayoutInflater
import android.view.inputmethod.InputMethodManager
import androidx.lifecycle.LifecycleOwner
import com.dimi.advnotes.databinding.CheckListItemBinding
import com.dimi.advnotes.domain.model.CheckItem
import com.dimi.advnotes.presentation.common.base.BaseViewHolder
import com.dimi.advnotes.presentation.create.NoteDetailViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi

@ExperimentalCoroutinesApi
class CheckItemViewHolder(
    private val lifecycleOwner: LifecycleOwner? = null,
    inflater: LayoutInflater
) : BaseViewHolder<CheckListItemBinding>(
    binding = CheckListItemBinding.inflate(inflater)
) {

    init {
        binding.apply {
            setClickListener {
                checkItem?.let {
                    viewModel?.removeCheckItem(it)
                }
            }
        }
    }

    fun bind(vM: NoteDetailViewModel, item: CheckItem, focus: Boolean) {
        binding.apply {
            viewModel = vM
            checkItem = item
            lifecycleOwner = this@CheckItemViewHolder.lifecycleOwner
            executePendingBindings()
        }

        if (item.focus && focus) {
            item.focus = false
            requestInputFieldFocus()
        }
    }

    private fun requestInputFieldFocus() {
        binding.textInputEditText.postDelayed({
            binding.textInputEditText.apply {
                if (requestFocus()) {
                    val inputMethodManager = context.getSystemService(
                        Context.INPUT_METHOD_SERVICE
                    ) as InputMethodManager
                    inputMethodManager.showSoftInput(this, InputMethodManager.SHOW_IMPLICIT)
                    setSelection(text?.length ?: 0)
                }
            }
        }, 50)
    }
}
