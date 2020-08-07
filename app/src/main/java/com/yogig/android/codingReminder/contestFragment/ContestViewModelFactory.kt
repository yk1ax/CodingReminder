package com.yogig.android.codingReminder.contestFragment

import android.app.Application
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.yogig.android.codingReminder.database.ContestDatabase
import com.yogig.android.codingReminder.repository.Contest
import java.lang.IllegalArgumentException

class ContestViewModelFactory(private val app: Application, private val database: ContestDatabase, private val contest: Contest): ViewModelProvider.Factory {

    @Suppress("unchecked_cast")
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        if(modelClass.isAssignableFrom(ContestViewModel::class.java)) {
            return ContestViewModel(app, database, contest) as T
        }

        throw IllegalArgumentException("Unknown ViewModel class.")
    }
}