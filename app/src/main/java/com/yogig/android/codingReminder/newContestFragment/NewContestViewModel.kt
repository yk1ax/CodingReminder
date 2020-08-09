package com.yogig.android.codingReminder.newContestFragment

import android.app.Application
import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.webkit.URLUtil
import android.widget.TextView
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.yogig.android.codingReminder.R
import com.yogig.android.codingReminder.contestListFragment.SiteType
import com.yogig.android.codingReminder.database.ContestDatabase
import com.yogig.android.codingReminder.database.DatabaseContest
import kotlinx.coroutines.*
import java.text.DateFormat
import java.util.*

lateinit var startCalendar: Calendar
lateinit var endCalendar: Calendar

class NewContestViewModel(private val database: ContestDatabase, app: Application) : AndroidViewModel(app) {

    private val viewModelJob = Job()

    override fun onCleared() {
        viewModelJob.cancel()
        super.onCleared()
    }

    private val coroutineScope = CoroutineScope(Dispatchers.Main + viewModelJob)

    init {
        startCalendar = Calendar.getInstance()
        endCalendar = Calendar.getInstance()
        endCalendar.timeInMillis = startCalendar.timeInMillis
    }

    val START = 1
    val END = 2

    // id 1 -> START, 2 -> END
    private val _calendarSetEvent = MutableLiveData<Int>(-1)
    val calendarSetEvent: LiveData<Int>
        get() = _calendarSetEvent

    private val _timeSetEvent = MutableLiveData<Int>(-1)
    val timeSetEvent: LiveData<Int>
        get() = _timeSetEvent

    private val _submitEvent = MutableLiveData<Boolean>(false)
    val submitEvent: LiveData<Boolean>
        get() = _submitEvent

    private val _snackBarText = MutableLiveData<String?>()
    val snackBarText: LiveData<String?>
        get() = _snackBarText

    val contestName = MutableLiveData<String?>()

    val contestUrl = MutableLiveData<String?>()

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

    fun onSubmitEvent() {
        _submitEvent.value = true
    }

    fun onSubmitEventComplete() {
        _submitEvent.value = false
        _snackBarText.value = null
    }

    fun trySubmit() {
        val isValid = validateData()

        if(isValid) {
            submitContest()
            _snackBarText.value = getApplication<Application>().getString(R.string.contest_added)
        }
    }

    private fun validateData(): Boolean {
        if(contestName.value.isNullOrEmpty()) {
            _snackBarText.value = getApplication<Application>().getString(R.string.contest_name_invalid)
            return false
        }
        if(!URLUtil.isNetworkUrl("https://".plus(contestUrl.value)) && !contestUrl.value.isNullOrEmpty()) {
            _snackBarText.value = getApplication<Application>().getString(R.string.contest_link_invalid)
            return false
        }
        if(startCalendar.timeInMillis <= System.currentTimeMillis()) {
            _snackBarText.value = getApplication<Application>().getString(R.string.contest_alerady_started)
            return false
        }
        if(startCalendar.timeInMillis >= endCalendar.timeInMillis) {
            _snackBarText.value = getApplication<Application>().getString(R.string.invalid_time_range)
            return false
        }
        return true
    }

    private fun submitContest() {
        coroutineScope.launch {
            withContext(Dispatchers.IO) {
                loop@ for(i in 1..1000) {
                    if(database.contestDao.getContest(i.toString()) == null) {
                        val contest = DatabaseContest(
                            i.toString(),
                            contestName.value?:"",
                            startCalendar.timeInMillis,
                            endCalendar.timeInMillis,
                            SiteType.UNKNOWN_SITE,
                            if(contestUrl.value.isNullOrEmpty()) ""
                            else "https://".plus(contestUrl.value),
                            false
                        )

                        database.contestDao.insertContest(contest)
                        break@loop
                    }
                }
            }
        }
    }

}
