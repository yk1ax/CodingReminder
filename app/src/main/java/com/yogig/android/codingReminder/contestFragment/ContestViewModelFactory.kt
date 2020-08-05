package com.yogig.android.codingReminder.contestFragment

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.yogig.android.codingReminder.database.ContestDatabase
import java.lang.IllegalArgumentException

class ContestViewModelFactory(private val database: ContestDatabase): ViewModelProvider.Factory {

    @Suppress("unchecked_cast")
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        if(modelClass.isAssignableFrom(ContestViewModel::class.java)) {
            return ContestViewModel(database) as T
        }

        throw IllegalArgumentException("Unknown ViewModel class.")
    }
}