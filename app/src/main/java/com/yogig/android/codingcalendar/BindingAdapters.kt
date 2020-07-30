package com.yogig.android.codingcalendar

import android.util.Log
import android.widget.ImageView
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.core.content.ContextCompat
import androidx.databinding.BindingAdapter
import androidx.recyclerview.widget.RecyclerView
import com.yogig.android.codingcalendar.contestList.SITE_TYPE
import com.yogig.android.codingcalendar.network.NetworkContest
import com.yogig.android.codingcalendar.repository.Contest
import java.text.DateFormat
import java.text.SimpleDateFormat
import java.time.ZoneId
import java.util.*
import java.util.concurrent.TimeUnit

@BindingAdapter("contestList")
fun setContestList(view: RecyclerView, contestList: List<Contest>?) {
    val adapter = view.adapter as ContestListAdapter
    adapter.submitList(contestList)
    Log.i("BindingAdapters", "Here the list has ${contestList?.size?:-1} elements.")
}

@BindingAdapter("contestTime")
fun TextView.setContestTime(contest: Contest) {
    // converts text to the like of 20:05 - 22:05
    val builder = StringBuilder()
    val dateObj = Date(contest.startTimeMilliseconds)

    val dateFormatter = DateFormat.getDateTimeInstance(DateFormat.DEFAULT,DateFormat.SHORT,Locale.UK)
    builder.append(dateFormatter.format(dateObj))
    builder.append(" - ")
    dateObj.time += contest.durationMilliseconds

    builder.append(dateFormatter.format(dateObj))

    text = builder.toString()
}

@BindingAdapter("websiteImage")
fun ImageView.setWebsiteImage(type: SITE_TYPE) {
    setImageResource(when(type) {
        SITE_TYPE.CODEFORCES_SITE -> R.drawable.ic_codeforces_svg
        else -> R.drawable.ic_codechef_svg
    })
}

@BindingAdapter("bindBackground")
fun CardView.setColor(type: SITE_TYPE){
    setCardBackgroundColor(ContextCompat.getColor( context,
        when(type) {
            SITE_TYPE.CODECHEF_SITE -> R.color.codechefColor
            else -> R.color.codeforcesColor
        }
    ))
}

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