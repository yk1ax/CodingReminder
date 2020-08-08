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
) {
    override fun equals(other: Any?): Boolean {
        val newItem = other as DatabaseContest
        return when {
            this.id != newItem.id || this.name != newItem.name ||
                    this.startTimeMilliseconds != newItem.startTimeMilliseconds ||
                    this.endTimeSeconds != newItem.endTimeSeconds || this.site != newItem.site ||
                    this.websiteUrl != newItem.websiteUrl -> false
            else -> true
        }
    }

    fun asDomainModel() = Contest(
            id,
            name,
            startTimeMilliseconds,
            endTimeSeconds,
            site,
            websiteUrl,
            isNotificationSet
    )
}

fun List<DatabaseContest>.asDomainModel(): List<Contest> {
    return map {
        it.asDomainModel()
    }
}
