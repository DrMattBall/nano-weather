package com.nanoweather.data.repository

import com.nanoweather.data.local.CityStorage
import com.nanoweather.domain.model.City

class CityRepositoryImpl(
    private val storage: CityStorage
) : CityRepository {

    private var cities: MutableList<City> = mutableListOf()
    private var loaded = false

    override suspend fun getSavedCities(): List<City> {
        if (!loaded) {
            cities = storage.loadCities().toMutableList()
            loaded = true
        }
        return cities.toList()
    }

    override suspend fun addCity(city: City) {
        if (!loaded) getSavedCities()
        if (cities.none { it.id == city.id }) {
            cities.add(city)
            storage.saveCities(cities.toList())
        }
    }

    override suspend fun removeCity(cityId: Int) {
        if (!loaded) getSavedCities()
        cities.removeAll { it.id == cityId }
        storage.saveCities(cities.toList())
    }
}
