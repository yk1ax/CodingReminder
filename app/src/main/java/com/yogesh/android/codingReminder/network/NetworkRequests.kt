package com.yogesh.android.codingReminder.network

import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import com.yogesh.android.codingReminder.viewModels.SiteType
import kotlinx.coroutines.Dispatchers
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
        val list = withContext(Dispatchers.IO) { fetchClistContests() }

        try {
            contests.addAll(list)
        } catch (e: IOException) {
            exceptions++
            exceptionInfo = e
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
        val contests: List<ClistContest> = ClistApi.retrofitService
                    .getContests(curTime, "start", CLIST_USERNAME, CLIST_API_KEY).contestList

        val list = mutableListOf<ClistContest>()

        for (contest in contests) {
            contest.site = when(contest.resource.name) {
                "codeforces.com" -> SiteType.CODEFORCES_SITE
                "codechef.com" -> SiteType.CODECHEF_SITE
                "atcoder.jp" -> SiteType.ATCODER_SITE
                else -> SiteType.UNKNOWN_SITE
            }
            if(contest.site != SiteType.UNKNOWN_SITE) list.add(contest)
        }

        return list

    }

    fun checkConnection(context: Context): Boolean {
        val cm = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager

        val activeNetwork = cm.activeNetwork
        val capabilities = cm.getNetworkCapabilities(activeNetwork)
        return when {
            capabilities == null -> false
            capabilities.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR)
                    or capabilities.hasTransport(NetworkCapabilities.TRANSPORT_WIFI)
                    or capabilities.hasTransport(NetworkCapabilities.TRANSPORT_ETHERNET)
            -> true
            else -> false
        }
    }
}