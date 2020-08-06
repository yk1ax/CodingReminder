package com.yogig.android.codingReminder.network

import com.google.gson.annotations.SerializedName
import com.yogig.android.codingReminder.contestListFragment.SITE_TYPE
import com.yogig.android.codingReminder.database.DatabaseContest

const val CODEFORCES_BASE = "https://codeforces.com/contest/"
const val CODECHEF_BASE = "https://www.codechef.com/"

data class CodeforcesContestList(
    @SerializedName("result")
    val contestList: List<NetworkContest>
)

data class NetworkContest(
    val id: String,
    val name: String,
    val phase: String = "",
    val durationSeconds: Long,      // codeforces in seconds, codechef in milliseconds
    val startTimeSeconds: Long,
    var site: SITE_TYPE,
    val websiteUrl: String = "")

fun List<NetworkContest>.asDatabaseModel(): List<DatabaseContest> {
    return map {
        DatabaseContest(
            it.id,
            it.name,
            it.startTimeSeconds
                .times(if(it.site == SITE_TYPE.CODEFORCES_SITE) 1000 else 1),
            (it.startTimeSeconds + it.durationSeconds)
                .times(if(it.site == SITE_TYPE.CODEFORCES_SITE) 1000 else 1),
            it.site,
            when(it.site) {
                SITE_TYPE.CODEFORCES_SITE -> CODEFORCES_BASE
                else -> CODECHEF_BASE
            }.plus(it.id)
        )
    }
}
