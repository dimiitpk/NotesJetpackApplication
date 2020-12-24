package com.dimi.advnotes.presentation.list

enum class ListType(val title: String) {
    NOTES("Notes"), ARCHIVE("Archive");

    fun isArchive() = this == ARCHIVE

    fun isNotes() = this == NOTES

    fun isNotNotes() = this != NOTES
}