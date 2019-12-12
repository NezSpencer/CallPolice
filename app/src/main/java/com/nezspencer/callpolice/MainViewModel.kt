package com.nezspencer.callpolice

import android.content.Context
import android.os.Looper
import androidx.lifecycle.ViewModel
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.firebase.database.DatabaseReference

class MainViewModel(
    context: Context,
    currentNode: DatabaseReference,
    fusedLocationProviderClient: FusedLocationProviderClient,
    mainLooper: Looper
) : ViewModel() {
    val firebaseLiveData = FirebaseLiveData(currentNode)
    val locationLiveData = LocationLivedata(context, fusedLocationProviderClient, mainLooper)
}