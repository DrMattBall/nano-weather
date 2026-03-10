package com.nanoweather.data.repository

import com.nanoweather.data.remote.api.AirQualityApiService
import com.nanoweather.domain.model.AirQuality

class AirQualityRepositoryImpl(
    private val api: AirQualityApiService
) : AirQualityRepository {

    override suspend fun getAirQuality(latitude: Double, longitude: Double): Result<AirQuality> =
        runCatching {
            val response = api.getAirQuality(latitude, longitude)
            AirQuality(
                usAqi = response.current.usAqi,
                pm25 = response.current.pm25,
                pm10 = response.current.pm10,
                grassPollen = response.hourly.grassPollen.filterNotNull().maxOrNull() ?: 0.0,
                birchPollen = response.hourly.birchPollen.filterNotNull().maxOrNull() ?: 0.0,
                ragweedPollen = response.hourly.ragweedPollen.filterNotNull().maxOrNull() ?: 0.0
            )
        }
}
