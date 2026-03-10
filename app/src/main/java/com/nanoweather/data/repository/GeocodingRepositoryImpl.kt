package com.nanoweather.data.repository

import com.nanoweather.data.remote.api.GeocodingApiService
import com.nanoweather.domain.model.City

class GeocodingRepositoryImpl(
    private val api: GeocodingApiService
) : GeocodingRepository {

    override suspend fun searchCities(query: String): Result<List<City>> = runCatching {
        val response = api.searchCities(name = query)
        response.results?.map { dto ->
            City(
                id = dto.id,
                name = dto.name,
                latitude = dto.latitude,
                longitude = dto.longitude,
                country = dto.country ?: "",
                admin1 = dto.admin1
            )
        } ?: emptyList()
    }
}
