package com.yogig.android.codingReminder.network

import android.util.Log
import com.yogig.android.codingReminder.contestList.SITE_TYPE
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.IOException

object NetworkRequests {

    @Throws(IOException::class)
    suspend fun contestsFromNetwork(): List<NetworkContest> {
        val contests = mutableListOf<NetworkContest>()

        var exceptions = 0
        val exceptionInfo = IOException()

        withContext(Dispatchers.IO) {
            try {
                contests.addAll(fetchCFContests())
            } catch (e: IOException) {
                Log.i("NetworkRequests", "Failed $e")
                exceptions++
                exceptionInfo.addSuppressed(e)
            }

            try {
                contests.addAll(CodechefFetching.contestList)
            } catch (e: IOException) {
                Log.i("NetworkRequests", "Failed $e")
                exceptions++
                exceptionInfo.addSuppressed(e)
            }
        }

        if(exceptions == 2) {
            throw exceptionInfo
        }
        return contests
    }

    @Throws(IOException::class)
    private suspend fun fetchCFContests(): List<NetworkContest> {

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