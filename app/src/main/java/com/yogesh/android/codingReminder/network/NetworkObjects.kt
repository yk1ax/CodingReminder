package com.yogesh.android.codingReminder.network

import com.google.gson.annotations.SerializedName
import com.yogesh.android.codingReminder.viewModels.SiteType
import com.yogesh.android.codingReminder.database.DatabaseContest

const val CODEFORCES_BASE = "https://codeforces.com/contest/"
const val CODECHEF_BASE = "https://www.codechef.com/contest/"
const val ATCODER_BASE = "https://atcoder.jp/contests"

data class CodeforcesContestList(
    @SerializedName("result")
    val contestList: List<NetworkContest>
)

data class NetworkContest(
    val id: String,
    val name: String,
    val phase: String = "",
    var durationSeconds: Long,      // codeforces in seconds, codechef in milliseconds
    var startTimeSeconds: Long,
    var site: SiteType,
    val websiteUrl: String = "")

fun List<NetworkContest>.asDatabaseModel(): List<DatabaseContest> {
    return map {
        DatabaseContest(
            it.id,
            it.name,
            it.startTimeSeconds,
            (it.startTimeSeconds + it.durationSeconds),
            it.site,
            when(it.site) {
                SiteType.CODEFORCES_SITE -> CODEFORCES_BASE
                SiteType.CODECHEF_SITE -> CODECHEF_BASE
                SiteType.ATCODER_SITE -> ATCODER_BASE
                else -> CODECHEF_BASE
            }.plus(it.id),
            false
        )
    }
}
