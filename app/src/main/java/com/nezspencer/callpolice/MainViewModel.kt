package com.nezspencer.callpolice

import android.content.Context
import android.os.Looper
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.ViewModel
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.firebase.database.DatabaseReference
import java.util.*

class MainViewModel(
    context: Context,
    currentNode: DatabaseReference,
    fusedLocationProviderClient: FusedLocationProviderClient,
    mainLooper: Looper
) : ViewModel() {
    var userState: String? = null
    val contactList = mutableListOf<ContactByState>()
    var stateContact: ContactByState? = null
    val firebaseLiveData = FirebaseLiveData(currentNode)
    val locationLiveData = LocationLivedata(context, fusedLocationProviderClient, mainLooper)
    val locationContactsMediator = MediatorLiveData<Boolean>().apply {
        addSource(firebaseLiveData) {
            it ?: return@addSource
            contactList.clear()
            contactList.addAll(it)
            value =
                !firebaseLiveData.value.isNullOrEmpty() && !locationLiveData.value.isNullOrBlank()
        }
        addSource(locationLiveData) {
            it ?: return@addSource
            userState = it
            value =
                !firebaseLiveData.value.isNullOrEmpty() && !locationLiveData.value.isNullOrBlank()
        }
    }

    fun findPhoneNumbersForUserState(state: String): ContactByState? {
        return contactList.find {
            it.state.toLowerCase(Locale.ENGLISH) == state.toLowerCase(Locale.ENGLISH)
        }
    }
}