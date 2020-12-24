package com.dimi.advnotes.presentation.common

import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.ItemTouchHelper.ACTION_STATE_DRAG
import androidx.recyclerview.widget.RecyclerView

class ListItemTouchHelper<T> constructor(
    private val classInstance: Class<T>,
    private val swipeCondition: () -> Boolean = { false },
    private val swipeEnabled: Boolean = false,
    private val onSwiped: ((Int) -> Unit) = {},
    private val dragEnabled: Boolean = false,
    private val onDragMoved: ((Int, Int) -> Unit) = { _: Int, _: Int -> }
) : ItemTouchHelper.SimpleCallback(
    ItemTouchHelper.UP or ItemTouchHelper.DOWN,
    ItemTouchHelper.LEFT or ItemTouchHelper.RIGHT
) {

    /**
     * Called when ItemTouchHelper wants to move the dragged item from its old position to
     * the new position.
     *
     * @param recyclerView The RecyclerView to which ItemTouchHelper is attached to.
     * @param viewHolder The ViewHolder which is being dragged by the user.
     * @param target The ViewHolder over which the currently active item is being dragged.
     * @return True if the viewHolder has been moved to the adapter position.
     * @see ItemTouchHelper.SimpleCallback.onMove
     */
    override fun onMove(
        recyclerView: RecyclerView,
        viewHolder: RecyclerView.ViewHolder,
        target: RecyclerView.ViewHolder
    ): Boolean {
        val holderPositionFrom = viewHolder.bindingAdapterPosition
        val holderPositionTo = target.bindingAdapterPosition

        onDragMoved(holderPositionFrom, holderPositionTo)
        // no notify necessary, coz submitList in diffUtil do that for us
        return true
    }

    override fun onSelectedChanged(viewHolder: RecyclerView.ViewHolder?, actionState: Int) {
        super.onSelectedChanged(viewHolder, actionState)
        if (actionState == ACTION_STATE_DRAG) {
            viewHolder?.itemView?.alpha = 0.5f
        }
    }

    override fun clearView(recyclerView: RecyclerView, viewHolder: RecyclerView.ViewHolder) {
        super.clearView(recyclerView, viewHolder)
        viewHolder.itemView.alpha = 1.0f
    }

    override fun isItemViewSwipeEnabled(): Boolean {
        return swipeCondition() && swipeEnabled
    }

    override fun isLongPressDragEnabled(): Boolean {
        return dragEnabled
    }

    override fun getSwipeDirs(
        recyclerView: RecyclerView,
        viewHolder: RecyclerView.ViewHolder
    ): Int {
        if (!swipeEnabled) return ItemTouchHelper.ACTION_STATE_IDLE
        if (!classInstance.isAssignableFrom(viewHolder.javaClass)) return ItemTouchHelper.ACTION_STATE_IDLE
        return super.getSwipeDirs(recyclerView, viewHolder)
    }

    override fun getDragDirs(recyclerView: RecyclerView, viewHolder: RecyclerView.ViewHolder): Int {
        if (!dragEnabled) return ItemTouchHelper.ACTION_STATE_IDLE
        if (!classInstance.isAssignableFrom(viewHolder.javaClass)) return ItemTouchHelper.ACTION_STATE_IDLE
        return super.getDragDirs(recyclerView, viewHolder)
    }

    /**
     * Called when a ViewHolder is swiped by the user.
     *
     * @param viewHolder The ViewHolder which has been swiped by the user.
     * @param direction The direction to which the ViewHolder is swiped. It is one of
     * [ItemTouchHelper.UP], [ItemTouchHelper.DOWN], [ItemTouchHelper.LEFT] or [ItemTouchHelper.RIGHT].
     * @see ItemTouchHelper.SimpleCallback.onSwiped
     */
    override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
        onSwiped(viewHolder.bindingAdapterPosition)
    }
}