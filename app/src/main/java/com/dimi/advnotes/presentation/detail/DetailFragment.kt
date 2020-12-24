package com.dimi.advnotes.presentation.detail

import android.graphics.Color
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import androidx.activity.addCallback
import androidx.appcompat.widget.PopupMenu
import androidx.core.widget.doOnTextChanged
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.RecyclerView
import androidx.transition.Slide
import androidx.transition.Transition
import androidx.transition.doOnEnd
import com.dimi.advnotes.R
import com.dimi.advnotes.databinding.FragmentDetailBinding
import com.dimi.advnotes.domain.model.Reminder
import com.dimi.advnotes.presentation.common.AreYouSureCallback
import com.dimi.advnotes.presentation.common.DialogColorCaptured
import com.dimi.advnotes.presentation.common.base.BaseFragment
import com.dimi.advnotes.presentation.common.extensions.collectWhenStarted
import com.dimi.advnotes.presentation.common.extensions.hideKeyboard
import com.dimi.advnotes.presentation.common.extensions.themeColor
import com.dimi.advnotes.presentation.detail.adapter.CheckItemListAdapter
import com.dimi.advnotes.presentation.detail.adapter.holders.CheckItemViewHolder
import com.dimi.advnotes.presentation.list.ARCHIVED_NOTE_NAV_KEY
import com.dimi.advnotes.presentation.list.DELETED_NOTE_NAV_KEY
import com.dimi.advnotes.presentation.common.ListItemTouchHelper
import com.dimi.advnotes.presentation.common.extensions.showKeyboard
import com.dimi.advnotes.util.Constants
import com.google.android.material.transition.MaterialArcMotion
import com.google.android.material.transition.MaterialContainerTransform
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.ExperimentalCoroutinesApi
import java.util.*

const val REMINDER_BUNDLE_KEY =
    "com.dimi.advnotes.presentation.create.DetailFragment.timeInMillis"

@ExperimentalCoroutinesApi
@AndroidEntryPoint
class DetailFragment :
    BaseFragment<FragmentDetailBinding>(
        R.layout.fragment_detail
    ),
    PopupMenu.OnMenuItemClickListener {

    private lateinit var viewAdapter: CheckItemListAdapter

    private val viewModel: DetailViewModel by viewModels()

    private val args: DetailFragmentArgs by navArgs()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if (args.noteId != Constants.INVALID_PRIMARY_KEY) {
            sharedElementEnterTransition = MaterialContainerTransform().apply {
                drawingViewId = R.id.nav_host_fragment
                duration = resources.getInteger(R.integer.note_motion_duration_large).toLong()
                excludeTarget(R.id.check_items_recycler_view, true)
                scrimColor = Color.TRANSPARENT
                setAllContainerColors(requireContext().themeColor(R.attr.colorSurface))
            }
        } else {
            sharedElementEnterTransition = MaterialContainerTransform().apply {
                drawingViewId = R.id.nav_host_fragment
                duration = resources.getInteger(R.integer.note_motion_duration_large).toLong()
                scrimColor = Color.TRANSPARENT
                setPathMotion(MaterialArcMotion())
                containerColor = requireContext().themeColor(R.attr.colorSurface)
                startContainerColor = requireContext().themeColor(R.attr.colorSecondary)
                endContainerColor = requireContext().themeColor(R.attr.colorSurface)
            }
            sharedElementReturnTransition = Slide().apply {
                duration = resources.getInteger(R.integer.note_motion_duration_medium).toLong()
                addTarget(R.id.detail_container)
            }
        }
    }

    private val itemTouchHelper by lazy {
        ItemTouchHelper(
            ListItemTouchHelper(
                classInstance = CheckItemViewHolder::class.java,
                dragEnabled = true,
                onDragMoved = { indexFrom: Int, indexTo: Int ->
                    viewModel.swapCheckItems(indexFrom, indexTo)
                }
            )
        )
    }

    override fun onInitDataBinding() {

        viewBinding.viewModel = viewModel

        setupRecyclerView()
        setupToolbar()
        setupBottomAppBar()
        setupCheckItemsPopupMenu()

        viewBinding.apply {

            /**
             * when fab in previous fragment is clicked
             * meaning creating item not editing
             *  - 1. setting new transition name
             *  - 2. force focus on input body editText
             */
            if (args.noteId == Constants.INVALID_PRIMARY_KEY) {
                detailContainer.transitionName =
                    resources.getString(R.string.transition_fab_to_detail_name)
                forceFocusOnInputField()
            }
        }
    }

    private fun setupRecyclerView() {
        initViewAdapter()

        viewBinding.checkItemsRecyclerView.apply {
            adapter = viewAdapter
            itemAnimator = object : DefaultItemAnimator() {
                override fun canReuseUpdatedViewHolder(viewHolder: RecyclerView.ViewHolder): Boolean {
                    return true
                }
            }
            itemTouchHelper.attachToRecyclerView(this)
        }
    }

    private fun setupToolbar() {
        viewBinding.detailToolbar.apply {
            setNavigationOnClickListener { navigateBack() }
            setOnMenuItemClickListener { menuItem ->
                when (menuItem.itemId) {
                    R.id.action_reminder -> {
                        viewModel.note.value?.let {
                            findNavController()
                                .navigate(
                                    DetailFragmentDirections.actionToReminderDialog(
                                        reminder = it.reminder
                                    )
                                )
                        }
                        true
                    }
                    R.id.action_pin_unpin -> {
                        viewModel.pinOrUnpin()
                        true
                    }
                    R.id.action_archive -> {
                        archiveNote()
                        true
                    }
                    else -> false
                }
            }
        }
    }

    private fun setupBottomAppBar() {
        viewBinding.bottomAppBar.apply {
            setNavigationOnClickListener {
                openColorPicker()
            }
            setOnMenuItemClickListener { menuItem ->
                when (menuItem.itemId) {
                    R.id.action_copy -> {
                        viewModel.copyNote()
                        true
                    }
                    R.id.action_delete -> {
                        openDeleteDialog()
                        true
                    }
                    else -> false
                }
            }
        }
    }

    private fun setupCheckItemsPopupMenu() {
        viewBinding.checkListOptions.setOnClickListener { view ->
            PopupMenu(view.context, view).apply {
                this@DetailFragment.viewModel.checkItems.value?.let { list ->
                    list.filter { it.checked }.run {
                        menu.add(
                            Menu.FIRST,
                            R.id.action_hide_checked,
                            Menu.NONE,
                            R.string.hide_check_boxes
                        )
                        if (isNotEmpty()) {
                            menu.add(
                                Menu.FIRST,
                                R.id.action_uncheck_all,
                                Menu.NONE,
                                R.string.uncheck_all
                            )
                            menu.add(
                                Menu.FIRST,
                                R.id.action_delete_checked,
                                Menu.NONE,
                                R.string.delete_checked
                            )
                        }
                    }
                    setOnMenuItemClickListener(this@DetailFragment)
                    show()
                }
            }
        }
    }

    private fun initViewAdapter() {
        if (!::viewAdapter.isInitialized)
            viewAdapter = CheckItemListAdapter(viewModel, {
                viewAdapter.clearFocus()
                itemTouchHelper.startDrag(it)
            }, viewLifecycleOwner)
        else viewAdapter.lifecycleOwner = viewLifecycleOwner
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewModel.loadNote(args.noteId)

        viewModel.checkItems.observe(viewLifecycleOwner) {
            viewAdapter.submitList(it)
        }

        viewLifecycleOwner.lifecycleScope
            .collectWhenStarted(viewModel.event, ::onViewEventChange)

        overrideBackPressed()

        observeReminderChanges()
    }

    private fun observeReminderChanges() {
        val savedStateHandle = findNavController().currentBackStackEntry?.savedStateHandle
        savedStateHandle?.getLiveData<Reminder>(REMINDER_BUNDLE_KEY)
            ?.observe(viewLifecycleOwner) {
                it?.let {
                    println("DELETE REMINDER $it")
                    if (it.repeating == null && it.timeInMillis == null)
                        viewModel.deleteReminder()
                    else
                        viewModel.setReminder(it)
                }
            }
    }

    private fun forceFocusOnInputField() {
        (sharedElementEnterTransition as? Transition)?.doOnEnd {
            viewBinding.inputField.apply {
                requestFocus()
                doOnTextChanged { text, _, _, _ ->
                    setSelection(text?.length ?: 0)
                }
                showKeyboard()
            }
        }
    }

    private fun overrideBackPressed() {
        requireActivity().onBackPressedDispatcher.addCallback(this) {
            navigateBack()
        }
    }

    private fun navigateBack() {
        viewModel.saveNote()
    }

    private fun openDeleteDialog() {
        uiController.showAreYouSureDialog(
            message = resources.getString(R.string.delete_note_event_message),
            areYouSureCallback = object : AreYouSureCallback {
                override fun proceed() {
                    deleteConfirmed()
                }

                override fun cancel() {
                    // nothing
                }
            }
        )
    }

    private fun archiveNote() {
        findNavController()
            .previousBackStackEntry
            ?.savedStateHandle
            ?.set(ARCHIVED_NOTE_NAV_KEY, viewModel.note.value?.copy())

        viewModel.archiveNote()
    }

    fun deleteConfirmed() {
        findNavController()
            .previousBackStackEntry
            ?.savedStateHandle
            ?.set(DELETED_NOTE_NAV_KEY, viewModel.note.value)

        viewModel.deleteConfirmed()
    }

    private fun onViewEventChange(viewEvent: DetailViewEvent) {
        when (viewEvent) {
            is DetailViewEvent.CancellingReminderDialog ->
                reminderCancellationConfirmation()
            is DetailViewEvent.NavigateBack ->
                findNavController().popBackStack()
            is DetailViewEvent.CopyNote ->
                uiController.showToast(resources.getString(R.string.successfully_copied_note_event_message))
        }
    }

    private fun reminderCancellationConfirmation() {
        uiController.showAreYouSureDialog(
            message = resources.getString(R.string.cancel_alarm_event_message),
            areYouSureCallback = object : AreYouSureCallback {
                override fun proceed() {
                    viewModel.clearReminder()
                }

                override fun cancel() {
                    // not needed.
                }
            }
        )
    }

    private fun openColorPicker() {
        uiController.showColorChoseDialog(object : DialogColorCaptured {
            override fun onCapturedColor(color: Int) {
                viewModel.setNoteColor(color)
            }
        })
    }

    override fun onStop() {
        super.onStop()
        view?.hideKeyboard()
    }

    override fun onDestroyView() {
        viewAdapter.lifecycleOwner = null
        viewBinding.checkItemsRecyclerView.adapter = null
        super.onDestroyView()
    }

    override fun onMenuItemClick(item: MenuItem?): Boolean {
        return item?.let {
            when (it.itemId) {
                R.id.action_hide_checked -> {
                    viewModel.removeAllCheckItems()
                    true
                }
                R.id.action_uncheck_all -> {
                    viewModel.unCheckCheckItems()
                    true
                }
                R.id.action_delete_checked -> {
                    viewModel.removeCheckedCheckItems()
                    true
                }
                else -> false
            }
        } ?: false
    }
}
