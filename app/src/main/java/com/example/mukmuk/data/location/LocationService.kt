package com.example.mukmuk.data.location

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.location.Location
import androidx.core.content.ContextCompat
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.Priority
import com.google.android.gms.tasks.CancellationTokenSource
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlin.coroutines.resume

data class LatLng(val latitude: Double, val longitude: Double)

class LocationService(private val context: Context) {

    companion object {
        val DEFAULT_LOCATION = LatLng(37.4979, 127.0276)
    }

    private val fusedClient = LocationServices.getFusedLocationProviderClient(context)

    suspend fun getCurrentLocation(): LatLng {
        if (!hasLocationPermission()) return DEFAULT_LOCATION

        return try {
            val location = getLastOrCurrentLocation()
            if (location != null) {
                LatLng(location.latitude, location.longitude)
            } else {
                DEFAULT_LOCATION
            }
        } catch (_: Exception) {
            DEFAULT_LOCATION
        }
    }

    fun hasLocationPermission(): Boolean {
        return ContextCompat.checkSelfPermission(
            context, Manifest.permission.ACCESS_FINE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED ||
        ContextCompat.checkSelfPermission(
            context, Manifest.permission.ACCESS_COARSE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED
    }

    @Suppress("MissingPermission")
    private suspend fun getLastOrCurrentLocation(): Location? {
        return suspendCancellableCoroutine { cont ->
            val cts = CancellationTokenSource()
            cont.invokeOnCancellation { cts.cancel() }

            fusedClient.getCurrentLocation(Priority.PRIORITY_BALANCED_POWER_ACCURACY, cts.token)
                .addOnSuccessListener { location ->
                    cont.resume(location)
                }
                .addOnFailureListener {
                    cont.resume(null)
                }
        }
    }
}
