package com.dimi.advnotes.presentation.list.adapter.holders

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.lifecycle.LifecycleOwner
import com.dimi.advnotes.databinding.ListItemCheckItemSimpleBinding
import com.dimi.advnotes.databinding.ListItemNoteBinding
import com.dimi.advnotes.domain.model.CheckItem
import com.dimi.advnotes.domain.model.Note
import com.dimi.advnotes.presentation.common.base.BaseViewHolder
import com.dimi.advnotes.presentation.list.adapter.NoteListAdapter
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview

@ExperimentalCoroutinesApi
@FlowPreview
class NoteViewHolder(
    private val lifecycleOwner: LifecycleOwner? = null,
    private val interaction: NoteListAdapter.Interaction? = null,
    private val inflater: LayoutInflater
) : BaseViewHolder<ListItemNoteBinding>(
    binding = ListItemNoteBinding.inflate(inflater)
) {

    init {
        binding.setClickListener {
            val position = bindingAdapterPosition
            binding.note?.let { note ->
                interaction?.onItemSelected(binding.card, position, note)
            }
        }
        binding.setLongClickListener {
            val position = bindingAdapterPosition
            binding.card
            binding.note?.let { note ->
                interaction?.onItemSelectedByLongClick(position, note)
            }
            true
        }
    }

    fun bind(item: Note) {
        binding.apply {
            note = item
            lifecycleOwner = this@NoteViewHolder.lifecycleOwner

            if (item.checkItems.isNotEmpty()) {
                checkItemsList.run {
                    if (childCount == item.checkItems.size) return@run
                    removeAllViews()
                    item.checkItems.forEach {
                        addView(inflateSimpleCheckItemView(it, this))
                    }
                }
            }

            executePendingBindings()
        }
    }

    private fun inflateSimpleCheckItemView(item: CheckItem, root: ViewGroup) =
        ListItemCheckItemSimpleBinding.inflate(
            inflater,
            root,
            false
        ).apply {
            checkItem = item
        }.root
}
