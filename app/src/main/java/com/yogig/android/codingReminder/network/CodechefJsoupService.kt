package com.yogig.android.codingReminder.network

import android.util.Log
import com.yogig.android.codingReminder.contestListFragment.SiteType
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.jsoup.Jsoup
import java.text.SimpleDateFormat
import java.util.*

private const val CODECHEF_URL = "https://www.codechef.com/contests"

suspend fun fetchCCContests(): List<NetworkContest> {

    Log.i("NetworkRequests", "fetchCCContests has been called")
    val list = mutableListOf<NetworkContest>()
    val document = Jsoup.connect(CODECHEF_URL).timeout(15000).get()
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
    Log.i("NetworkRequests", "fetchCCContests is returning")
    return list


}