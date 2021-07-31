package com.yogesh.android.codingReminder.repository

import android.os.Parcelable
import com.yogesh.android.codingReminder.viewModels.SiteType
import com.yogesh.android.codingReminder.database.DatabaseContest
import kotlinx.parcelize.IgnoredOnParcel
import kotlinx.parcelize.Parcelize

/**
 * Domain Model of the Contest,
 * i.e., the actual form in which contest will be fetched for display in the app
 */
@Parcelize
data class Contest(
    val id: String,
    val name: String,
    val startTimeMilliseconds: Long,
    val endTimeSeconds: Long,
    var site: SiteType,
    val websiteUrl: String,
    var isNotificationSet: Boolean
) : Parcelable {
    @IgnoredOnParcel
    val hasStarted = startTimeMilliseconds < System.currentTimeMillis()

    override fun equals(other: Any?): Boolean {
        val newItem = other as Contest
        return when {
            this.id != newItem.id || this.name != newItem.name ||
                    this.startTimeMilliseconds != newItem.startTimeMilliseconds ||
                    this.endTimeSeconds != newItem.endTimeSeconds || this.site != newItem.site ||
                    this.websiteUrl != newItem.websiteUrl -> false
            else -> true
        }
    }

    fun asDatabaseModel(): DatabaseContest = DatabaseContest(
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
        result = 31 * result + hasStarted.hashCode()
        return result
    }
}