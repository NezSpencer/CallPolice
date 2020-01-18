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
import androidx.recyclerview.widget.RecyclerView
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationServices
import com.google.firebase.database.FirebaseDatabase
import kotlinx.android.synthetic.main.fragment_main.*
import kotlinx.android.synthetic.main.item_user_state_phone.view.*

class MainFragment : Fragment() {
    private lateinit var mainViewModel: MainViewModel
    private lateinit var currentStateContactsAdapter: StateContactsAdapter
    private var selectedNumber: String? = null
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

        currentStateContactsAdapter = StateContactsAdapter {
            selectedNumber = it
        }
        rv_state_contact.adapter = currentStateContactsAdapter

        mainViewModel.firebaseLiveData.observe(this, Observer {
            it ?: return@Observer
        })

        mainViewModel.locationContactsMediator.observe(this, Observer {
            it ?: return@Observer
            if (it) {
                // location, contact and state data is ready
                with(mainViewModel) {
                    stateContact = findPhoneNumbersForUserState(userState!!)
                    debug {
                        if (stateContact == null) {
                            stateContact = ContactByState(
                                "California",
                                mutableListOf(
                                    "45744893939",
                                    "47474783809",
                                    "64539267254",
                                    "44673737214"
                                )
                            )
                        }
                    }
                    stateContact?.let { contact ->
                        currentStateContactsAdapter.refreshList(
                            prepareUserStateContacts(
                                contact
                            )
                        )
                    }
                }
            }
        })

        tv_call.setOnClickListener {
            mainViewModel.stateContact?.let {
                makeDialIntent(selectedNumber ?: it.phones[0])
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
        })
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

    class StateContactsAdapter(private val onNumberSelected: (String) -> Unit) :
        RecyclerView.Adapter<StateContactsAdapter.Holder>() {
        private val items = mutableListOf<StateContact>()
        private var lastSelectedItemPosition = 0
        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): Holder {
            return Holder(
                LayoutInflater.from(parent.context).inflate(
                    R.layout.item_user_state_phone,
                    parent,
                    false
                )
            )
        }

        fun refreshList(newItems: List<StateContact>) {
            items.clear()
            items.addAll(newItems)
            notifyDataSetChanged()
        }

        override fun getItemCount() = items.size

        override fun onBindViewHolder(holder: Holder, position: Int) {
            holder.bind(items[position])
        }

        inner class Holder(private val rootView: View) : RecyclerView.ViewHolder(rootView) {
            fun bind(contact: StateContact) {
                with(rootView) {
                    radio_phone.text = contact.phoneNumber
                    radio_phone.isChecked = contact.isSelected
                    radio_phone.setOnCheckedChangeListener { _, b ->
                        if (b) {
                            items[lastSelectedItemPosition].isSelected = false
                            items[adapterPosition].isSelected = true
                            lastSelectedItemPosition = adapterPosition
                            onNumberSelected(contact.phoneNumber)
                            notifyDataSetChanged()
                        }
                    }
                }
            }
        }
    }

    data class StateContact(val phoneNumber: String, var isSelected: Boolean)

    companion object {
        const val RC_LOCATION = 109
        fun newInstance() = MainFragment()
    }
}