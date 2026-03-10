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
        val geocoder = reverseGeocoder
            ?: throw IllegalStateException("Reverse geocoder unavailable")
        val localityNames = geocoder.getNearbyLocalityNames(latitude, longitude)
        if (localityNames.isEmpty()) throw IllegalStateException("No nearby localities found")

        val seenIds = mutableSetOf<Int>()
        val allCities = mutableListOf<City>()
        for (name in localityNames) {
            val response = api.searchCities(name = name, count = 3)
            val cities = response.results?.map { it.toCity() } ?: emptyList()
            for (city in cities) {
                if (seenIds.add(city.id)) {
                    allCities.add(city)
                }
            }
        }
        allCities
            .sortedBy { approxDistance(latitude, longitude, it.latitude, it.longitude) }
            .take(10)
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
