package com.dimi.advnotes.presentation.detail.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.lifecycle.LifecycleOwner
import androidx.recyclerview.widget.RecyclerView
import com.dimi.advnotes.domain.model.CheckItem
import com.dimi.advnotes.presentation.common.base.BaseListAdapter
import com.dimi.advnotes.presentation.common.extensions.hideKeyboard
import com.dimi.advnotes.presentation.detail.DetailViewModel
import com.dimi.advnotes.presentation.detail.adapter.holders.CheckItemViewHolder
import kotlinx.coroutines.ExperimentalCoroutinesApi

@ExperimentalCoroutinesApi
class CheckItemListAdapter constructor(
    private val viewModel: DetailViewModel,
    private val dragAction: (RecyclerView.ViewHolder) -> Unit,
    var lifecycleOwner: LifecycleOwner? = null
) : BaseListAdapter<CheckItem>(
    itemsSame = { old, new -> old.id == new.id },
    contentsSame = { old, new -> old == new }
) {

    override fun onCreateViewHolder(
        parent: ViewGroup,
        inflater: LayoutInflater,
        viewType: Int
    ) = CheckItemViewHolder(lifecycleOwner, dragAction, inflater)

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val currentItem = getItem(position)
        currentItem.let {
            (holder as CheckItemViewHolder).bind(viewModel, it)
        }
    }

    override fun onCurrentListChanged(
        previousList: MutableList<CheckItem>,
        currentList: MutableList<CheckItem>
    ) {
        // on remove item lose focus
        if ((previousList.size - currentList.size) == 1)
            clearFocus()
        super.onCurrentListChanged(previousList, currentList)
    }

    fun clearFocus() {
        recyclerView?.rootView?.clearFocus()
        recyclerView?.rootView?.hideKeyboard()
    }
}