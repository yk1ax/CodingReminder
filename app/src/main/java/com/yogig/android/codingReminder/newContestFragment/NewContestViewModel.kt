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
import com.yogig.android.codingReminder.contestListFragment.SITE_TYPE
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

    val coroutineScope = CoroutineScope(Dispatchers.Main + viewModelJob)

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

    fun submitContest() {
        coroutineScope.launch {
            withContext(Dispatchers.IO) {
                loop@ for(i in 1..1000) {
                    if(database.contestDao.getContest(i.toString()) == null) {
                        val contest = DatabaseContest(
                            i.toString(),
                            contestName.value?:"",
                            startCalendar.timeInMillis,
                            endCalendar.timeInMillis,
                            SITE_TYPE.UNKNOWN_SITE,
                            if(contestUrl.value.isNullOrEmpty()) ""
                            else "https://".plus(contestUrl.value)
                        )

                        database.contestDao.insertContest(contest)
                        break@loop
                    }
                }
            }
        }
    }

}

fun TextView.setDate(id: Int) {
    val calendar = if(id == 1) startCalendar else endCalendar
    val mYear = calendar.get(Calendar.YEAR)
    val mMonth = calendar.get(Calendar.MONTH)
    val mDay = calendar.get(Calendar.DAY_OF_MONTH)

    val formatter = DateFormat.getDateInstance(DateFormat.DEFAULT, Locale.UK)

    val dialog = DatePickerDialog(
        this.context,
        DatePickerDialog.OnDateSetListener { _, year, month, dayOfMonth ->
            calendar.set(Calendar.YEAR, year)
            calendar.set(Calendar.MONTH, month)
            calendar.set(Calendar.DAY_OF_MONTH, dayOfMonth)

            text = formatter.format(calendar.timeInMillis)
        },
        mYear,
        mMonth,
        mDay
    )
    dialog.show()
}

fun TextView.setTime(id: Int) {
    val calendar = if(id == 1) startCalendar else endCalendar
    val mHour = calendar.get(Calendar.HOUR_OF_DAY)
    val mMinute = calendar.get(Calendar.MINUTE)

    val formatter = DateFormat.getTimeInstance(DateFormat.SHORT, Locale.UK)

    val dialog = TimePickerDialog(
        this.context,
        TimePickerDialog.OnTimeSetListener { _, hourOfDay, minute ->
            calendar.set(Calendar.HOUR_OF_DAY, hourOfDay)
            calendar.set(Calendar.MINUTE, minute)
            text = formatter.format(calendar.timeInMillis)
        },
        mHour,
        mMinute,
        true
    )
    dialog.show()
}
