package com.dimi.advnotes.presentation.list

import android.os.Bundle
import android.view.ActionMode
import android.view.View
import androidx.core.view.doOnPreDraw
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.Navigator
import androidx.navigation.fragment.FragmentNavigatorExtras
import androidx.navigation.fragment.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupWithNavController
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import com.dimi.advnotes.R
import com.dimi.advnotes.databinding.FragmentNoteListBinding
import com.dimi.advnotes.presentation.common.AreYouSureCallback
import com.dimi.advnotes.presentation.common.DialogColorCaptured
import com.dimi.advnotes.presentation.common.SnackbarUndoCallback
import com.dimi.advnotes.presentation.common.TodoCallback
import com.dimi.advnotes.presentation.common.base.BaseFragment
import com.dimi.advnotes.presentation.common.bindings.visible
import com.dimi.advnotes.presentation.common.extensions.collectWhenStarted
import com.dimi.advnotes.presentation.common.extensions.onQueryTextChanged
import com.dimi.advnotes.presentation.list.adapter.NoteListAdapter
import com.dimi.advnotes.presentation.list.adapter.holders.NoteViewHolder
import com.dimi.advnotes.presentation.list.state.MultiSelectionState
import com.dimi.advnotes.util.Constants.INVALID_PRIMARY_KEY
import com.google.android.material.transition.MaterialElevationScale
import com.google.android.material.transition.MaterialFadeThrough
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.collectLatest

@FlowPreview
@ExperimentalCoroutinesApi
@AndroidEntryPoint
class NoteListFragment : BaseFragment<FragmentNoteListBinding>(R.layout.fragment_note_list) {

    private lateinit var viewAdapter: NoteListAdapter

    val viewModel: NoteListViewModel by viewModels()

    private var multiSelectionMode: ActionMode? = null

    private val actionModeCallback by lazy {
        ActionModeCallback(
            menuRes = R.menu.action_mode_menu,
            onCreate = {
                viewBinding.floatingActionButton.visible = false
            },
            onDestroy = {
                viewModel.disableMultiSelectionStateAndClearSelectedNotes()
                viewBinding.floatingActionButton.visible = true
            },
            itemWithActions = arrayOf(
                R.id.action_delete to { openDeleteDialog() },
                R.id.action_palette to { openColorPicker() }
            )
        )
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        exitTransition = MaterialElevationScale(false).apply {
            duration = resources.getInteger(R.integer.note_motion_duration_large).toLong()
        }
        reenterTransition = MaterialElevationScale(true).apply {
            duration = resources.getInteger(R.integer.note_motion_duration_large).toLong()
        }
        enterTransition = MaterialFadeThrough().apply {
            duration = resources.getInteger(R.integer.note_motion_duration_large).toLong()
        }
    }

    override fun onInitDataBinding() {

        initViewAdapter()

        viewBinding.apply {

            this.viewModel = this@NoteListFragment.viewModel
            noteListRecyclerView.apply {
                adapter = viewAdapter
                adapter?.stateRestorationPolicy =
                    RecyclerView.Adapter.StateRestorationPolicy.PREVENT_WHEN_EMPTY
            }

//            floatingActionButton.setOnClickListener {
//                this@NoteListFragment.viewModel.openCreateNoteView()
//            }
        }

        initSearchView()
    }

    private fun initViewAdapter() {
        if (!::viewAdapter.isInitialized)
            viewAdapter = NoteListAdapter(viewModel, viewLifecycleOwner)
        else viewAdapter.lifecycleOwner = viewLifecycleOwner
    }

    private fun initSearchView() {
        viewBinding.includedToolbar.searchView.onQueryTextChanged {
            viewModel.setSearchQuery(it)
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val navController = findNavController()
        val appBarConfiguration = AppBarConfiguration(navController.graph)
        viewBinding.includedToolbar.toolbar.setupWithNavController(navController, appBarConfiguration)

        postponeEnterTransition()
        view.doOnPreDraw { startPostponedEnterTransition() }

        viewLifecycleOwner.lifecycleScope.launchWhenCreated {
            viewModel.noteList.collectLatest {
                viewAdapter.submitData(it)
            }
        }

        viewLifecycleOwner.lifecycleScope
            .collectWhenStarted(viewModel.multiSelectionState, ::onMultiSelectionModeChange)

        viewLifecycleOwner.lifecycleScope
            .collectWhenStarted(viewModel.event, ::onViewEventChange)
    }

    private fun onMultiSelectionModeChange(multiSelectionState: MultiSelectionState) {
        when (multiSelectionState) {
            is MultiSelectionState.Disabled -> {
                multiSelectionMode?.let { actionMode ->
                    actionMode.finish()
                    multiSelectionMode = null
                }
            }
            is MultiSelectionState.Enabled -> {
                val numberOfSelectedNotes = multiSelectionState.size
                if (numberOfSelectedNotes > 0 && multiSelectionMode == null) {
                    multiSelectionMode =
                        viewBinding.includedToolbar.toolbar.startActionMode(actionModeCallback)
                }

                multiSelectionMode?.title =
                    resources.getString(R.string.selected, numberOfSelectedNotes)
            }
        }
    }

    private fun onViewEventChange(viewData: NoteListViewEvent) {
        when (viewData) {
            is NoteListViewEvent.OpenCreateNoteView -> {
                val string = resources.getString(R.string.transition_fab_to_detail_name)
                val extras = FragmentNavigatorExtras(viewBinding.floatingActionButton to string)
                navigateToDetailFragment(
                    extras
                )
            }
            is NoteListViewEvent.OpenEditNoteView -> {
                val string = resources.getString(R.string.transition_card_detail_name)
                viewData.position.let { position ->
                    if (position != -1) {
                        viewAdapter.getChildViewHolder(position)?.let { viewHolder ->
                            val view = (viewHolder as NoteViewHolder).binding.card
                            val extras = FragmentNavigatorExtras(view to string)
                            navigateToDetailFragment(
                                extras,
                                noteId = viewData.note.id
                            )
                        }
                    }
                }
            }
            is NoteListViewEvent.RefreshNotes ->
                viewAdapter.notifySelectedItemsChanged()
        }
    }

    private fun navigateToDetailFragment(
        navExtras: Navigator.Extras,
        noteId: Long = INVALID_PRIMARY_KEY
    ) {
        findNavController().navigate(
            NoteListFragmentDirections.actionNoteListFragmentToDetailNav(
                noteId
            ),
            navExtras
        )
    }

    override fun onResume() {
        super.onResume()
        viewBinding.run {
            focusableView.requestFocus()
            includedToolbar.searchView.apply {
                setQuery(this@NoteListFragment.viewModel.getSearchQuery(), false)
            }
        }
        uiController.hideSoftKeyboard()
    }

    private fun openDeleteDialog() {
        if (viewModel.isSelectedNotesListEmpty()) {
            uiController.showToast(getString(R.string.zero_selected_notes_error))
            return
        }
        uiController.showAreYouSureDialog(
            message = "Deleting ${viewModel.getSelectedNotes().size} selected notes.",
            areYouSureCallback = object : AreYouSureCallback {
                override fun proceed() {
                    viewModel.deleteSelectedNotesTemporary()
                    showUndoSnackBar()
                }

                override fun cancel() {
                    // nothing
                }
            }
        )
    }

    private fun showUndoSnackBar() {
        uiController.showUndoSnackBar(
            view = viewBinding.noteListContainer,
            message = R.string.delete_successful_event_message,
            snackbarUndoCallback = object : SnackbarUndoCallback {
                override fun undo() {
                    viewModel.undoPendingDeleteNotes()
                }
            },
            onDismissCallback = object : TodoCallback {
                override fun execute() {
                    viewModel.deleteSelectedNotesPermanently()
                }
            }
        )
    }

    private fun openColorPicker() {
        if (viewModel.isSelectedNotesListEmpty()) {
            uiController.showToast(getString(R.string.zero_selected_notes_error))
            return
        }
        uiController.showColorChoseDialog(object : DialogColorCaptured {
            override fun onCapturedColor(color: Int) {
                viewModel.updateAndSaveColorSelectedNotes(color)
            }
        })
    }

    override fun onDestroyView() {
        viewAdapter.clearAdapter()
        viewBinding.noteListRecyclerView.adapter = null
        multiSelectionMode = null
        super.onDestroyView()
    }
}