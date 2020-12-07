package com.dimi.advnotes.data.interactors

data class NoteUseCase(
    val observeNotesUseCase: ObserveNotesUseCase,
    val insertOrUpdateNoteUseCase: InsertOrUpdateNoteUseCase,
    val deleteNotesUseCase: DeleteNotesUseCase,
    val deleteNoteUseCase: DeleteNoteUseCase,
    val updateNotesUseCase: UpdateNotesUseCase,
    val insertAll: InsertAll,
    val fetchSingleNote: FetchSingleNote,
    val clearReminderUseCase: ClearReminderUseCase,
    val observeRemindersUseCase: ObserveRemindersUseCase,
    val fetchAllNotesUseCase: FetchAllNotesUseCase
)