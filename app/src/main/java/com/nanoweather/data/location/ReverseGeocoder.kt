package com.nanoweather.data.location

interface ReverseGeocoder {
    suspend fun getLocalityName(latitude: Double, longitude: Double): String?
}
