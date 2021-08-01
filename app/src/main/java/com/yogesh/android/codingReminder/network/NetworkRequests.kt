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
        var exceptionInfo = IOException()
        withContext(Dispatchers.IO) {
            val list = async { fetchClistContests() }

            try {
                contests.addAll(list.await())
            } catch (e: IOException) {
                exceptions++
                exceptionInfo = e
            }
        }

        if(exceptions==1) {
            throw exceptionInfo
        }
        return contests
    }

    @Throws(IOException::class)
    private suspend fun fetchClistContests(): List<ClistContest> {

        val formatter = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.US)
        val curTime = formatter.format(Calendar.getInstance(Locale.ENGLISH).time)
        val list: List<ClistContest> = ClistApi.retrofitService
                    .getContests(curTime, "start", CLIST_USERNAME, CLIST_API_KEY).contestList

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