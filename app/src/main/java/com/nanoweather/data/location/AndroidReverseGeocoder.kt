package com.nanoweather.data.location

import android.content.Context
import android.location.Geocoder
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.util.Locale

class AndroidReverseGeocoder(context: Context) : ReverseGeocoder {

    private val geocoder = Geocoder(context, Locale.getDefault())

    @Suppress("DEPRECATION")
    override suspend fun getLocalityName(latitude: Double, longitude: Double): String? {
        return withContext(Dispatchers.IO) {
            try {
                val addresses = geocoder.getFromLocation(latitude, longitude, 1)
                addresses?.firstOrNull()?.locality
            } catch (_: Exception) {
                null
            }
        }
    }
}
