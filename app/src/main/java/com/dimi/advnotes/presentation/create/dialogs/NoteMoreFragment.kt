package com.dimi.advnotes.presentation.create.dialogs

import android.os.Bundle
import android.view.View
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.navigation.navGraphViewModels
import com.dimi.advnotes.R
import com.dimi.advnotes.databinding.DialogFragmentMoreOptionsBinding
import com.dimi.advnotes.presentation.common.base.BaseBottomSheetDialogFragment
import com.dimi.advnotes.presentation.common.extensions.collectWhenStarted
import com.dimi.advnotes.presentation.common.extensions.observe
import com.dimi.advnotes.presentation.create.NoteDetailViewEvent
import com.dimi.advnotes.presentation.create.NoteDetailViewModel

class NoteMoreFragment :
    BaseBottomSheetDialogFragment<DialogFragmentMoreOptionsBinding>(R.layout.dialog_fragment_more_options) {

    private val viewModel: NoteDetailViewModel by navGraphViewModels(R.id.detail_nav) {
        defaultViewModelProviderFactory
    }

    override fun onInitDataBinding() {
        viewBinding.viewModel = viewModel
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewLifecycleOwner.lifecycleScope.collectWhenStarted(
            viewModel.event, ::onViewEventChange
        )
    }

    private fun onViewEventChange(viewData: NoteDetailViewEvent) {
        when (viewData) {
            NoteDetailViewEvent.ConfirmDelete,
            NoteDetailViewEvent.CopyNote ->
                findNavController().popBackStack()
            else -> { }
        }
    }
}