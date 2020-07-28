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
import java.util.*

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