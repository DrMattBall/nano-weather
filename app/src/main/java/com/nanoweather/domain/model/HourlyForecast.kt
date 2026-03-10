package com.nanoweather.domain.model

data class HourlyForecast(
    val time: String,
    val temperature: Double,
    val precipitation: Double
)
