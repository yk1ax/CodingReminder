package com.yogig.android.codingcalendar

import android.util.Log
import android.widget.TextView
import androidx.databinding.BindingAdapter
import androidx.recyclerview.widget.RecyclerView
import com.yogig.android.codingcalendar.contestList.CODEFORCES_SITE
import com.yogig.android.codingcalendar.network.NetworkContest
import java.text.DateFormat
import java.util.*

@BindingAdapter("contestList")
fun setContestList(view: RecyclerView, contestList: List<NetworkContest>?) {
    val adapter = view.adapter as ContestListAdapter
    adapter.submitList(contestList)
    Log.i("BindingAdapters", "Here the list has ${contestList?.size?:-1} elements.")
}

@BindingAdapter("contestTime")
fun TextView.setContestTime(contest: NetworkContest) {
    // converts text to the like of 20:05 - 22:05
    val builder = StringBuilder()
    val dateObj = Date(contest.startTimeSeconds
        .times( if(contest.site == CODEFORCES_SITE) 1000 else 1 )
    )

    val dateFormatter = DateFormat.getDateTimeInstance(DateFormat.DEFAULT,DateFormat.SHORT,Locale.UK)
    builder.append(dateFormatter.format(dateObj))
    builder.append(" - ")
    dateObj.time += contest.durationSeconds.times( if(contest.site == CODEFORCES_SITE) 1000 else 1 )
    builder.append(dateFormatter.format(dateObj))

    text = builder.toString()
}