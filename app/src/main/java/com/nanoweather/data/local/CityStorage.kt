package com.nanoweather.data.local

import com.nanoweather.domain.model.City

interface CityStorage {
    suspend fun loadCities(): List<City>
    suspend fun saveCities(cities: List<City>)
}
