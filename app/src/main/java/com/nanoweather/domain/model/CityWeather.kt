package com.nanoweather.domain.model

data class CityWeather(
    val current: CurrentWeather,
    val dailyForecasts: List<DailyForecast>,
    val hourly: List<HourlyForecast>,
    val currentUvIndex: Double
)
