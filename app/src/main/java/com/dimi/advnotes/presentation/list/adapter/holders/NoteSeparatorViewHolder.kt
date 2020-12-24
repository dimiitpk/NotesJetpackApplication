package com.dimi.advnotes.presentation.list.adapter.holders

import android.view.LayoutInflater
import com.dimi.advnotes.databinding.ListItemNoteSeparatorBinding
import com.dimi.advnotes.presentation.common.base.BaseViewHolder

class NoteSeparatorViewHolder(
    inflater: LayoutInflater
) : BaseViewHolder<ListItemNoteSeparatorBinding>(
    binding = ListItemNoteSeparatorBinding.inflate(inflater)
) {
    fun bind(description: String) {
        binding.description = description
        binding.executePendingBindings()
    }
}