package com.example.user.bartender.ingredients

import android.app.Application
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.user.bartender.database.BartenderDatabaseDao

@Suppress("UNCHECKED_CAST")
class IngredientsViewModelFactory (
        private val dataSource: BartenderDatabaseDao,
        private val application: Application): ViewModelProvider.Factory{
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(IngredientsViewModel::class.java)) {
            return IngredientsViewModel(dataSource, application) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }

}