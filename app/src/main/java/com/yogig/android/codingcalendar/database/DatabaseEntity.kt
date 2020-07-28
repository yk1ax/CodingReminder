package com.yogig.android.codingcalendar.database

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.yogig.android.codingcalendar.contestList.SITE_TYPE
import com.yogig.android.codingcalendar.repository.Contest

@Entity(tableName = "contest_table")
data class DatabaseContest(
    @PrimaryKey
    val id: String,
    val name: String,
    val phase: String,
    @ColumnInfo(name = "start_time_milliseconds")
    val startTimeMilliseconds: Long,
    @ColumnInfo(name = "duration_milliseconds")
    val durationMilliseconds: Long,
    @ColumnInfo(name = "end_time_milliseconds")
    val endTimeSeconds: Long,
    var site: SITE_TYPE,
    @ColumnInfo(name = "website_url")
    val websiteUrl: String
)

fun List<DatabaseContest>.asDomainModel(): List<Contest> {
    return map {
        Contest(
            it.id,
            it.name,
            it.phase,
            it.startTimeMilliseconds,
            it.durationMilliseconds,
            it.endTimeSeconds,
            it.site,
            it.websiteUrl
        )
    }
}
