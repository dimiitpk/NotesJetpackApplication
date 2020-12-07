package com.dimi.advnotes.presentation.create.model

import android.app.AlarmManager
import androidx.databinding.BaseObservable
import androidx.databinding.Bindable
import com.dimi.advnotes.BR
import com.dimi.advnotes.presentation.common.extensions.toBiggerMonthAndDay
import com.dimi.advnotes.presentation.common.extensions.toHoursAndMinutes
import java.util.*

// 0L
// AlarmManager.INTERVAL_DAY
// AlarmManager.INTERVAL_HOUR

//enum class RepeatValue(value: Long, printName: String) {
//    NONE(0L, "Without Repeating"),
//    BY_DAY(AlarmManager.INTERVAL_DAY, "Every Day"),
//    BY_HOUR(AlarmManager.INTERVAL_HOUR, "Every Hour")
//}

data class ObservableReminder(
    var calendar: Calendar = Calendar.getInstance(),
    var repeating: Long = 0L
) {

    var isInitialCalendarNull: Boolean = false

    var completed: Boolean = false

    fun timeString() = calendar.time.toHoursAndMinutes()

    fun repeatingString() = when (repeating) {
        AlarmManager.INTERVAL_DAY -> "Every Day"
        AlarmManager.INTERVAL_HOUR -> "Every Hour"
        else -> "Without Repeating"
    }

    fun getDateString() = calendar.time.toBiggerMonthAndDay()

    fun setTime(calendar: Calendar) {
        this.calendar.apply {
            set(Calendar.MINUTE, calendar.get(Calendar.MINUTE))
            set(Calendar.HOUR_OF_DAY, calendar.get(Calendar.HOUR_OF_DAY))
            set(Calendar.SECOND, 0)
        }
    }

    fun setDate(calendar: Calendar) {
        this.calendar.apply {
            set(Calendar.DAY_OF_MONTH, calendar.get(Calendar.DAY_OF_MONTH))
            set(Calendar.YEAR, calendar.get(Calendar.YEAR))
            set(Calendar.MONTH, calendar.get(Calendar.MONTH))
        }
    }

    fun reset() {
        calendar = Calendar.getInstance()
        repeating = 0
        completed = false
    }
}

//data class ObservableReminder(
//    var calendar: Calendar = Calendar.getInstance(),
//    var repeating: Long = 0L
//) : BaseObservable() {
//
//    var isInitialCalendarNull: Boolean = false
//
//    var completed: Boolean = false
//        @Bindable get
//        set(value) {
//            field = value
//            notifyPropertyChanged(BR.completed)
//        }
//
//    var timeString: String = ""
//        @Bindable get() = calendar.time.toHoursAndMinutes()
//        set(value) {
//            field = value
//            notifyPropertyChanged(BR.timeString)
//        }
//
//    var repeatingString: String = ""
//        @Bindable get() {
//            return when (repeating) {
//                AlarmManager.INTERVAL_DAY -> "Every Day"
//                AlarmManager.INTERVAL_HOUR -> "Every Hour"
//                else -> "Without Repeating"
//            }
//        }
//        set(value) {
//            field = value
//            notifyPropertyChanged(BR.repeatingString)
//        }
//
//    var dateString: String = ""
//        @Bindable get() = calendar.time.toBiggerMonthAndDay()
//        set(value) {
//            field = value
//            notifyPropertyChanged(BR.dateString)
//        }
//
//    fun setRepeating2(long: Long) {
//        repeating = long
//        val string = when (repeating) {
//            AlarmManager.INTERVAL_DAY -> "Every Day"
//            AlarmManager.INTERVAL_HOUR -> "Every Hour"
//            else -> "Without Repeating"
//        }
//        repeatingString = string
//    }
//
//    fun setTime(calendar: Calendar) {
//        this.calendar.apply {
//            set(Calendar.MINUTE, calendar.get(Calendar.MINUTE))
//            set(Calendar.HOUR_OF_DAY, calendar.get(Calendar.HOUR_OF_DAY))
//            set(Calendar.SECOND, 0)
//        }
//        timeString = calendar.time.toHoursAndMinutes()
//    }
//
//    fun setDate(calendar: Calendar) {
//        this.calendar.apply {
//            set(Calendar.DAY_OF_MONTH, calendar.get(Calendar.DAY_OF_MONTH))
//            set(Calendar.YEAR, calendar.get(Calendar.YEAR))
//            set(Calendar.MONTH, calendar.get(Calendar.MONTH))
//        }
//        dateString = calendar.time.toBiggerMonthAndDay()
//    }
//
//    fun reset() {
//        calendar = Calendar.getInstance()
//        setRepeating2(0)
//        completed = false
//        dateString = calendar.time.toBiggerMonthAndDay()
//        timeString = calendar.time.toHoursAndMinutes()
//    }
//}