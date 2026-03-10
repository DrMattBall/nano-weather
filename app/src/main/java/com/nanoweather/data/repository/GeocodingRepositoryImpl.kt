package com.nanoweather.data.repository

import com.nanoweather.data.location.ReverseGeocoder
import com.nanoweather.data.remote.api.GeocodingApiService
import com.nanoweather.domain.model.City
import kotlin.math.cos
import kotlin.math.sqrt

class GeocodingRepositoryImpl(
    private val api: GeocodingApiService,
    private val reverseGeocoder: ReverseGeocoder? = null
) : GeocodingRepository {

    override suspend fun searchCities(query: String): Result<List<City>> = runCatching {
        val response = api.searchCities(name = query)
        response.results?.map { it.toCity() } ?: emptyList()
    }

    override suspend fun nearbyCities(latitude: Double, longitude: Double): Result<List<City>> = runCatching {
        val locality = reverseGeocoder?.getLocalityName(latitude, longitude)
            ?: throw IllegalStateException("Reverse geocoder unavailable")
        val response = api.searchCities(name = locality, count = 10)
        val cities = response.results?.map { it.toCity() } ?: emptyList()
        cities.sortedBy { approxDistance(latitude, longitude, it.latitude, it.longitude) }
    }

    private fun approxDistance(lat1: Double, lon1: Double, lat2: Double, lon2: Double): Double {
        val dLat = lat2 - lat1
        val dLon = (lon2 - lon1) * cos(Math.toRadians((lat1 + lat2) / 2))
        return sqrt(dLat * dLat + dLon * dLon)
    }
}

private fun com.nanoweather.data.remote.dto.GeocodingResultDto.toCity() = City(
    id = id,
    name = name,
    latitude = latitude,
    longitude = longitude,
    country = country ?: "",
    admin1 = admin1
)
