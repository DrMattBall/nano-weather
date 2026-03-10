package com.nanoweather.data.repository

import com.nanoweather.domain.model.CityWeather

interface WeatherRepository {
    suspend fun getWeather(latitude: Double, longitude: Double): Result<CityWeather>
}
