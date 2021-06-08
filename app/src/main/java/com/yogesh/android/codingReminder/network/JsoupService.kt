package com.yogesh.android.codingReminder.network

import android.util.Log
import com.yogesh.android.codingReminder.viewModels.SiteType
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.jsoup.Jsoup
import org.jsoup.nodes.Document
import java.text.SimpleDateFormat
import java.util.*

private const val CODECHEF_URL = "https://www.codechef.com/contests"
private const val ATCODER_URL = "https://atcoder.jp/contests"

suspend fun fetchCCContests(): List<NetworkContest> {

    val list = mutableListOf<NetworkContest>()
    val document: Document =
        withContext(Dispatchers.IO) { Jsoup.connect(CODECHEF_URL).timeout(15000).get() }

    val tables = document.getElementsByClass("dataTable")
    for (i in 0..1) {
        val table = tables[i].getElementsByTag("tbody").first()
        val rows = table.getElementsByTag("tr")
        for (row in rows) {
            // fetch data for every individual contest
            val columns = row.getElementsByTag("td")

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


            val contest =
                NetworkContest(code, name, phase, (end - start), start, SiteType.CODECHEF_SITE)
            list.add(contest)
        }
    }
    return list
}

suspend fun fetchACContests(): List<NetworkContest> {

    // There is a problem, I can't fetch end time for the contests so I'll take the default of 100 mins
    val list = mutableListOf<NetworkContest>()
    val document: Document =
        withContext(Dispatchers.IO) { Jsoup.connect(ATCODER_URL).timeout(15000).get() }

    val table = document.getElementById("contest-table-upcoming").getElementsByTag("table").first()
    val rows = table.getElementsByTag("tbody").first()
        .getElementsByTag("tr")

    for(row in rows)
    {
        val columns = row.getElementsByTag("td")
        val timeText = columns[0].getElementsByTag("a").first().attr("href")
            .substringAfter("iso=").split('T')
        var startTime =
            SimpleDateFormat("yyyyMMddHHmm", Locale.US).parse(timeText[0]+timeText[1].substring(0,4))?.time ?: 0
        startTime -= 12600000 // ms equivalent to 3.5 hrs, i.e. standard time difference between Japan and India
        var durationText = columns[2].getElementsByClass("text-center").first().text()
        var duration = (durationText.substring(0,2).toLong().times(60) +
                durationText.substring(3,5).toLong()).times(60000)
        Log.i("AtcoderContest", "startTime = $startTime, duration = $duration")
        // taken the default duration to be 100 mins

        val nameTag = columns[1].getElementsByTag("a").first()
        val name = nameTag.text()
        val code = nameTag.attr("href").substringAfterLast('/')

        val contest = NetworkContest(code, name, "BEFORE", duration, startTime, SiteType.ATCODER_SITE)
        list.add(contest)
    }

    return list
}