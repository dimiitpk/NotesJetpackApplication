package com.dimi.advnotes.presentation.common.bindings

import androidx.databinding.BindingAdapter
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import com.dimi.advnotes.presentation.common.recyclerview.RecyclerViewItemDecoration

/**
 * Add an [RecyclerViewItemDecoration] to this RecyclerView. Item decorations can
 * affect both measurement and drawing of individual item views.
 *
 * @param spacingPx Spacing in pixels to set as a item margin.
 * @see RecyclerView.addItemDecoration
 */
@BindingAdapter("itemDecorationSpacing")
fun RecyclerView.setItemDecorationSpacing(spacingPx: Float) {
    val itemDecoration = RecyclerViewItemDecoration(spacingPx.toInt())
    removeItemDecoration(itemDecoration)
    addItemDecoration(itemDecoration)
}

@BindingAdapter("staggeredSpanCount")
fun RecyclerView.staggeredSpanCount(spanCount: Int) {
    if (spanCount < 1 || spanCount > 2) return
    layoutManager?.let {
        (it as StaggeredGridLayoutManager).spanCount = spanCount
    }
    // check for useless call on zero item in RecyclerView
    if (childCount > 0)
        adapter?.notifyDataSetChanged()
}
