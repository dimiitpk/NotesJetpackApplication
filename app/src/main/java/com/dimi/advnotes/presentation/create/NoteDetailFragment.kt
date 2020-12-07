package com.dimi.advnotes.presentation.create

import android.graphics.Color
import android.os.Bundle
import android.view.View
import androidx.activity.addCallback
import androidx.core.widget.doOnTextChanged
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.navigation.navGraphViewModels
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupWithNavController
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.RecyclerView
import androidx.transition.Slide
import androidx.transition.TransitionManager
import com.dimi.advnotes.R
import com.dimi.advnotes.databinding.FragmentNoteDetailBinding
import com.dimi.advnotes.presentation.common.AreYouSureCallback
import com.dimi.advnotes.presentation.common.DialogColorCaptured
import com.dimi.advnotes.presentation.common.base.BaseFragment
import com.dimi.advnotes.presentation.common.extensions.collectWhenStarted
import com.dimi.advnotes.presentation.common.extensions.observe
import com.dimi.advnotes.presentation.create.adapter.CheckItemListAdapter
import com.dimi.advnotes.util.Constants
import com.google.android.material.transition.MaterialArcMotion
import com.google.android.material.transition.MaterialContainerTransform
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.ExperimentalCoroutinesApi
import java.util.*

@ExperimentalCoroutinesApi
@AndroidEntryPoint
class NoteDetailFragment :
    BaseFragment<FragmentNoteDetailBinding>(R.layout.fragment_note_detail) {

    private lateinit var viewAdapter: CheckItemListAdapter

    private val viewModel: NoteDetailViewModel by navGraphViewModels(R.id.detail_nav) {
        defaultViewModelProviderFactory
    }

    private val args: NoteDetailFragmentArgs by navArgs()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if (args.noteId != Constants.INVALID_PRIMARY_KEY) {
            var color: Int = R.color.colorSurface
            viewModel.note.value?.color?.let { index ->
                val listOfAvailableColors =
                    resources.getIntArray(R.array.note_background_colors)
                color = listOfAvailableColors[index]
            }
            sharedElementEnterTransition = MaterialContainerTransform().apply {
                drawingViewId = R.id.nav_host_fragment
                duration = resources.getInteger(R.integer.note_motion_duration_large).toLong()
                excludeTarget(R.id.check_items_recycler_view, true)
                scrimColor = Color.TRANSPARENT
                setAllContainerColors(requireContext().getColor(color))
            }
        } else {
            // need to change to enter
            sharedElementEnterTransition = MaterialContainerTransform().apply {
                drawingViewId = R.id.nav_host_fragment
                setPathMotion(MaterialArcMotion())
                duration = resources.getInteger(R.integer.note_motion_duration_large).toLong()
                scrimColor = Color.TRANSPARENT
                containerColor = requireContext().getColor(R.color.colorSurface)
                startContainerColor = requireContext().getColor(R.color.colorSecondary)
                endContainerColor = requireContext().getColor(R.color.colorSurface)
            }
        }
    }

    override fun onInitDataBinding() {

        initViewAdapter()

        viewBinding.apply {
            viewModel = this@NoteDetailFragment.viewModel

            checkItemsRecyclerView.apply {
                adapter = viewAdapter
                itemAnimator = object : DefaultItemAnimator() {
                    override fun canReuseUpdatedViewHolder(viewHolder: RecyclerView.ViewHolder): Boolean {
                        return true
                    }
                }
            }

        //    includedDetailToolbar.detailToolbar.setNavigationOnClickListener { navigateBack() }

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

    private fun initViewAdapter() {
        if (!::viewAdapter.isInitialized)
            viewAdapter = CheckItemListAdapter(viewModel, viewLifecycleOwner)
        else viewAdapter.lifecycleOwner = viewLifecycleOwner
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val navController = findNavController()
        val appBarConfiguration = AppBarConfiguration(navController.graph)
        viewBinding.includedDetailToolbar.detailToolbar.setupWithNavController(navController, appBarConfiguration)

        viewLifecycleOwner.observe(viewModel.state, ::onViewStateChange)
        viewModel.loadNote(args.noteId)

        viewModel.note.observe(viewLifecycleOwner, {
            it?.let { note ->
                viewAdapter.submitList(note.checkItems)
            }
        })

        viewLifecycleOwner.lifecycleScope
            .collectWhenStarted(viewModel.event, ::onViewEventChange)

        overrideBackPressed()
    }

    private fun forceFocusOnInputField() {
        viewBinding.inputField.apply {
            requestFocus()
            doOnTextChanged { text, _, _, _ ->
                setSelection(text?.length ?: 0)
            }
            uiController.showSoftKeyboard(this)
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
                    viewModel.deleteConfirmed()
                }

                override fun cancel() {
                    // nothing
                }
            }
        )
    }

    private fun onViewStateChange(viewState: NoteDetailViewState) {
        when (viewState) {
            NoteDetailViewState.NonFocusable -> {
                viewModel.setInitialViewState()
            }
            else -> {
            }
        }
    }

    private fun onViewEventChange(viewEvent: NoteDetailViewEvent) {
        when (viewEvent) {
            is NoteDetailViewEvent.OpenColorPicker ->
                openColorPicker()
            is NoteDetailViewEvent.OpenMoreOptionsDialog ->
                findNavController()
                    .navigate(
                        NoteDetailFragmentDirections.actionNoteCreateFragmentToNoteSettingsDialogFragment()
                    )
            is NoteDetailViewEvent.CancellingReminderDialog ->
                reminderCancellationConfirmation()
            is NoteDetailViewEvent.OpenReminderDialog ->
                findNavController()
                    .navigate(
                        NoteDetailFragmentDirections.actionNoteCreateFragmentToNoteReminderDialog()
                    )
            is NoteDetailViewEvent.NavigateBack ->
                findNavController().popBackStack()
            is NoteDetailViewEvent.CopyNote ->
                uiController.showToast(resources.getString(R.string.successfully_copied_note_event_message))
            is NoteDetailViewEvent.ConfirmDelete ->
                openDeleteDialog()
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
        uiController.hideSoftKeyboard()
    }

    override fun onDestroyView() {
        viewAdapter.lifecycleOwner = null
        viewBinding.checkItemsRecyclerView.adapter = null
        super.onDestroyView()
    }
}
