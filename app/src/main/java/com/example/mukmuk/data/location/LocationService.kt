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
        private const val CACHE_TTL_MS = 5 * 60 * 1000L // 5 minutes
    }

    private val fusedClient = LocationServices.getFusedLocationProviderClient(context)

    private var cachedLocation: LatLng? = null
    private var cachedTime: Long = 0L

    suspend fun getCurrentLocation(): LatLng {
        if (!hasLocationPermission()) return DEFAULT_LOCATION

        val now = System.currentTimeMillis()
        if (cachedLocation != null && now - cachedTime < CACHE_TTL_MS) {
            return cachedLocation!!
        }

        return try {
            val location = getLastOrCurrentLocation()
            if (location != null) {
                val result = LatLng(location.latitude, location.longitude)
                cachedLocation = result
                cachedTime = now
                result
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
        // Try lastLocation first for faster response
        val lastLocation = suspendCancellableCoroutine<Location?> { cont ->
            fusedClient.lastLocation
                .addOnSuccessListener { location -> cont.resume(location) }
                .addOnFailureListener { cont.resume(null) }
        }
        if (lastLocation != null) return lastLocation

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
