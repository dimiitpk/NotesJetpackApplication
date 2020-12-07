package com.dimi.advnotes.presentation.list.adapter.holders

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.MutableLiveData
import com.dimi.advnotes.databinding.NoteListItemBinding
import com.dimi.advnotes.databinding.SimpleCheckListItemBinding
import com.dimi.advnotes.domain.model.CheckItem
import com.dimi.advnotes.domain.model.Note
import com.dimi.advnotes.presentation.common.base.BaseViewHolder
import com.dimi.advnotes.presentation.list.NoteListViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview

@ExperimentalCoroutinesApi
@FlowPreview
class NoteViewHolder(
    private val lifecycleOwner: LifecycleOwner? = null,
    private val inflater: LayoutInflater
) : BaseViewHolder<NoteListItemBinding>(
    binding = NoteListItemBinding.inflate(inflater)
) {

    init {
        binding.setClickListener {
            val position = bindingAdapterPosition
            binding.note?.let { note ->
                binding.viewModel?.clickOnNoteView(note, position)
            }
        }
    }

    fun bind(vM: NoteListViewModel, item: Note) {
        binding.apply {
            viewModel = vM
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
        SimpleCheckListItemBinding.inflate(
            inflater,
            root,
            false
        ).apply {
            checkItem = item
        }.root
}
