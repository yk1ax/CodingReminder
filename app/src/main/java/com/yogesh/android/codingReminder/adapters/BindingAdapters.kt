package com.yogesh.android.codingReminder.adapters

import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.util.Log
import android.widget.ImageView
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.core.content.ContextCompat
import androidx.databinding.BindingAdapter
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.button.MaterialButton
import com.yogesh.android.codingReminder.R
import com.yogesh.android.codingReminder.viewModels.SiteType
import com.yogesh.android.codingReminder.viewModels.endCalendar
import com.yogesh.android.codingReminder.viewModels.startCalendar
import com.yogesh.android.codingReminder.repository.Contest
import java.text.DateFormat
import java.util.*
import java.util.concurrent.TimeUnit

/**
 * Using data binding for submitting list for the recycler view adapter
 */
@BindingAdapter("contestList")
fun setContestList(view: RecyclerView, contestList: List<Contest>?) {
    val adapter = view.adapter as ContestListAdapter
    adapter.submitList(contestList)
}

/**
 * Passing @param[contest] to return the period of contest as string in the form
 * DD MMM YYYY HH:mm - DD MMM YYYY HH:mm
 */
@BindingAdapter("contestTime")
fun TextView.setContestTime(contest: Contest) {
    // converts text to the like of 20:05 - 22:05
    val builder = StringBuilder()
    val dateObj = Date(contest.startTimeMilliseconds)

    val dateFormatter = DateFormat.getDateTimeInstance(DateFormat.DEFAULT,DateFormat.SHORT,Locale.UK)
    builder.append(dateFormatter.format(dateObj))
    builder.append(" - ")
    dateObj.time += contest.endTimeSeconds - contest.startTimeMilliseconds

    builder.append(dateFormatter.format(dateObj))

    text = builder.toString()
}

/**
 * Sets vector drawable of the logo of the Website depending upon the contest site
 */
@BindingAdapter("websiteImage")
fun ImageView.setWebsiteImage(type: SiteType) {
    Log.i("BindingAdapters","Type is $type")
    setImageResource(when(type) {
        SiteType.CODEFORCES_SITE -> R.drawable.ic_codeforces_svg
        SiteType.CODECHEF_SITE -> R.drawable.ic_codechef_svg
        SiteType.ATCODER_SITE -> R.drawable.ic_atcoder
        else -> R.drawable.ic_code
    })
}

/**
 * Sets the background color for the card view depending upon the contest site
 */
@BindingAdapter("bindBackground")
fun CardView.setColor(type: SiteType){
    setCardBackgroundColor(ContextCompat.getColor( context,
        when(type) {
            SiteType.CODEFORCES_SITE -> R.color.codeforcesColor
            SiteType.CODECHEF_SITE -> R.color.codechefColor
            SiteType.ATCODER_SITE -> R.color.atcoderColor
            else -> R.color.unknownColor
        }
    ))
}

/**
 * Sets the formatted text for a contest showing time left before start / end
 * "Starts in @return[timeLeftFormatted]" or "Ends in @return[timeLeftFormatted]"
 */
@BindingAdapter("timeLeftFormatted")
fun TextView.setTimeFormatted(contest: Contest) {
    val curTime = System.currentTimeMillis()
    text = when {
        contest.startTimeMilliseconds > curTime -> "Starts in "
            .plus(timeLeftFormatted(contest.startTimeMilliseconds - curTime))
        contest.endTimeSeconds > curTime -> "Ends in "
            .plus(timeLeftFormatted(contest.endTimeSeconds - curTime))
        else -> ""
    }
}

/**
 * Sets the Notification button depending upon whether the notification has been set or not
 */
@BindingAdapter("isSet", "time")
fun MaterialButton.setButton(isNotificationSet: Boolean, time: Long) {
    val curTime = System.currentTimeMillis() + TimeUnit.MINUTES.toMillis(15)
    if(isNotificationSet && curTime < time) {
        text = context.getString(R.string.remove_reminder)
        icon = context.getDrawable(R.drawable.ic_remove_notification)
    } else {
        text = context.getString(R.string.set_reminder)
        icon = context.getDrawable(R.drawable.ic_add_notification)
    }

    if(curTime >= time) {
        isEnabled = false
    }
}

/**
 * @
 * Formats the text for showing time left
 * @param[time] ->   time in milliseconds which has to be converted in units
 *                  of year(s) / month(s) / day(s) / hour(s) / minute(s)
 * @return -> A string of the format "__ month(s)"
 * It also rounds off depending upon the proximity to the lower limit and to the upper limit
 */
private fun timeLeftFormatted(time: Long): String {
    val calendar = Calendar.getInstance()
    calendar.timeInMillis = time - TimeZone.getDefault().rawOffset

    var period = calendar.get(Calendar.YEAR)-1970
    if(period !=0) {
        if(calendar.get(Calendar.MONTH) >= 6) period++
        return "$period year".plus(if(period>1) "s" else "")
    }

    period = calendar.get(Calendar.MONTH)
    if(period != 0) {
        if(calendar.get(Calendar.DAY_OF_MONTH)-1 > 15) period++
        return "$period month".plus(if(period>1) "s" else "")
    }

    period = calendar.get(Calendar.DAY_OF_MONTH)-1
    if(period != 0) {
        if(calendar.get(Calendar.HOUR_OF_DAY) > 12) period++
        return "$period day".plus(if(period>1) "s" else "")
    }
    period = calendar.get(Calendar.HOUR_OF_DAY)
    if(period != 0) {
        if(calendar.get(Calendar.MINUTE) > 30) period++
        return "$period hour".plus(if(period>1) "s" else "")
    }
    period = calendar.get(Calendar.MINUTE)
    return "$period minute".plus(if(period>1) "s" else "")
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