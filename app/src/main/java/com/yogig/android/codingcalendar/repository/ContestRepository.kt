package com.yogig.android.codingcalendar.repository

import androidx.lifecycle.Transformations
import com.yogig.android.codingcalendar.database.ContestDatabase
import com.yogig.android.codingcalendar.database.asDomainModel
import com.yogig.android.codingcalendar.network.NetworkRequests
import com.yogig.android.codingcalendar.network.asDatabaseModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.IOException

class ContestRepository(private val database: ContestDatabase) {

    val contests = Transformations.map(database.contestDao.getContests()) {
        it.asDomainModel()
    }

    @Throws(IOException::class)
    suspend fun refreshContests(): Int {
        val curTime = System.currentTimeMillis()
        return withContext(Dispatchers.IO) {
            database.contestDao.validateContests(curTime)
            val contests = NetworkRequests.contestsFromNetwork().asDatabaseModel()

//            database.contestDao.insertAll(*contests.asDatabaseModel())
            var newContests = 0
            for(newContest in contests) {
                val contest = database.contestDao.getContest(newContest.id)
                if(contest == null) {
                    database.contestDao.insertContest(newContest)
                    newContests++
                } else {
                    if(contest != newContest) {
                        database.contestDao.updateContest(newContest)
                        newContests++
                    }
                }
            }
            newContests
        }
    }
}