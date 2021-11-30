package com.example.testforwemo

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.location.Location
import android.location.LocationListener
import android.location.LocationManager
import android.os.Bundle
import androidx.core.app.ActivityCompat
import timber.log.Timber
import java.lang.Exception

object LocationGettingUtil {

    fun isGpsEnable(context: Context?): Boolean {
        if (context == null) return false
        val locationManager = context
            .getSystemService(Context.LOCATION_SERVICE) as LocationManager

        // Getting GPS status
        val isGPSEnabled = locationManager
            .isProviderEnabled(LocationManager.GPS_PROVIDER)

        // Getting network status
        val isNetworkEnabled = locationManager
            .isProviderEnabled(LocationManager.NETWORK_PROVIDER)
        return isGPSEnabled || isNetworkEnabled
    }

    fun getCurrentLocation(context: Context?): Location? {
        if (context == null) return null
        var location: Location? = null
        try {
            val locationManager = context
                .getSystemService(Context.LOCATION_SERVICE) as LocationManager

            // Getting GPS status
            val isGPSEnabled = locationManager
                .isProviderEnabled(LocationManager.GPS_PROVIDER)

            // Getting network status
            val isNetworkEnabled =
                locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER)
            val locationListener: LocationListener = object : LocationListener {
                override fun onLocationChanged(location: Location) {
                    Timber.i("onLocationChanged , Lat: ${location.latitude}  Lon: ${location.longitude}")
                }

                override fun onStatusChanged(provider: String, status: Int, extras: Bundle) {}
                override fun onProviderEnabled(provider: String) {}
                override fun onProviderDisabled(provider: String) {}
            }
            if (ActivityCompat.checkSelfPermission(
                    context,
                    Manifest.permission.ACCESS_FINE_LOCATION
                ) == PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(
                    context,
                    Manifest.permission.ACCESS_COARSE_LOCATION
                ) == PackageManager.PERMISSION_GRANTED
            ) {
                if (isNetworkEnabled) {
                    locationManager.requestSingleUpdate(
                        LocationManager.NETWORK_PROVIDER,
                        locationListener,
                        null
                    )
                    location =
                        locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER)
                    if (location != null) {
                        Timber.i("Get Location from NETWORK_PROVIDER , Lat: ${location.latitude}  Lon: ${location.longitude}")
                    }
                } else if (isGPSEnabled) {
                    locationManager.requestSingleUpdate(
                        LocationManager.GPS_PROVIDER,
                        locationListener,
                        null
                    )
                    location = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER)
                    if (location != null) {
                        Timber.i("Get Location from GPS_PROVIDER , Lat: ${location.latitude}  Lon: ${location.longitude}")
                    }
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return location
    }
}
