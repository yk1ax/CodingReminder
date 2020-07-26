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
import com.yogig.android.codingcalendar.network.NetworkContest
import com.yogig.android.codingcalendar.network.NetworkRequests
import kotlinx.coroutines.*
import java.io.IOException

enum class SITE_TYPE(val type: Int) {
    CODEFORCES_SITE(1),
    CODECHEF_SITE(2)
}

class ContestListViewModel(app: Application) : AndroidViewModel(app) {

    private val viewModelJob = Job()

    private val coroutineScope = CoroutineScope(viewModelJob + Dispatchers.Main)

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

    private val _snackBarText = MutableLiveData<String?>()
    val snackBarText: LiveData<String?>
        get() = _snackBarText

    init {

        if (checkConnection()) {
            fetchContests()
        } else {
            _snackBarText.value = app.getString(R.string.no_internet)
            _progressBarVisible.value = false
        }
    }

    private fun fetchContests() {
        coroutineScope.launch {
            val contests = mutableListOf<NetworkContest>()

            try {
                withContext(Dispatchers.IO) {
                    contests.addAll(NetworkRequests.contestsFromNetwork())
                }
                _snackBarText.value =
                    getApplication<Application>().getString(R.string.success_fetch_contests)
            } catch (e: IOException) {
                Log.e("ContestListViewModel", "Failed to fetch contests. $e")
                _snackBarText.value =
                    getApplication<Application>().getString(R.string.failed_fetch_contests)
            }


            _networkContestList.value = contests
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

        return if (Build.VERSION.SDK_INT < Build.VERSION_CODES.O) {
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