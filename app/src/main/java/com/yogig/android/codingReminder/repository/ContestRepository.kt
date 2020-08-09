package com.yogig.android.codingReminder.repository

import androidx.lifecycle.Transformations
import com.yogig.android.codingReminder.database.ContestDatabase
import com.yogig.android.codingReminder.database.asDomainModel
import com.yogig.android.codingReminder.network.NetworkRequests
import com.yogig.android.codingReminder.network.asDatabaseModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.IOException

/**
 * Repository layer for the App
 */
class ContestRepository(private val database: ContestDatabase) {

    /**
     * List of liveData of DatabaseContest(s) is converted into a list of LiveData of Contest(s)
     * with the order such that if it is a future contest the ordered by its start time
     * else by its end time
     */
    val contests = Transformations.map(database.contestDao.getContests()) { list ->
        val curTime = System.currentTimeMillis()
        list.asDomainModel().sortedBy {
            if (it.startTimeMilliseconds >= curTime) it.startTimeMilliseconds
            else it.endTimeSeconds
        }
    }

    /**
     * This function implements the functionality for the data holder of the repository layer
     * 1.   It validates if all the contests in the database are yet to *end* and
     *      if not then it removes those contests
     * 2.   It makes the Network requests by @see[NetworkRequests.contestsFromNetwork]
     *      and saves int
     * 3.   For every contest in the fetched list it checks if a contest with the same id
     *      exists in the database, if not then it inserts the contest in the database
     *      else it checks if all the contests of the fetched contest are same as the
     *      contest in the database, if not then it updates the contest
     * @return[Int] It returns the number of new contests added / updated in the database
     */

    suspend fun refreshContests(): Int {
        val curTime = System.currentTimeMillis()
        database.contestDao.validateContests(curTime)
        val contests = NetworkRequests.contestsFromNetwork().asDatabaseModel()

        var newContests = 0
        for (newContest in contests) {
            val contest = database.contestDao.getContest(newContest.id)
            if (contest == null) {
                database.contestDao.insertContest(newContest)
                newContests++
            } else {
                if (contest != newContest) {
                    database.contestDao.updateContest(newContest)
                    newContests++
                }
            }
        }
        return newContests
    }
}