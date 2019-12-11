package com.nezspencer.callpolice

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProviders
import com.google.firebase.database.FirebaseDatabase

class MainActivity : AppCompatActivity() {
    private lateinit var firebaseDatabase: FirebaseDatabase
    private lateinit var viewModel: MainViewModel
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        firebaseDatabase = FirebaseDatabase.getInstance()
        viewModel = ViewModelProviders.of(
            this,
            MainViewModelFactory(firebaseDatabase.reference)
        )[MainViewModel::class.java]
        supportFragmentManager.beginTransaction().replace(R.id.frame_content, MainFragment())
            .addToBackStack(MainFragment::javaClass.name).commit()
    }
}
