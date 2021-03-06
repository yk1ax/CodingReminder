package com.yogesh.android.codingReminder.network

import android.util.Log
import com.yogesh.android.codingReminder.viewModels.SiteType
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.withContext
import java.io.IOException

object NetworkRequests {

    suspend fun contestsFromNetwork(): List<NetworkContest> {
        val contests = mutableListOf<NetworkContest>()

        var exceptions = 0
        val exceptionInfo = IOException()
        withContext(Dispatchers.IO) {
            val list1 = async { fetchCFContests() }
            val list2 = async { fetchCCContests() }
            val list3 = async { fetchACContests() }

            try {
                contests.addAll(list1.await())
            } catch (e: IOException) {
                Log.i("NetworkRequests", "Failed $e")
                exceptions++
                exceptionInfo.addSuppressed(e)
            }

            try {
                contests.addAll(list2.await())
            } catch (e: IOException) {
                Log.i("NetworkRequests", "Failed $e")
                exceptions++
                exceptionInfo.addSuppressed(e)
            }

            try {
                contests.addAll(list3.await())
            } catch (e: IOException) {
                Log.i("NetworkRequests", "Failed $e")
                exceptions++
                exceptionInfo.addSuppressed(e)
            }
        }

        if(exceptions == 3) {
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
                contest.site = SiteType.CODEFORCES_SITE
                contest.startTimeSeconds *= 1000
                contest.durationSeconds *= 1000
                when (contest.phase) {
                    "BEFORE", "CODING" -> contests.add(contest)
                    else -> break@loop
                }
            }
        }
        return contests

    }
}