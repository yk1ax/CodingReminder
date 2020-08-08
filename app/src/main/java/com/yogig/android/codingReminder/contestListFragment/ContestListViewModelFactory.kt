package com.yogig.android.codingReminder.contestListFragment

import android.app.Application
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider

import com.yogig.android.codingReminder.database.ContestDatabase
import java.lang.IllegalArgumentException

class ContestListViewModelFactory(private val database: ContestDatabase, private val application: Application): ViewModelProvider.Factory {
    @Suppress("unchecked_cast")
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(ContestListViewModel::class.java)) {
            return ContestListViewModel(database, application) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class.")
    }
}