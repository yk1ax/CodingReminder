package com.yogig.android.codingcalendar.network

import com.google.gson.annotations.SerializedName

data class CodeforcesContestList(
    @SerializedName("result")
    val contestList: List<NetworkContest>


)

data class NetworkContest(
    val id: String,
    val name: String,
    val phase: String = "",
    val durationSeconds: Long,
    val startTimeSeconds: Long,
    var site: Int = 0,
    val websiteUrl: String = "")