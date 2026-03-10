package com.nanoweather.data.remote.api

import com.nanoweather.data.remote.dto.WeatherResponseDto
import retrofit2.http.GET
import retrofit2.http.Query

interface WeatherApiService {
    @GET("v1/forecast")
    suspend fun getForecast(
        @Query("latitude") latitude: Double,
        @Query("longitude") longitude: Double,
        @Query("current_weather") currentWeather: Boolean = true,
        @Query("daily") daily: String = "temperature_2m_max,temperature_2m_min",
        @Query("hourly") hourly: String = "temperature_2m,precipitation",
        @Query("forecast_days") forecastDays: Int = 1,
        @Query("timezone") timezone: String = "auto"
    ): WeatherResponseDto
}
