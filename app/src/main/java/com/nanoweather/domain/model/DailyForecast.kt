package com.nanoweather.domain.model

data class DailyForecast(
    val date: String,
    val highTemp: Double,
    val lowTemp: Double,
    val uvIndexMax: Double,
    val precipitationProbability: Int,
    val weatherCode: Int
)
