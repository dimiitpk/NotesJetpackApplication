package com.dimi.advnotes.data.interactors

data class NoteUseCase(
    val observeNotes: ObserveNotesUseCase,
    val insertOrUpdateNote: InsertOrUpdateNoteUseCase,
    val deleteNotes: DeleteNotesUseCase,
    val deleteNote: DeleteNoteUseCase,
    val updateNotes: UpdateNotesUseCase,
    val insertAllNotes: InsertAllNotesUseCase,
    val fetchSingleNote: FetchSingleNote,
    val clearReminder: ClearReminderUseCase,
    val observeReminders: ObserveRemindersUseCase,
    val fetchAllNotes: FetchAllNotesUseCase
)