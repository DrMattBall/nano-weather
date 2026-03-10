package com.nanoweather.data.remote.dto

import com.google.gson.annotations.SerializedName

data class WeatherResponseDto(
    @SerializedName("current_weather") val currentWeather: CurrentWeatherDto,
    val daily: DailyDto,
    val hourly: HourlyDto
)

data class CurrentWeatherDto(
    val temperature: Double,
    @SerializedName("weathercode") val weatherCode: Int
)

data class DailyDto(
    val time: List<String>,
    @SerializedName("temperature_2m_max") val temperatureMax: List<Double>,
    @SerializedName("temperature_2m_min") val temperatureMin: List<Double>,
    @SerializedName("uv_index_max") val uvIndexMax: List<Double>,
    @SerializedName("precipitation_probability_max") val precipitationProbabilityMax: List<Int>,
    @SerializedName("weather_code") val weatherCode: List<Int>
)

data class HourlyDto(
    val time: List<String>,
    @SerializedName("temperature_2m") val temperature: List<Double>,
    val precipitation: List<Double>,
    @SerializedName("uv_index") val uvIndex: List<Double>
)
