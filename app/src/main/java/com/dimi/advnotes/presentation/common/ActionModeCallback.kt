package com.dimi.advnotes.presentation.common

import android.view.Menu
import android.view.MenuItem
import androidx.appcompat.view.ActionMode

class ActionModeCallback(
    var listener: Listener? = null
) : ActionMode.Callback {
    override fun onCreateActionMode(mode: ActionMode?, menu: Menu?): Boolean {
        mode?.let {
            listener?.getActionModeMenuRes()?.let { menuRes ->
                val inflater = it.menuInflater
                inflater.inflate(menuRes, menu)
                listener?.onCreateActionMode(mode)
            }
        }
        return true
    }

    override fun onPrepareActionMode(mode: ActionMode?, menu: Menu?): Boolean {
        return listener?.onPrepareActionMode(mode, menu) ?: false
    }

    override fun onActionItemClicked(mode: ActionMode?, item: MenuItem?): Boolean {
        return listener?.onActionItemClicked(item) ?: false
    }

    override fun onDestroyActionMode(mode: ActionMode?) {
        listener?.onDestroyActionMode()
    }

    interface Listener {
        fun getActionModeMenuRes(): Int
        fun onCreateActionMode(mode: ActionMode?)
        fun onPrepareActionMode(mode: ActionMode?, menu: Menu?): Boolean
        fun onActionItemClicked(item: MenuItem?): Boolean
        fun onDestroyActionMode()
    }
}