package com.nezspencer.callpolice

import android.content.Context
import android.os.Looper
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.firebase.database.DatabaseReference

class MainViewModelFactory(
    private val context: Context,
    private val fusedLocationProviderClient: FusedLocationProviderClient,
    private val mainLooper: Looper,
    private val reference: DatabaseReference
) : ViewModelProvider.Factory {
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(MainViewModel::class.java)) {
            return MainViewModel(context, reference, fusedLocationProviderClient, mainLooper) as T
        }
        throw IllegalArgumentException("Unknown viewModel class")
    }
}