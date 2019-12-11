package com.nezspencer.callpolice

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.google.firebase.database.DatabaseReference

class MainViewModelFactory(private val reference: DatabaseReference) : ViewModelProvider.Factory {
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(MainViewModel::class.java)) {
            return MainViewModel(reference) as T
        }
        throw IllegalArgumentException("Unknown viewModel class")
    }
}