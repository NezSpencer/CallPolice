package com.nezspencer.callpolice

import androidx.lifecycle.LiveData
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ValueEventListener

class FirebaseLiveData(databaseReference: DatabaseReference) :
    LiveData<List<ContactByState>>(), ValueEventListener {
    private val backingList = mutableListOf<ContactByState>()

    init {
        databaseReference.addValueEventListener(this)
    }

    override fun onCancelled(p0: DatabaseError) {}

    override fun onDataChange(p0: DataSnapshot) {
        backingList.clear()
        for (child in p0.children) {
            backingList.add(child.getValue(ContactByState::class.java)!!)
        }
        value = backingList
    }
}