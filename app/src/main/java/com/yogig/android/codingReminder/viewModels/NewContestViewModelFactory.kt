package com.yogig.android.codingReminder.viewModels

import android.app.Application
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.yogig.android.codingReminder.database.ContestDatabase
import java.lang.IllegalArgumentException

class NewContestViewModelFactory(private val database : ContestDatabase, private val app: Application) : ViewModelProvider.Factory {
    @Suppress("unchecked_cast")
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        if(modelClass.isAssignableFrom(NewContestViewModel::class.java)) {
            return NewContestViewModel(
                database,
                app
            ) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}