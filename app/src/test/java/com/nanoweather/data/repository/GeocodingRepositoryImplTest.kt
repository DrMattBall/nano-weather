package com.nanoweather.data.repository

import com.nanoweather.data.remote.api.GeocodingApiService
import com.nanoweather.data.remote.dto.GeocodingResponseDto
import com.nanoweather.data.remote.dto.GeocodingResultDto
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class GeocodingRepositoryImplTest {

    @Test
    fun `searchCities maps dto to domain model`() = runTest {
        val fakeApi = object : GeocodingApiService {
            override suspend fun searchCities(
                name: String, count: Int, language: String, format: String
            ) = GeocodingResponseDto(
                results = listOf(
                    GeocodingResultDto(
                        id = 1,
                        name = "London",
                        latitude = 51.5,
                        longitude = -0.1,
                        country = "United Kingdom",
                        admin1 = "England"
                    )
                )
            )
        }
        val repo = GeocodingRepositoryImpl(fakeApi)

        val result = repo.searchCities("London")

        assertTrue(result.isSuccess)
        val cities = result.getOrThrow()
        assertEquals(1, cities.size)
        assertEquals("London", cities[0].name)
        assertEquals("England", cities[0].admin1)
        assertEquals(51.5, cities[0].latitude, 0.001)
    }

    @Test
    fun `searchCities returns empty list when results is null`() = runTest {
        val fakeApi = object : GeocodingApiService {
            override suspend fun searchCities(
                name: String, count: Int, language: String, format: String
            ) = GeocodingResponseDto(results = null)
        }
        val repo = GeocodingRepositoryImpl(fakeApi)

        val result = repo.searchCities("xyz")

        assertTrue(result.isSuccess)
        assertTrue(result.getOrThrow().isEmpty())
    }

    @Test
    fun `searchCities returns failure on exception`() = runTest {
        val fakeApi = object : GeocodingApiService {
            override suspend fun searchCities(
                name: String, count: Int, language: String, format: String
            ): GeocodingResponseDto = throw RuntimeException("Network error")
        }
        val repo = GeocodingRepositoryImpl(fakeApi)

        val result = repo.searchCities("London")

        assertTrue(result.isFailure)
    }
}
