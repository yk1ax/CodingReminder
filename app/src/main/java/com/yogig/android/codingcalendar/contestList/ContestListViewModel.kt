package com.yogig.android.codingcalendar.contestList

import android.app.Application
import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.os.Build
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.yogig.android.codingcalendar.R
import com.yogig.android.codingcalendar.database.ContestDatabase
import com.yogig.android.codingcalendar.network.NetworkContest
import com.yogig.android.codingcalendar.network.NetworkRequests
import com.yogig.android.codingcalendar.repository.Contest
import com.yogig.android.codingcalendar.repository.ContestRepository
import kotlinx.coroutines.*
import java.io.IOException

enum class SITE_TYPE(val type: Int) {
    CODEFORCES_SITE(1),
    CODECHEF_SITE(2)
}

class ContestListViewModel(database: ContestDatabase, app: Application) : AndroidViewModel(app) {

    private val viewModelJob = Job()

    private val coroutineScope = CoroutineScope(viewModelJob + Dispatchers.Main)

    override fun onCleared() {
        super.onCleared()
        viewModelJob.cancel()
    }



    private val _progressBarVisible = MutableLiveData<Boolean>(true)
    val progressBarVisible: LiveData<Boolean>
        get() = _progressBarVisible

    private val _snackBarText = MutableLiveData<String?>()
    val snackBarText: LiveData<String?>
        get() = _snackBarText

    val repository: ContestRepository = ContestRepository(database)
    val currentContestList: LiveData<List<Contest>>

    init {
        currentContestList = repository.contests

        if (checkConnection()) {
            fetchContests()
        } else {
            _snackBarText.value = app.getString(R.string.no_internet)
            _progressBarVisible.value = false
        }
    }

    fun retryFetching() {
        if (checkConnection()) {
            fetchContests()
        } else {
            _snackBarText.value = getApplication<Application>().getString(R.string.no_internet)
            _progressBarVisible.value = false
        }
    }

    private fun fetchContests() {
        coroutineScope.launch {

            try {
                withContext(Dispatchers.IO) {
                    repository.refreshContests()
                }
                _snackBarText.value =
                    getApplication<Application>().getString(R.string.success_fetch_contests)
            } catch (e: IOException) {
                Log.e("ContestListViewModel", "Failed to fetch contests. $e")
                _snackBarText.value =
                    getApplication<Application>().getString(R.string.failed_fetch_contests)
            }

            _progressBarVisible.value = false
        }
    }

    fun onCompleteSnackBarEvent() {
        _snackBarText.value = null
    }

    // Check Internet Connection
    private fun checkConnection(): Boolean {
        val cm = getApplication<Application>().applicationContext
            .getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager

        return if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
            val netInfo = cm.activeNetworkInfo
            netInfo?.isConnectedOrConnecting == true
        } else {
            val activeNetwork = cm.activeNetwork
            val capabilities = cm.getNetworkCapabilities(activeNetwork)

            when {
                capabilities == null -> false
                capabilities.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR)
                        or capabilities.hasTransport(NetworkCapabilities.TRANSPORT_WIFI)
                        or capabilities.hasTransport(NetworkCapabilities.TRANSPORT_ETHERNET)
                -> true
                else -> false
            }
        }
    }
}