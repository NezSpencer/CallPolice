package com.nezspencer.callpolice

import androidx.lifecycle.ViewModel
import com.google.firebase.database.DatabaseReference

class MainViewModel(currentNode: DatabaseReference) : ViewModel() {
    val firebaseLiveData = FirebaseLiveData(currentNode)
}