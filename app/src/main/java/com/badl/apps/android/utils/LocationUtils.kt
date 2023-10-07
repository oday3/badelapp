package com.badl.apps.android.utils

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.content.pm.PackageManager
import android.location.Geocoder
import android.location.LocationManager
import android.os.Looper
import android.util.Log
import androidx.core.content.ContextCompat
import com.badl.apps.android.baseClasses.ui.BaseActivity
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.maps.model.LatLng
import java.util.*

object LocationUtils {


    fun isLocationEnabled(locationManager: LocationManager): Boolean {
        return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)
    }


    fun BaseActivity.storeUserLocation(userLocation: LatLng) {

        sharedPrefUtils.lastUserLocation =
            "${userLocation.latitude}//${userLocation.longitude}"
    }


    @SuppressLint("MissingPermission")
    fun requestNewLocationData(
        fusedLocationPc: FusedLocationProviderClient,
        locationCallback: LocationCallback, looper: Looper
    ) {

        // Initializing LocationRequest
        // object with appropriate methods
        val mLocationRequest = LocationRequest.create()
        mLocationRequest.priority = LocationRequest.PRIORITY_HIGH_ACCURACY
        mLocationRequest.interval = 5
        mLocationRequest.fastestInterval = 0
        mLocationRequest.numUpdates = 1
        Log.e("location_test", "requestNewLocationData")
        // setting LocationRequest
        // on FusedLocationClient
        fusedLocationPc.requestLocationUpdates(
            mLocationRequest,
            locationCallback,
            looper
        )
    }

    fun isFinePermissionLocationGranted(context: Context): Boolean {

        return ContextCompat.checkSelfPermission(
            context, Manifest.permission.ACCESS_FINE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED
    }

    fun isCoarsePermissionLocationGranted(context: Context): Boolean {

        return ContextCompat.checkSelfPermission(
            context, Manifest.permission.ACCESS_COARSE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED
    }

    fun BaseActivity.isLocationExist(): Boolean {

        return sharedPrefUtils.lastUserLocation != null
                && sharedPrefUtils.lastUserLocation?.isNotEmpty() == true
    }

    fun BaseActivity.getUserLocation(): kotlin.collections.List<String> {

        return sharedPrefUtils.lastUserLocation.toString().split("//")
    }

    fun getAddressFromLocation(location: LatLng, geocoder: Geocoder): String? {


        val addresses =
            geocoder.getFromLocation(location.latitude, location.longitude, 1)

        if (addresses?.isNotEmpty() == true) {

            //addresses[0].featureName + ", " + addresses[0].locality + ", " + addresses[0].adminArea + ", " + addresses[0].countryName
           return addresses[0].getAddressLine(0)
        }

        return null
    }

    fun getCityFromLocation(location: LatLng, geocoder: Geocoder): String? {


        val addresses =
            geocoder.getFromLocation(location.latitude, location.longitude, 1)



        if (addresses?.isNotEmpty() == true) {

            val address = addresses.get(0)

            Log.e("location_name_ttt",
                " subLocality (neighborhood) = ${address?.subLocality} " +
                     " locality (city) = ${address?.locality} " +
                     " subAdminArea () = ${address?.subAdminArea} " +
                     " adminArea () = ${address?.adminArea} ")

            //addresses[0].featureName + ", " + addresses[0].locality + ", " + addresses[0].adminArea + ", " + addresses[0].countryName
            return "${address.subLocality ?: ""} ${address.locality ?: ""}"
        }

        return null
    }

    suspend fun geLocationName (centerOfMap: LatLng, geocoder: Geocoder): String? {

        return getAddressFromLocation(centerOfMap, geocoder)
    }
}