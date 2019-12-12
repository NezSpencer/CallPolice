package com.nezspencer.callpolice

import android.content.Context
import android.location.Address
import android.location.Geocoder
import android.location.Location
import android.os.Looper
import androidx.lifecycle.LiveData
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationResult
import java.util.*

class LocationLivedata(
    context: Context,
    fusedLocationProviderClient: FusedLocationProviderClient,
    mainLooper: Looper
) : LiveData<String>() {

    private val locationCallback = object : LocationCallback() {
        override fun onLocationResult(p0: LocationResult?) {
            p0 ?: return
            var userLocation: Location? = null
            for (location in p0.locations) {
                userLocation = location
                break
            }
            userLocation?.let {
                val geoCoder = Geocoder(context, Locale.getDefault())
                var addresses = emptyList<Address>()
                try {
                    addresses = geoCoder.getFromLocation(it.latitude, it.longitude, 1)
                } catch (ex: Exception) {
                    // do nothing
                }

                if (addresses.isNotEmpty()) {
                    value = addresses[0].adminArea
                    fusedLocationProviderClient.removeLocationUpdates(this)
                }
            }
        }
    }
    private val locationRequest = LocationRequest.create().apply {
        priority = LocationRequest.PRIORITY_HIGH_ACCURACY
        interval = 10 * 1000 //10 seconds
    }

    init {
        fusedLocationProviderClient.requestLocationUpdates(
            locationRequest,
            locationCallback,
            mainLooper
        )
    }
}