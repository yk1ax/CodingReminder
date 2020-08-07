package com.yogig.android.codingReminder.database

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.yogig.android.codingReminder.contestListFragment.SITE_TYPE
import com.yogig.android.codingReminder.repository.Contest

@Entity(tableName = "contest_table")
data class DatabaseContest(
    @PrimaryKey
    val id: String,
    val name: String,
    @ColumnInfo(name = "start_time_milliseconds")
    val startTimeMilliseconds: Long,
    @ColumnInfo(name = "end_time_milliseconds")
    val endTimeSeconds: Long,
    var site: SITE_TYPE,
    @ColumnInfo(name = "website_url")
    val websiteUrl: String,
    @ColumnInfo(name = "is_notification_set")
    val isNotificationSet: Boolean
)

fun List<DatabaseContest>.asDomainModel(): List<Contest> {
    return map {
        Contest(
            it.id,
            it.name,
            it.startTimeMilliseconds,
            it.endTimeSeconds,
            it.site,
            it.websiteUrl,
            it.isNotificationSet
        )
    }
}
