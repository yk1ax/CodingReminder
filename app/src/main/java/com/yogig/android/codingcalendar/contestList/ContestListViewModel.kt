package com.yogig.android.codingcalendar.contestList

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.yogig.android.codingcalendar.network.CodeforcesApi
import com.yogig.android.codingcalendar.network.CodeforcesFetching
import com.yogig.android.codingcalendar.network.NetworkContest
import kotlinx.coroutines.*
import org.jsoup.Jsoup
import retrofit2.HttpException
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.*

const val CODEFORCES_SITE = 1
const val CODECHEF_SITE = 2

class ContestListViewModel : ViewModel() {

    private val viewModelJob = Job()

    val coroutineScope = CoroutineScope(viewModelJob + Dispatchers.Main)

    override fun onCleared() {
        super.onCleared()
        viewModelJob.cancel()
    }

    private val _networkContestList = MutableLiveData<List<NetworkContest>>()
    val networkContestList: LiveData<List<NetworkContest>>
        get() = _networkContestList

    private val _progressBarVisible = MutableLiveData<Boolean>(true)
    val progressBarVisible: LiveData<Boolean>
        get() = _progressBarVisible

    init {
        fetchContests()
    }

    private fun fetchContests() {
        _progressBarVisible.value = true
        coroutineScope.launch {
            val contests = mutableListOf<NetworkContest>()
            try {
                contests.addAll(fetchCFContests())
                Log.i(
                    "ContestListViewModel",
                    "Fetched ${contests.size} codeforces contests."
                )
            } catch (e: HttpException) {
                Log.e("ContestListViewModel", "Failed to fetch Codeforces Contests.")
            }

            try {
                contests.addAll(fetchCCContests())
                Log.i(
                    "ContestListViewModel",
                    "Fetched ${contests.size} codechef contests."
                )
            } catch (e: HttpException) {
                Log.e("ContestListViewModel", "Failed to fetch codechef Contests.")
            }

            _networkContestList.value = contests
            _progressBarVisible.value = false
        }
    }

    private suspend fun fetchCFContests(): List<NetworkContest> {
        return withContext(Dispatchers.IO) {
            val list = CodeforcesApi.retrofitService.getContests().contestList
            val contests = mutableListOf<NetworkContest>()
            if (list.isNotEmpty()) {
                loop@ for (contest in list) {
                    contest.site = CODEFORCES_SITE
                    when (contest.phase) {
                        "BEFORE", "CODING" -> contests.add(contest)
                        else -> break@loop
                    }
                }
            }
            return@withContext contests
        }
    }

    private suspend fun fetchCCContests(): List<NetworkContest> {
        return withContext(Dispatchers.IO) {
            val list = mutableListOf<NetworkContest>()
            try {
                list.addAll(CodeforcesFetching.contestList)
            } catch (e: IOException) {
                Log.e("ContestListViewModel", "Failed to fetch Codechef contests.")
            }

            return@withContext list
        }
    }
}