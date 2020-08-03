package com.yogig.android.codingcalendar.contestFragment

import android.content.Intent
import android.provider.CalendarContract
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel


class ContestViewModel() : ViewModel() {


    // Encapsulated calendarEvent boolean variable
    private val _calendarEvent = MutableLiveData<Boolean>(false)
    val calendarEvent: LiveData<Boolean>
        get() = _calendarEvent

    // Encapsulated websiteEvent boolean variable
    private val _websiteEvent = MutableLiveData<Boolean>(false)
    val websiteEvent: LiveData<Boolean>
        get() = _websiteEvent

    // To be called when the Calendar Event has been completed to update the LiveData
    fun onCalendarEventComplete() {
        Log.i("ContestViewModel", "Came back from Calendar")
        _calendarEvent.value = false
    }

    // To be called to initiate the Calendar Event by updating the LiveData
    fun onClickCalendarEvent() {
        _calendarEvent.value = true
    }

    // To be called when the Website Event has been completed to update the LiveData
    fun onWebsiteEventComplete() {
        Log.i("ContestViewModel", "Came back from browser")
        _websiteEvent.value = false
    }

    // To be called to initiate the Website Event by updating the LiveData
    fun onclickWebsiteEvent() {
        _websiteEvent.value = true
    }
}