package com.nanoweather.data.location

data class DeviceLocation(val latitude: Double, val longitude: Double)

interface LocationProvider {
    suspend fun getLastLocation(): DeviceLocation?
}
