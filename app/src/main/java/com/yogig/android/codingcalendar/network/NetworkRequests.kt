package com.yogig.android.codingcalendar.network

import android.util.Log
import com.yogig.android.codingcalendar.contestList.SITE_TYPE
import java.io.IOException

object NetworkRequests {

    @Throws(IOException::class)
    suspend fun contestsFromNetwork(): MutableList<NetworkContest> {
        val contests = mutableListOf<NetworkContest>()

        var exceptions = 0
        val exceptionInfo = IOException()
        try {
            contests.addAll(fetchCFContests())
        } catch (e: IOException) {
            Log.i("NetworkRequests", "Failed $e")
            exceptions++
            exceptionInfo.addSuppressed(e)
        }

        try {
            contests.addAll(CodeforcesFetching.contestList)
        } catch (e: IOException) {
            Log.i("NetworkRequests", "Failed $e")
            exceptions++
            exceptionInfo.addSuppressed(e)
        }

        contests.sortBy { it.startTimeSeconds + it.durationSeconds }

        if(exceptions == 2) {
            throw exceptionInfo
        }
        return contests
    }

    @Throws(IOException::class)
    private suspend fun fetchCFContests(): List<NetworkContest> {

        @SuppressWarnings("BlockingMethod")
        val list: List<NetworkContest> = CodeforcesApi.retrofitService.getContests().contestList

        val contests = mutableListOf<NetworkContest>()
        if (list.isNotEmpty()) {
            loop@ for (contest in list) {
                contest.site = SITE_TYPE.CODEFORCES_SITE
                when (contest.phase) {
                    "BEFORE", "CODING" -> contests.add(contest)
                    else -> break@loop
                }
            }
        }
        return contests

    }
}