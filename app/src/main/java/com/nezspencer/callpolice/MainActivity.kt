package com.nezspencer.callpolice

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.database.FirebaseDatabase

class MainActivity : AppCompatActivity() {
    private lateinit var firebaseDatabase: FirebaseDatabase
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        firebaseDatabase = FirebaseDatabase.getInstance()
        supportFragmentManager.beginTransaction().replace(R.id.frame_content, MainFragment())
            .addToBackStack(MainFragment::javaClass.name).commit()
    }
}
