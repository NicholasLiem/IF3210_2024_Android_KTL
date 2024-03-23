package com.ktl.bondoman.utils

import android.Manifest
import android.app.Activity
import android.content.pm.PackageManager
import android.location.Location
import androidx.core.app.ActivityCompat
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.tasks.Task

object LocationUtils {

    fun getLastKnownLocation(activity: Activity, callback: (String) -> Unit) {
        if (ActivityCompat.checkSelfPermission(activity, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
            ActivityCompat.checkSelfPermission(activity, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

            PermissionUtils.requestLocationPermissions(activity, PermissionUtils.LOCATION_PERMISSION_REQUEST_CODE)
            return
        }

        val fusedLocationClient: FusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(activity)

        val locationTask: Task<Location> = fusedLocationClient.lastLocation
        locationTask.addOnSuccessListener { location: Location? ->
            location?.let {
                val locationString = "Lat: ${it.latitude}, Lon: ${it.longitude}"
                callback(locationString)
            } ?: run {
                callback("Location not available")
            }
        }.addOnFailureListener {
            callback("Failed to get location")
        }
    }
}
