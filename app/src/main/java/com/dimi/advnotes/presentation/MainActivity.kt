package com.dimi.advnotes.presentation

import android.os.Bundle
import androidx.appcompat.view.ActionMode
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import com.dimi.advnotes.AppScopeCoroutineExceptionHandler
import com.dimi.advnotes.AppScopeExceptionUIHandler
import com.dimi.advnotes.R
import com.dimi.advnotes.databinding.ActivityMainBinding
import com.dimi.advnotes.presentation.common.base.BaseActivity
import com.dimi.advnotes.presentation.common.extensions.contentView
import com.dimi.advnotes.presentation.common.ActionModeCallback
import com.dimi.advnotes.presentation.list.ListFragmentDirections
import com.dimi.advnotes.presentation.list.ListType
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity :
    BaseActivity<ActivityMainBinding>(R.layout.activity_main),
    AppScopeExceptionUIHandler {

    val viewBinding: ActivityMainBinding by contentView(R.layout.activity_main)

    private var actionModeCallback: ActionModeCallback? = null

    @Inject
    lateinit var appScopeExceptionHandler: AppScopeCoroutineExceptionHandler

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        appScopeExceptionHandler.appScopeExceptionUIHandler = this

        setupBinding()
    }

    private fun setupBinding() {
        viewBinding.run {
            rootDrawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED)

            rootNavigationView.setCheckedItem(R.id.home_frag)
            rootNavigationView.setNavigationItemSelectedListener { menuItem ->
                when (menuItem.itemId) {
                    R.id.archive_frag -> {
                        navController.navigate(
                            ListFragmentDirections.actionGlobalListFragment(ListType.ARCHIVE)
                        )
                        rootDrawerLayout.close()
                        true
                    }
                    R.id.home_frag -> {
                        navController.navigate(
                            ListFragmentDirections.actionGlobalListFragment(ListType.NOTES)
                        )
                        rootDrawerLayout.close()
                        true
                    }
                    else -> false
                }
            }
        }
    }

    override fun onBackPressed() {
        if (viewBinding.rootDrawerLayout.isDrawerOpen(GravityCompat.START))
            viewBinding.rootDrawerLayout.closeDrawer(GravityCompat.START)
        else
            super.onBackPressed()
    }

    override fun onDestroy() {
        super.onDestroy()
        actionModeCallback = null
        appScopeExceptionHandler.appScopeExceptionUIHandler = null
    }

    override fun handleException(exception: Throwable) {
        exception.message?.let { showSnackBar(it) }
    }

    override fun openDrawer() {
        viewBinding.rootDrawerLayout.open()
    }

    override fun closeDrawer() {
        viewBinding.rootDrawerLayout.close()
    }

    override fun startActionMode(
        listener: ActionModeCallback.Listener
    ): ActionMode? {
        actionModeCallback = ActionModeCallback(listener)
        return startSupportActionMode(actionModeCallback!!)
    }

    override fun clearActionMode() {
        actionModeCallback?.listener = null
        actionModeCallback = null
    }
}