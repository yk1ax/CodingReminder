package com.yogig.android.codingcalendar.network

import com.google.gson.annotations.SerializedName
import com.yogig.android.codingcalendar.contestList.SITE_TYPE

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