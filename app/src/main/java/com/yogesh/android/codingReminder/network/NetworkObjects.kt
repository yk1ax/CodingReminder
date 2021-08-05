package com.yogesh.android.codingReminder.network

import com.google.gson.annotations.SerializedName
import com.yogesh.android.codingReminder.viewModels.SiteType
import com.yogesh.android.codingReminder.database.DatabaseContest
import java.text.SimpleDateFormat
import java.util.*

data class ClistContestList(
    @SerializedName("objects")
    val contestList: List<ClistContest>
)

data class Resource(
    val icon: String,
    val id: Long,
    val name: String
)

data class ClistContest(
    val id: Long,
    @SerializedName("event")
    val name: String,
    val duration: Long,
    val start: String,
    val end: String,
    val resource: Resource,
    val href: String,
    var site: SiteType
)

fun List<ClistContest>.asTempDatabaseModel(): List<DatabaseContest> {
    return map { it ->
        DatabaseContest(
            it.id,
            it.name,
            (SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.US).parse(it.start)?.time ?: 0L) + 1000L * 3600 * 11/2,
            (SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.US).parse(it.end)?.time ?: 0L) + 1000L * 3600 * 11/2,
            it.site, // This needs to be determined first before calling asTempDatabaseModel()
            it.href,
            false
        )
    }
}