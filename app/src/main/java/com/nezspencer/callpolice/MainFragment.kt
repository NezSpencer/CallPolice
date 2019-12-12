package com.nezspencer.callpolice

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.os.Looper
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationServices
import com.google.firebase.database.FirebaseDatabase
import kotlinx.android.synthetic.main.fragment_main.*
import java.util.*

class MainFragment : Fragment() {
    private lateinit var mainViewModel: MainViewModel
    private val contactList = mutableListOf<ContactByState>()
    private var userState: String? = null
    private var stateContact: ContactByState? = null
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_main, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val client = LocationServices.getFusedLocationProviderClient(activity!!)
        val locationRequest = LocationRequest.create()
        locationRequest.priority = LocationRequest.PRIORITY_HIGH_ACCURACY
        locationRequest.interval = 10 * 1000 //10 seconds
        mainViewModel = ViewModelProviders.of(
            activity!!,
            MainViewModelFactory(
                activity!!,
                client,
                Looper.getMainLooper(),
                FirebaseDatabase.getInstance().reference
            )
        )[MainViewModel::class.java]
        mainViewModel.firebaseLiveData.observe(this, Observer {
            it ?: return@Observer
            contactList.clear()
            contactList.addAll(it)
        })

        tv_show_list.setOnClickListener {
            activity!!.supportFragmentManager.beginTransaction()
                .replace(R.id.frame_content, PoliceContactListFragment())
                .addToBackStack(PoliceContactListFragment::javaClass.name)
                .commit()
        }
        tv_call.setOnClickListener {
            stateContact?.let {
                makeDialIntent(it.phones[0])
            }

        }
        requestLocationPermission()
    }

    private fun requestLocationPermission() {
        if (activity!!.checkCallingOrSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(arrayOf(Manifest.permission.ACCESS_FINE_LOCATION), RC_LOCATION)
        } else {
            getLocationAddress()
        }
    }

    private fun getLocationAddress() {
        mainViewModel.locationLiveData.observe(this, Observer {
            it ?: return@Observer
            tv_call.text = getString(
                R.string.call_police_btn_prompt,
                it
            )
            if (contactList.isNotEmpty() && userState != null) {
                stateContact = findPhoneNumbersForUserState(it)
            }
        })
    }

    private fun findPhoneNumbersForUserState(state: String): ContactByState? {
        return contactList.find {
            it.state.toLowerCase(Locale.ENGLISH) == state.toLowerCase(Locale.ENGLISH)
        }
    }

    private fun makeDialIntent(phoneNumber: String) {
        val callIntent = Intent(Intent.ACTION_DIAL)
        callIntent.data = Uri.parse("tel:$phoneNumber")
        if (callIntent.resolveActivity(activity!!.packageManager) != null)
            startActivity(callIntent)

    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        if (requestCode == RC_LOCATION) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                getLocationAddress()
            }
        }
    }

    companion object {
        const val RC_LOCATION = 109
    }
}