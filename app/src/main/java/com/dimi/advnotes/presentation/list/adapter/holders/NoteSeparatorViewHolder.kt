package com.dimi.advnotes.presentation.list.adapter.holders

import android.view.LayoutInflater
import com.dimi.advnotes.databinding.NoteSeparatorListItemBinding
import com.dimi.advnotes.presentation.common.base.BaseViewHolder

class NoteSeparatorViewHolder(
    inflater: LayoutInflater
) : BaseViewHolder<NoteSeparatorListItemBinding>(
    binding = NoteSeparatorListItemBinding.inflate(inflater)
) {
    fun bind(description: String) {
        binding.description = description
        binding.executePendingBindings()
    }
}