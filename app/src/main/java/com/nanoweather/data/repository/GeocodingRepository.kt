package com.nanoweather.data.repository

import com.nanoweather.domain.model.City

interface GeocodingRepository {
    suspend fun searchCities(query: String): Result<List<City>>
    suspend fun nearbyCities(latitude: Double, longitude: Double): Result<List<City>>
}
