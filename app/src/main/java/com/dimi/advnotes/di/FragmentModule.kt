package com.dimi.advnotes.di

import com.dimi.advnotes.presentation.create.adapter.CheckItemListAdapter
import com.dimi.advnotes.presentation.list.adapter.NoteListAdapter
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.FragmentComponent
import dagger.hilt.android.scopes.FragmentScoped
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview

@FlowPreview
@ExperimentalCoroutinesApi
@Module
@InstallIn(FragmentComponent::class)
object FragmentModule {

//    @FragmentScoped
//    @Provides
//    fun provideNoteListAdapter() = NoteListAdapter()

//    @FragmentScoped
//    @Provides
//    fun provideCheckItemListAdapter() = CheckItemListAdapter()
}