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

    @Suppress("DEPRECATION")
    override suspend fun getNearbyLocalityNames(latitude: Double, longitude: Double): List<String> {
        return withContext(Dispatchers.IO) {
            val names = mutableSetOf<String>()
            // Sample points in a grid around the user (~0.15 degrees ≈ 15-17km spacing)
            val offsets = listOf(-0.30, -0.15, 0.0, 0.15, 0.30)
            for (dLat in offsets) {
                for (dLon in offsets) {
                    try {
                        val addresses = geocoder.getFromLocation(
                            latitude + dLat, longitude + dLon, 1
                        )
                        addresses?.firstOrNull()?.locality?.let { names.add(it) }
                    } catch (_: Exception) {
                        // skip this sample point
                    }
                }
            }
            names.toList()
        }
    }
}
