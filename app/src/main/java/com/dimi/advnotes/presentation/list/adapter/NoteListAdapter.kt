package com.dimi.advnotes.presentation.list.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.lifecycle.LifecycleOwner
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import com.dimi.advnotes.R
import com.dimi.advnotes.presentation.common.base.BasePagedListAdapter
import com.dimi.advnotes.presentation.list.NoteListViewModel
import com.dimi.advnotes.presentation.list.adapter.holders.NoteSeparatorViewHolder
import com.dimi.advnotes.presentation.list.adapter.holders.NoteViewHolder
import com.dimi.advnotes.presentation.list.adapter.model.UiModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview

@FlowPreview
@ExperimentalCoroutinesApi
class NoteListAdapter constructor(
    private var viewModel: NoteListViewModel? = null,
    var lifecycleOwner: LifecycleOwner? = null
) : BasePagedListAdapter<UiModel>(
    itemsSame = { old, new ->
        (old is UiModel.NoteModel && new is UiModel.NoteModel && old.note.id == new.note.id) ||
                (old is UiModel.SeparatorModel && new is UiModel.SeparatorModel && old.description == new.description)
    },
    contentsSame = { old, new -> old == new }
) {

    override fun onCreateViewHolder(parent: ViewGroup, inflater: LayoutInflater, viewType: Int) =
        when (viewType) {
            R.layout.note_list_item -> NoteViewHolder(lifecycleOwner, inflater)
            else -> {
                NoteSeparatorViewHolder(inflater)
            }
        }

    override fun onViewAttachedToWindow(holder: RecyclerView.ViewHolder) {
        if (holder is NoteSeparatorViewHolder)
            (holder.itemView.layoutParams
                    as StaggeredGridLayoutManager.LayoutParams).isFullSpan = true
        super.onViewAttachedToWindow(holder)
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        getItem(position)?.let { uiModel ->
            when (uiModel) {
                is UiModel.NoteModel -> {
                    viewModel?.let { (holder as NoteViewHolder).bind(it, uiModel.note) }
                }
                is UiModel.SeparatorModel -> {
                    val viewHolder = (holder as NoteSeparatorViewHolder)
                    viewHolder.bind(uiModel.description)
                }
            }
        }
    }

    override fun getItemViewType(position: Int): Int {
        return when (getItem(position)) {
            is UiModel.NoteModel -> R.layout.note_list_item
            is UiModel.SeparatorModel -> R.layout.note_separator_list_item
            null -> throw UnsupportedOperationException("Unknown view")
        }
    }

    fun clearAdapter() {
        lifecycleOwner = null
    }

    fun notifySelectedItemsChanged() {
        actionOnSelectedNote {
            notifyItemChanged(it)
        }
    }

    fun notifySelectedItemsRemoved() {
        actionOnSelectedNote {
            notifyItemRemoved(it)
        }
    }

    fun notifySelectedItemsInserted() {
        actionOnSelectedNote {
            notifyItemInserted(it)
        }
    }

    private inline fun actionOnSelectedNote(action: (Int) -> Unit) {
        snapshot().items.forEachIndexed { index, uiModel ->
            if (uiModel is UiModel.NoteModel) {
                val note = uiModel.note
                if (note.isSelected()) {
                    action(index)
                }
            }
        }
    }
}