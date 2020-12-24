package com.dimi.advnotes.presentation.search

import androidx.navigation.fragment.findNavController
import com.dimi.advnotes.R
import com.dimi.advnotes.databinding.FragmentSearchBinding
import com.dimi.advnotes.presentation.common.base.BaseFragment
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class SearchFragment : BaseFragment<FragmentSearchBinding>(R.layout.fragment_search) {

    override fun onInitDataBinding() {
        viewBinding.apply {
            toolbar.setNavigationOnClickListener {
                findNavController().popBackStack()
            }
        }
    }
}