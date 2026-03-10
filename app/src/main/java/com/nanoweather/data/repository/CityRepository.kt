package com.nanoweather.data.repository

import com.nanoweather.domain.model.City

interface CityRepository {
    suspend fun getSavedCities(): List<City>
    suspend fun addCity(city: City)
    suspend fun removeCity(cityId: Int)
}
