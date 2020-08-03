package com.yogig.android.codingcalendar

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import java.util.*

class NewContestViewModel : ViewModel() {

    // id 1 -> START, 2 -> END
    private val _calendarSetEvent = MutableLiveData<Int>(-1)
    val calendarSetEvent: LiveData<Int>
        get() = _calendarSetEvent

    val START = 1
    val END = 2

    private val _timeSetEvent = MutableLiveData<Int>(-1)
    val timeSetEvent: LiveData<Int>
        get() = _timeSetEvent

    fun onCalendarSetEvent(id: Int) {
        _calendarSetEvent.value = id
    }

    fun onCalendarSetEventComplete() {
        _calendarSetEvent.value = -1
    }

    fun onTimeSetEvent(id: Int) {
        _timeSetEvent.value = id
    }

    fun onTimeSetEventComplete() {
        _timeSetEvent.value = -1
    }
}