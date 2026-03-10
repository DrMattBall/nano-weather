package com.nanoweather.data.location

import android.annotation.SuppressLint
import android.content.Context
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.Priority
import com.google.android.gms.tasks.CancellationTokenSource
import kotlinx.coroutines.tasks.await

class FusedLocationProvider(context: Context) : LocationProvider {

    private val client = LocationServices.getFusedLocationProviderClient(context)

    @SuppressLint("MissingPermission")
    override suspend fun getLastLocation(): DeviceLocation? {
        return try {
            val location = client.getCurrentLocation(
                Priority.PRIORITY_BALANCED_POWER_ACCURACY,
                CancellationTokenSource().token
            ).await()
            location?.let { DeviceLocation(it.latitude, it.longitude) }
        } catch (_: SecurityException) {
            null
        } catch (_: Exception) {
            null
        }
    }
}
