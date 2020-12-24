package com.dimi.advnotes.presentation.list

import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import androidx.activity.addCallback
import androidx.appcompat.view.ActionMode
import androidx.core.view.doOnPreDraw
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.Navigator
import androidx.navigation.fragment.FragmentNavigatorExtras
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.RecyclerView
import com.dimi.advnotes.R
import com.dimi.advnotes.databinding.FragmentListBinding
import com.dimi.advnotes.domain.model.Note
import com.dimi.advnotes.presentation.MainActivity
import com.dimi.advnotes.presentation.common.ActionModeCallback
import com.dimi.advnotes.presentation.common.AreYouSureCallback
import com.dimi.advnotes.presentation.common.DialogColorCaptured
import com.dimi.advnotes.presentation.common.LAYOUT_MODE_LINEAR
import com.dimi.advnotes.presentation.common.ListItemTouchHelper
import com.dimi.advnotes.presentation.common.TodoCallback
import com.dimi.advnotes.presentation.common.base.BaseFragment
import com.dimi.advnotes.presentation.common.extensions.collectWhenStarted
import com.dimi.advnotes.presentation.common.extensions.hideKeyboard
import com.dimi.advnotes.presentation.common.extensions.invisible
import com.dimi.advnotes.presentation.common.extensions.setIconByCondition
import com.dimi.advnotes.presentation.common.extensions.themeDrawable
import com.dimi.advnotes.presentation.list.adapter.NoteListAdapter
import com.dimi.advnotes.presentation.list.adapter.holders.NoteViewHolder
import com.dimi.advnotes.presentation.list.state.MultiSelectionState
import com.dimi.advnotes.util.Constants.INVALID_PRIMARY_KEY
import com.google.android.material.card.MaterialCardView
import com.google.android.material.transition.MaterialElevationScale
import com.google.android.material.transition.MaterialFadeThrough
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.collectLatest

const val DELETED_NOTE_NAV_KEY = "navigation.savedstatehandle.note.deleted"
const val ARCHIVED_NOTE_NAV_KEY = "navigation.savedstatehandle.note.archived"

@FlowPreview
@ExperimentalCoroutinesApi
@AndroidEntryPoint
class ListFragment :
    BaseFragment<FragmentListBinding>(R.layout.fragment_list),
    ActionModeCallback.Listener,
    NoteListAdapter.Interaction {

    private lateinit var viewAdapter: NoteListAdapter

    private val args: ListFragmentArgs by navArgs()

    val viewModel: ListViewModel by viewModels()

    private var multiSelectionMode: ActionMode? = null

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

        viewModel.setListType(args.listType)

        viewBinding.viewModel = viewModel
        setupRecyclerView()
        setupFab()
        setupToolbar()
    }

    private fun setupRecyclerView() {

        initViewAdapter()

        viewBinding.noteListRecyclerView.apply {
            adapter = viewAdapter
            adapter?.stateRestorationPolicy =
                RecyclerView.Adapter.StateRestorationPolicy.PREVENT_WHEN_EMPTY
            ItemTouchHelper(
                ListItemTouchHelper(
                    classInstance = NoteViewHolder::class.java,
                    swipeEnabled = true,
                    swipeCondition = {
                        multiSelectionMode == null && args.listType.isNotes()
                    },
                    onSwiped = { position ->
                        viewAdapter.getNoteOrNull(position)?.let {
                            viewModel.archiveNote(note = it)
                            showArchivedUndoSnackbar()
                        }
                    }
                )
            ).attachToRecyclerView(this)
        }
    }

    private fun setupFab() {
        viewBinding.floatingActionButton.apply {
            if (args.listType.isArchive())
                invisible()
            setOnClickListener {
                val string = resources.getString(R.string.transition_fab_to_detail_name)
                val extras =
                    FragmentNavigatorExtras(viewBinding.floatingActionButton to string)
                navigateToDetailFragment(
                    extras
                )
            }
        }
    }

    private fun setupToolbar() {
        viewBinding.toolbar.apply {
            setNavigationOnClickListener { uiController.openDrawer() }
            title = args.listType.title
            setOnMenuItemClickListener { menuItem ->
                when (menuItem.itemId) {
                    R.id.action_layout_change -> {
                        viewModel.changeViewLayout()
                        true
                    }
                    R.id.action_search -> {
                        findNavController().navigate(
                            ListFragmentDirections.actionToSearchFragment()
                        )
                        true
                    }
                    else -> false
                }
            }
        }
    }

    private fun initViewAdapter() {
        if (!::viewAdapter.isInitialized)
            viewAdapter = NoteListAdapter(this, viewLifecycleOwner)
        else viewAdapter.lifecycleOwner = viewLifecycleOwner
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        postponeEnterTransition()
        view.doOnPreDraw { startPostponedEnterTransition() }

        viewLifecycleOwner.lifecycleScope.launchWhenCreated {
            viewModel.noteList.collectLatest {
                viewAdapter.submitData(it)
            }
        }

        viewModel.userPreferences.observe(viewLifecycleOwner, {
            it?.layoutMode?.let { layoutMode ->
                setLayoutModeIcon(layoutMode)
            }
        })

        viewLifecycleOwner.lifecycleScope
            .collectWhenStarted(viewModel.multiSelectionState, ::onMultiSelectionModeChange)

        viewLifecycleOwner.lifecycleScope
            .collectWhenStarted(viewModel.event, ::onViewEventChange)

        checkForDeletedOrArchivedNotes()

        overrideBackPressed()
    }

    private fun setLayoutModeIcon(layoutMode: Int) {
        viewBinding.toolbar.setIconByCondition(
            R.id.action_layout_change,
            (layoutMode == LAYOUT_MODE_LINEAR),
            R.drawable.ic_grid_view_24dp,
            R.drawable.ic_list_view_24dp
        )
    }

    private fun checkForDeletedOrArchivedNotes() {
        val savedStateHandle = findNavController().currentBackStackEntry?.savedStateHandle

        savedStateHandle?.get<Note>(DELETED_NOTE_NAV_KEY)?.let { note ->
            viewModel.setPendingNote(note)
            showDeletedUndoSnackbar()
            savedStateHandle.remove<Note>(DELETED_NOTE_NAV_KEY)
        }
        savedStateHandle?.get<Note>(ARCHIVED_NOTE_NAV_KEY)?.let { note ->
            viewModel.setPendingNote(note)
            showArchivedUndoSnackbar()
            savedStateHandle.remove<Note>(ARCHIVED_NOTE_NAV_KEY)
        }
    }

    private fun overrideBackPressed() {
        requireActivity().onBackPressedDispatcher.addCallback(
            this,
            enabled = args.listType.isNotNotes()
        ) {
            (activity as? MainActivity)?.viewBinding?.rootNavigationView?.setCheckedItem(R.id.home_frag)
            findNavController().navigate(
                ListFragmentDirections.actionGlobalListFragment(ListType.NOTES)
            )
        }
    }

    private fun clearActionMode() {
        multiSelectionMode?.let { actionMode ->
            actionMode.finish()
            uiController.clearActionMode()
            multiSelectionMode = null
        }
    }

    private fun onMultiSelectionModeChange(multiSelectionState: MultiSelectionState) {
        when (multiSelectionState) {
            is MultiSelectionState.Disabled -> {
                clearActionMode()
            }
            is MultiSelectionState.Enabled -> {
                val numberOfSelectedNotes = multiSelectionState.size
                if (numberOfSelectedNotes > 0 && multiSelectionMode == null)
                    multiSelectionMode = uiController.startActionMode(this)

                multiSelectionMode?.title = numberOfSelectedNotes.toString()
            }
        }
    }

    private fun onViewEventChange(viewData: ListViewEvent) {
        when (viewData) {
            is ListViewEvent.RefreshNotes ->
                viewAdapter.notifySelectedItemsChanged()
        }
    }

    private fun navigateToDetailFragment(
        navExtras: Navigator.Extras,
        noteId: Long = INVALID_PRIMARY_KEY
    ) {
        findNavController().navigate(
            ListFragmentDirections.actionToDetailFragment(
                noteId
            ),
            navExtras
        )
    }

    override fun onResume() {
        super.onResume()
        view?.hideKeyboard()
    }

    private fun archiveSelectedNotes() {
        viewModel.archiveSelectedNotes()
        showArchivedUndoSnackbar()
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
                    viewModel.deleteSelectedNotes()
                    showDeletedUndoSnackbar()
                }

                override fun cancel() {
                    // nothing
                }
            }
        )
    }

    private fun showDeletedUndoSnackbar() {
        uiController.showUndoSnackBar(
            message = R.string.delete_successful_event_message,
            onUndoCallback = object : TodoCallback {
                override fun execute() {
                    viewModel.undoPendingDeleteNotes()
                }
            },
            onDismissCallback = object : TodoCallback {
                override fun execute() {
                    viewModel.clearPendingNotes()
                }
            }
        )
    }

    private fun showArchivedUndoSnackbar() {
        uiController.showUndoSnackBar(
            message = if (args.listType.isArchive())
                R.string.unarchived_successful_event_message
            else
                R.string.archived_successful_event_message,
            onUndoCallback = object : TodoCallback {
                override fun execute() {
                    viewModel.undoPendingArchiveNotes()
                }
            },
            onDismissCallback = object : TodoCallback {
                override fun execute() {
                    viewModel.clearPendingNotes()
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
        clearActionMode()
        super.onDestroyView()
    }

    override fun onItemSelected(view: MaterialCardView, position: Int, item: Note) {
        if (viewModel.isMultiSelectionEnabled())
            viewModel.selectOrUnSelectNote(item)
        else {
            val string = resources.getString(R.string.transition_card_detail_name)
            val extras = FragmentNavigatorExtras(view to string)
            navigateToDetailFragment(
                extras,
                noteId = item.id
            )
        }
    }

    override fun onItemSelectedByLongClick(position: Int, item: Note) {
        viewModel.selectOrUnSelectNote(item)
    }

    override fun onCreateActionMode(mode: ActionMode?) {
        viewBinding.floatingActionButton.hide()
    }

    override fun onPrepareActionMode(mode: ActionMode?, menu: Menu?): Boolean {
        return if (args.listType.isArchive()) {
            mode?.menu?.findItem(R.id.action_archive)?.icon =
                requireContext().themeDrawable(R.drawable.ic_unarchive_24dp)
            true
        } else false
    }

    override fun onDestroyActionMode() {
        viewModel.disableMultiSelectionStateAndClearSelectedNotes()
        viewBinding.floatingActionButton.show()
    }

    override fun onActionItemClicked(item: MenuItem?): Boolean {
        return item?.let { menuItem ->
            when (menuItem.itemId) {
                R.id.action_delete -> {
                    openDeleteDialog()
                    true
                }
                R.id.action_palette -> {
                    openColorPicker()
                    true
                }
                R.id.action_archive -> {
                    archiveSelectedNotes()
                    true
                }
                else -> false
            }
        } ?: false
    }

    override fun getActionModeMenuRes() = R.menu.action_mode_menu
}