package com.nezspencer.callpolice

import com.google.firebase.database.Exclude

data class ContactByState(
    val state: String,
    val phones: MutableList<String>,
    @get: Exclude @set: Exclude var isSelected: Boolean = false
) {
    constructor() : this("", mutableListOf<String>())
}