package com.dimi.advnotes.presentation

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentFactory
import com.dimi.advnotes.presentation.create.NoteDetailFragment
import com.dimi.advnotes.presentation.create.adapter.CheckItemListAdapter
import com.dimi.advnotes.presentation.list.NoteListFragment
import com.dimi.advnotes.presentation.list.adapter.NoteListAdapter
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview
import javax.inject.Inject

//@ExperimentalCoroutinesApi
//@FlowPreview
//class MainFragmentFactory @Inject constructor(
//   // private var adapter: NoteListAdapter
//) : FragmentFactory() {
//
//    override fun instantiate(classLoader: ClassLoader, className: String): Fragment {
//
//        return when (className) {
//            NoteListFragment::class.java.name -> {
//                NoteListFragment()
//            }
//            NoteDetailFragment::class.java.name -> {
//                NoteDetailFragment()
//            }
//            else -> super.instantiate(classLoader, className)
//        }
//    }
//}