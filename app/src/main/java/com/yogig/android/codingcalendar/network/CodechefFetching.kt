package com.yogig.android.codingcalendar.network

import android.util.Log
import org.jsoup.Jsoup
import java.text.SimpleDateFormat
import java.util.*

private const val CODECHEF_URL = "https://www.codechef.com/contests"

private fun jsoupFetch(): List<NetworkContest> {

    val list = mutableListOf<NetworkContest>()
    val document = Jsoup.connect(CODECHEF_URL).timeout(10000).get()
    val tables = document.getElementsByClass("dataTable")
    for (i in 0..1) {
        val table = tables[i].getElementsByTag("tbody").first()
        val rows = table.getElementsByTag("tr")
        for (row in rows) {
            // fetch data for every individual contest
            val columns = row.getElementsByTag("td")
            Log.v("CodechefFetching", "Size of columns list is ${columns.size}")

            val code = columns[0].text()
            val name = columns[1].text()
            val startRaw = columns[2].text()
            val endRaw = columns[3].text()

            val start =
                SimpleDateFormat("dd MMM yyyy HH:mm:ss", Locale.US).parse(startRaw)?.time ?: 0
            val end =
                SimpleDateFormat("dd MMM yyyy HH:mm:ss", Locale.US).parse(endRaw)?.time ?: 0

            val curTime = Date().time
            val phase = when {
                curTime < start -> "BEFORE"
                curTime in start..end -> "CODING"
                else -> "FINISHED"
            }


            val contest = NetworkContest(code, name, phase, (end - start), start)
            list.add(contest)
        }
    }
    return list
}

object CodeforcesFetching {
    val contestList: List<NetworkContest> by lazy { jsoupFetch() }
}