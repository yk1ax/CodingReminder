package com.yogesh.android.codingReminder.viewModels

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.yogesh.android.codingReminder.R
import com.yogesh.android.codingReminder.database.ContestDatabase
import com.yogesh.android.codingReminder.network.NetworkRequests
import com.yogesh.android.codingReminder.repository.Contest
import com.yogesh.android.codingReminder.repository.ContestRepository
import kotlinx.coroutines.*
import java.io.IOException

enum class SiteType(val type: Int) {
    UNKNOWN_SITE(0),
    CODEFORCES_SITE(1),
    CODECHEF_SITE(2),
    ATCODER_SITE(3)
}

class ContestListViewModel(database: ContestDatabase, app: Application) : AndroidViewModel(app) {

    private val viewModelJob = SupervisorJob()
    private val coroutineScope = CoroutineScope(viewModelJob + Dispatchers.Main)

    override fun onCleared() {
        super.onCleared()
        viewModelJob.cancel()
    }

    private val _refreshingState = MutableLiveData<Boolean>(true)
    val refreshingState: LiveData<Boolean>
        get() = _refreshingState

    private val _snackBarText = MutableLiveData<String?>()
    val snackBarText: LiveData<String?>
        get() = _snackBarText

    private val _contestEvent = MutableLiveData<Contest?>()
    val contestEvent: LiveData<Contest?>
        get() = _contestEvent

    private val _newContestEvent = MutableLiveData<Boolean>(false)
    val newContestEvent: LiveData<Boolean>
        get() = _newContestEvent

    fun onContestNavigate(contest: Contest) {
        _contestEvent.value = contest
    }

    fun onContestNavigateComplete() {
        _contestEvent.value = null
    }

    fun onNewContestNavigate() {
        _newContestEvent.value = true
    }

    fun onNewContestNavigateComplete() {
        _newContestEvent.value = false
    }

    fun onCompleteSnackBarEvent() {
        _snackBarText.value = null
    }

    private val repository: ContestRepository = ContestRepository(database)
    val currentContestList: LiveData<List<Contest>>
        get() = repository.contests

    init {
        if (NetworkRequests.checkConnection(getApplication())) {
            fetchContests()
        } else {
            _snackBarText.value = app.getString(R.string.no_internet)
            _refreshingState.value = false
        }
    }

    fun retryFetching() {
        if (NetworkRequests.checkConnection(getApplication())) {
            fetchContests()
        } else {
            _snackBarText.value = getApplication<Application>().getString(R.string.no_internet)
            _refreshingState.value = false
        }

    }

    /**
     * Fetches the contests and changes @see[_snackBarText] which is observed in the fragment
     * to generate SnackBar text
     */
    private fun fetchContests() {
        coroutineScope.launch {

            try {
                val new = repository.refreshContests()

                if(new == 0) {
                    _snackBarText.value =
                        getApplication<Application>().getString(R.string.no_new_contest)
                } else {
                    _snackBarText.value =
                        getApplication<Application>().getString(R.string.success_fetch_contests, new)
                }

            } catch (e: IOException) {
                Log.e("ContestListViewModel", "Failed to fetch contests. $e")
                _snackBarText.value =
                    getApplication<Application>().getString(R.string.failed_fetch_contests)
            }
            _refreshingState.value = false

        }
    }
}