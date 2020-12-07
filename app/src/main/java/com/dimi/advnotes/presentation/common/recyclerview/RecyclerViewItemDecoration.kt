package com.dimi.advnotes.presentation.common.recyclerview

import android.graphics.Rect
import android.view.View
import androidx.annotation.VisibleForTesting
import androidx.annotation.VisibleForTesting.PRIVATE
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import kotlin.math.ceil

/**
 * Simple item decoration allows the application to add a special drawing and layout offset
 * to specific item views from the adapter's data set. Support the grid and linear layout.
 *
 * @see RecyclerView.ItemDecoration
 */
class RecyclerViewItemDecoration(
    @VisibleForTesting(otherwise = PRIVATE)
    internal val spacingPx: Int
) : RecyclerView.ItemDecoration() {

    private var anyFullSpanItem: Boolean = false

    /**
     * Retrieve any offsets for the given item.
     *
     * @param outRect Rect to receive the output.
     * @param view The child view to decorate
     * @param parent RecyclerView this ItemDecoration is decorating
     * @param state The current state of RecyclerView.
     * @see RecyclerView.ItemDecoration.getItemOffsets
     */
    override fun getItemOffsets(
        outRect: Rect,
        view: View,
        parent: RecyclerView,
        state: RecyclerView.State
    ) {
        when (val layoutManager = parent.layoutManager) {
            is GridLayoutManager -> configSpacingForGridLayoutManager(
                outRect = outRect,
                layoutManager = layoutManager,
                position = parent.getChildViewHolder(view).bindingAdapterPosition,
                itemCount = state.itemCount
            )
            is StaggeredGridLayoutManager -> configSpacingForStaggeredGridLayoutManager(
                outRect = outRect,
                layoutManager = layoutManager,
                layoutParams = view.layoutParams as StaggeredGridLayoutManager.LayoutParams,
                position = parent.getChildAdapterPosition(view)
            )
            is LinearLayoutManager -> configSpacingForLinearLayoutManager(
                outRect = outRect,
                layoutManager = layoutManager,
                position = parent.getChildViewHolder(view).bindingAdapterPosition,
                itemCount = state.itemCount
            )
        }
    }

    // ============================================================================================
    //  Private configs methods
    // ============================================================================================

    /**
     * Configure spacing for grid layout, given a rectangle.
     *
     * @param outRect Rect to modify.
     * @param layoutManager The currently responsible for layout policy.
     * @param position Position of the item represented by this ViewHolder.
     * @param itemCount The total number of items that can be laid out.
     */
    private fun configSpacingForGridLayoutManager(
        outRect: Rect,
        layoutManager: GridLayoutManager,
        position: Int,
        itemCount: Int
    ) {
        val cols = layoutManager.spanCount
        val rows = ceil(itemCount / cols.toDouble()).toInt()

        outRect.top = spacingPx
        outRect.left = spacingPx
        outRect.right = if (position % cols == cols - 1) spacingPx else 0
        outRect.bottom = if (position / cols == rows - 1) spacingPx else 0
    }

    private fun configSpacingForStaggeredGridLayoutManager(
        outRect: Rect,
        layoutManager: StaggeredGridLayoutManager,
        layoutParams: StaggeredGridLayoutManager.LayoutParams,
        position: Int
    ) {
        outRect.top = spacingPx/2
        outRect.bottom = spacingPx/2
        outRect.right = spacingPx/2
        outRect.left = spacingPx/2
//        val spanCount = layoutManager.spanCount
//        val spanIndex = layoutParams.spanIndex
//
//        layoutParams.isFullSpan.let { isFullSpan ->
//            if (isFullSpan)
//                anyFullSpanItem = isFullSpan
//        }
//
//        if (spanIndex == 0) {
//            // left edge
//            // should write left border
//            outRect.left = spacingPx
//        }
//
//        if (anyFullSpanItem) {
//            // if its first row, first in a row and have full span
//            if (spanIndex == 0 && position == 0)
//                outRect.top = spacingPx
//        } else {
//            // first row and there is no fullSpan on any item
//            // should write top border
//            if (position < spanCount) {
//                outRect.top = spacingPx
//            }
//        }
//
//        outRect.right = spacingPx
//        // if (!(anyFullSpanItem && spanIndex == 0 && position == 0))
//        outRect.bottom = spacingPx
    }

    /**
     * Configure spacing for linear layout, given a rectangle.
     *
     * @param outRect Rect to modify.
     * @param layoutManager The currently responsible for layout policy.
     * @param position Position of the item represented by this ViewHolder.
     * @param itemCount The total number of items that can be laid out.
     */
    private fun configSpacingForLinearLayoutManager(
        outRect: Rect,
        layoutManager: LinearLayoutManager,
        position: Int,
        itemCount: Int
    ) {
        outRect.top = spacingPx
        outRect.left = spacingPx
        if (layoutManager.canScrollHorizontally()) {
            outRect.right = if (position == itemCount - 1) spacingPx else 0
            outRect.bottom = spacingPx
        } else if (layoutManager.canScrollVertically()) {
            outRect.right = spacingPx
            outRect.bottom = if (position == itemCount - 1) spacingPx else 0
        }
    }
}
