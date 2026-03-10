package com.nanoweather.data.remote.api

import com.nanoweather.data.remote.dto.AirQualityResponseDto
import retrofit2.http.GET
import retrofit2.http.Query

interface AirQualityApiService {
    @GET("v1/air-quality")
    suspend fun getAirQuality(
        @Query("latitude") latitude: Double,
        @Query("longitude") longitude: Double,
        @Query("current") current: String = "us_aqi,pm2_5,pm10",
        @Query("hourly") hourly: String = "grass_pollen,birch_pollen,ragweed_pollen",
        @Query("forecast_days") forecastDays: Int = 1,
        @Query("timezone") timezone: String = "auto"
    ): AirQualityResponseDto
}
