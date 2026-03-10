package com.nanoweather.data.repository

import com.nanoweather.domain.model.AirQuality

interface AirQualityRepository {
    suspend fun getAirQuality(latitude: Double, longitude: Double): Result<AirQuality>
}
