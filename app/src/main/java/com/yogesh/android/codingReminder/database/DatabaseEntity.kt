package com.yogesh.android.codingReminder.database

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.yogesh.android.codingReminder.viewModels.SiteType
import com.yogesh.android.codingReminder.repository.Contest

@Entity(tableName = "contest_table")
data class DatabaseContest(
    @PrimaryKey
    val id: Long,
    val name: String,
    @ColumnInfo(name = "start_time_milliseconds")
    val startTimeMilliseconds: Long,
    @ColumnInfo(name = "end_time_milliseconds")
    val endTimeSeconds: Long,
    var site: SiteType,
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

    override fun hashCode(): Int {
        var result = id.hashCode()
        result = 31 * result + name.hashCode()
        result = 31 * result + startTimeMilliseconds.hashCode()
        result = 31 * result + endTimeSeconds.hashCode()
        result = 31 * result + site.hashCode()
        result = 31 * result + websiteUrl.hashCode()
        result = 31 * result + isNotificationSet.hashCode()
        return result
    }
}

fun List<DatabaseContest>.asDomainModel(): List<Contest> {
    return map {
        it.asDomainModel()
    }
}
