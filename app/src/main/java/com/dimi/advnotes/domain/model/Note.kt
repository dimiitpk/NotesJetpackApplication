package com.dimi.advnotes.domain.model

import android.os.Parcelable
import androidx.lifecycle.MutableLiveData
import com.dimi.advnotes.presentation.common.extensions.generateUniqueId
import com.dimi.advnotes.util.Constants
import com.dimi.advnotes.util.Constants.DEFAULT_NOTE_COLOR
import com.dimi.advnotes.util.Constants.DEFAULT_PRIMARY_KEY
import kotlinx.parcelize.IgnoredOnParcel
import kotlinx.parcelize.Parcelize
import java.util.*

@Parcelize
data class Note(
    var id: Long = DEFAULT_PRIMARY_KEY,
    var title: String = "",
    var body: String = "",
    var color: Int = DEFAULT_NOTE_COLOR,
    var pinned: Boolean = false,
    var archived: Boolean = false,
    var createdAt: Date = Date(),
    var updatedAt: Date = Date(),
    var checkItems: List<CheckItem> = emptyList(),
    var reminder: Reminder = Reminder()
) : Parcelable {

    @IgnoredOnParcel
    val selected: MutableLiveData<Boolean> = MutableLiveData(false)

    fun unSelect() {
        selected.postValue(false)
    }

    fun toggleSelection() {
        selected.postValue(!selected.value!!)
    }

    fun isSelected() = selected.value!!

    fun isNoteValid(): Boolean = body.isNotBlank() || checkItems.isNotEmpty()

    /**
     * Resetting id to 0, automatically create new note
     * Resetting check items id to 0, automatically create ones that are binned to this note
     * Others resetting fields are not necessary
     */
    fun copyNote(): Boolean {
        if (!isNoteValid()) return false
        id = DEFAULT_PRIMARY_KEY
        createdAt = Date()
        updatedAt = Date()
        pinned = Constants.DEFAULT_PINNED
        return true
    }

    fun togglePinned() {
        pinned = !pinned
    }

    fun toggleArchive() =
        if (archived)
            copy(archived = false)
        else
            copy(archived = true, pinned = false)

//    fun createReminder(timeInMillis: Long?, repeating: Long?) {
//        val timeMillis = timeInMillis?.let {
//            if (timeInMillis == 0L) return@let null
//            val savedCalendar = Calendar.getInstance().apply {
//                this.timeInMillis = it
//            }
//            val currentCalendar = Calendar.getInstance()
//            if (savedCalendar.before(currentCalendar))
//                null
//            else it
//        }
//        reminder = Reminder(
//            noteId = id,
//            requestCode = createdAt.generateUniqueId(),
//            timeInMillis = timeMillis,
//            repeating = null // OVO JE BILO !!! if (timeMillis == null || repeating == 0L) null else repeating
//        )
//    }
}