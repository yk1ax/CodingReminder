package com.yogig.android.codingcalendar.repository

import android.os.Parcelable
import com.yogig.android.codingcalendar.contestList.SITE_TYPE
import kotlinx.android.parcel.Parcelize

@Parcelize
data class Contest(
    val id: String,
    val name: String,
    val phase: String,
    val startTimeMilliseconds: Long,
    val durationMilliseconds: Long,
    val endTimeSeconds: Long,
    var site: SITE_TYPE,
    val websiteUrl: String
) : Parcelable