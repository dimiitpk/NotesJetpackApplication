package com.dimi.advnotes.presentation.list

import android.view.ActionMode
import android.view.Menu
import android.view.MenuItem
import androidx.annotation.MenuRes

class ActionModeCallback(
    @MenuRes val menuRes: Int,
    val onCreate: () -> Unit,
    val onDestroy: () -> Unit,
    vararg val itemWithActions: Pair<Int, () -> Unit>
) : ActionMode.Callback {
    override fun onCreateActionMode(mode: ActionMode?, menu: Menu?): Boolean {
        mode?.let {
            val inflater = it.menuInflater
            inflater.inflate(menuRes, menu)
            onCreate()
        }
        return true
    }

    override fun onPrepareActionMode(mode: ActionMode?, menu: Menu?): Boolean {
        return false
    }

    override fun onActionItemClicked(mode: ActionMode?, item: MenuItem?): Boolean {
        return item?.itemId?.let { itemId ->
            itemWithActions.forEach { (actionId, action) ->
                if( itemId == actionId ) {
                    action()
                    return@let true
                }
            }
            false
        } ?: false
    }

    override fun onDestroyActionMode(mode: ActionMode?) {
        onDestroy()
    }
}