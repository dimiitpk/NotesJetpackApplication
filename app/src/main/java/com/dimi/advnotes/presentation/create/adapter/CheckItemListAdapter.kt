package com.dimi.advnotes.presentation.create.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.lifecycle.LifecycleOwner
import androidx.recyclerview.widget.RecyclerView
import com.dimi.advnotes.domain.model.CheckItem
import com.dimi.advnotes.presentation.common.base.BaseListAdapter
import com.dimi.advnotes.presentation.create.NoteDetailViewModel
import com.dimi.advnotes.presentation.create.adapter.holders.CheckItemViewHolder
import kotlinx.coroutines.ExperimentalCoroutinesApi

@ExperimentalCoroutinesApi
class CheckItemListAdapter constructor(
    private val viewModel: NoteDetailViewModel,
    var lifecycleOwner: LifecycleOwner? = null
) : BaseListAdapter<CheckItem>(
    itemsSame = { old, new -> old.id == new.id },
    contentsSame = { old, new -> old == new }
) {
    // initial focus is disabled, when user click on add item then it's set to true
    private var focus = false

    override fun onCreateViewHolder(
        parent: ViewGroup,
        inflater: LayoutInflater,
        viewType: Int
    ) = CheckItemViewHolder(lifecycleOwner, inflater)

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val currentItem = getItem(position)
        currentItem.let {
            (holder as CheckItemViewHolder).bind(viewModel, it, focus)
        }
    }

    override fun submitList(list: List<CheckItem>?) {
        if (itemCount > 0)
            focus = true
        super.submitList(list)
    }
}