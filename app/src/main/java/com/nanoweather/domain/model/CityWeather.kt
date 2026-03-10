package com.nanoweather.domain.model

data class CityWeather(
    val current: CurrentWeather,
    val daily: DailyForecast,
    val hourly: List<HourlyForecast>
)
