package com.yogig.android.codingcalendar.repository

import com.yogig.android.codingcalendar.contestList.SITE_TYPE

data class Contest(
    val id: String,
    val name: String,
    val phase: String,
    val startTimeMilliseconds: Long,
    val durationMilliseconds: Long,
    val endTimeSeconds: Long,
    var site: SITE_TYPE,
    val websiteUrl: String
)