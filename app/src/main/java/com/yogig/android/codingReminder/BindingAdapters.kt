package com.yogig.android.codingReminder

import android.util.Log
import android.widget.ImageView
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.core.content.ContextCompat
import androidx.databinding.BindingAdapter
import androidx.recyclerview.widget.RecyclerView
import com.yogig.android.codingReminder.contestListFragment.SITE_TYPE
import com.yogig.android.codingReminder.repository.Contest
import java.text.DateFormat
import java.util.*

/**
 * Using data binding for submitting list for the recycler view adapter
 */
@BindingAdapter("contestList")
fun setContestList(view: RecyclerView, contestList: List<Contest>?) {
    val adapter = view.adapter as ContestListAdapter
    adapter.submitList(contestList)
    Log.i("BindingAdapters", "Here the list has ${contestList?.size?:-1} elements.")
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
fun ImageView.setWebsiteImage(type: SITE_TYPE) {
    setImageResource(when(type) {
        SITE_TYPE.CODEFORCES_SITE -> R.drawable.ic_codeforces_svg
        SITE_TYPE.CODECHEF_SITE -> R.drawable.ic_codechef_svg
        else -> R.drawable.ic_code
    })
}

/**
 * Sets the background color for the card view depending upon the contest site
 */
@BindingAdapter("bindBackground")
fun CardView.setColor(type: SITE_TYPE){
    setCardBackgroundColor(ContextCompat.getColor( context,
        when(type) {
            SITE_TYPE.CODECHEF_SITE -> R.color.codechefColor
            else -> R.color.codeforcesColor
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