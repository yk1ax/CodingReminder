package com.yogesh.android.codingReminder.network

import android.util.Log
import com.yogesh.android.codingReminder.viewModels.SiteType
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.withContext
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.*

private const val CLIST_USERNAME = "yk_ax"
private const val CLIST_API_KEY = "b1bebcecdcd976249c04f2e1659c40342efb2565"

object NetworkRequests {

    suspend fun contestsFromNetwork(): List<ClistContest> {
        val contests = mutableListOf<ClistContest>()

        var exceptions = 0
        val exceptionInfo = IOException()
        withContext(Dispatchers.IO) {
//            val list1 = async { fetchCFContests() }
//            val list2 = async { fetchCCContests() }
//            val list3 = async { fetchACContests() }
            val list4 = async { fetchClistContests() }


//            try {
//                contests.addAll(list1.await())
//            } catch (e: IOException) {
//                Log.i("NetworkRequests", "Failed $e")
//                exceptions++
//                exceptionInfo.addSuppressed(e)
//            }
//
//            try {
//                contests.addAll(list2.await())
//            } catch (e: IOException) {
//                Log.i("NetworkRequests", "Failed $e")
//                exceptions++
//                exceptionInfo.addSuppressed(e)
//            }
//
//            try {
//                contests.addAll(list3.await())
//            } catch (e: IOException) {
//                Log.i("NetworkRequests", "Failed $e")
//                exceptions++
//                exceptionInfo.addSuppressed(e)
//            }

            try {
                contests.addAll(list4.await())
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

    @Throws(IOException::class)
    private suspend fun fetchClistContests(): List<ClistContest> {

        val formatter = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.US)
        val curTime = formatter.format(Calendar.getInstance(Locale.ENGLISH).time)
        Log.i("NetworkRequests", curTime)
        val list: List<ClistContest> = ClistApi.retrofitService
                    .getContests(curTime, "start", CLIST_USERNAME, CLIST_API_KEY).contestList

        Log.i("NetworkRequests", "list.size = ${list.size}")

        for (contest in list) {
            contest.site = when(contest.resource.name) {
                "codeforces.com" -> SiteType.CODEFORCES_SITE
                "codechef.com" -> SiteType.CODECHEF_SITE
                "atcoder.com" -> SiteType.ATCODER_SITE
                else -> SiteType.UNKNOWN_SITE
            }
        }

        return list

    }
}