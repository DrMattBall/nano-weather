package com.nanoweather.data.remote.dto

import com.google.gson.annotations.SerializedName

data class AirQualityResponseDto(
    val current: AirQualityCurrentDto,
    val hourly: AirQualityHourlyDto
)

data class AirQualityCurrentDto(
    @SerializedName("us_aqi") val usAqi: Int,
    @SerializedName("pm2_5") val pm25: Double,
    val pm10: Double
)

data class AirQualityHourlyDto(
    @SerializedName("grass_pollen") val grassPollen: List<Double?>,
    @SerializedName("birch_pollen") val birchPollen: List<Double?>,
    @SerializedName("ragweed_pollen") val ragweedPollen: List<Double?>
)
