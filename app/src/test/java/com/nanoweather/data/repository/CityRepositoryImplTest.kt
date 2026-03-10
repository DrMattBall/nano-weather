package com.nanoweather.data.repository

import com.nanoweather.data.local.CityStorage
import com.nanoweather.domain.model.City
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class CityRepositoryImplTest {

    private class FakeCityStorage : CityStorage {
        var cities = mutableListOf<City>()

        override suspend fun loadCities(): List<City> = cities.toList()
        override suspend fun saveCities(cities: List<City>) {
            this.cities = cities.toMutableList()
        }
    }

    private val london = City(1, "London", 51.5, -0.1, "UK", "England")
    private val paris = City(2, "Paris", 48.9, 2.3, "France", "Ile-de-France")

    @Test
    fun `getSavedCities returns stored cities`() = runTest {
        val storage = FakeCityStorage().apply { cities.add(london) }
        val repo = CityRepositoryImpl(storage)

        val result = repo.getSavedCities()

        assertEquals(1, result.size)
        assertEquals("London", result[0].name)
    }

    @Test
    fun `addCity persists new city`() = runTest {
        val storage = FakeCityStorage()
        val repo = CityRepositoryImpl(storage)

        repo.addCity(london)

        assertEquals(1, storage.cities.size)
        assertEquals("London", storage.cities[0].name)
    }

    @Test
    fun `addCity prevents duplicates`() = runTest {
        val storage = FakeCityStorage()
        val repo = CityRepositoryImpl(storage)

        repo.addCity(london)
        repo.addCity(london)

        assertEquals(1, storage.cities.size)
    }

    @Test
    fun `removeCity removes and persists`() = runTest {
        val storage = FakeCityStorage().apply {
            cities.addAll(listOf(london, paris))
        }
        val repo = CityRepositoryImpl(storage)
        repo.getSavedCities() // trigger load

        repo.removeCity(1)

        assertEquals(1, storage.cities.size)
        assertEquals("Paris", storage.cities[0].name)
    }

    @Test
    fun `removeCity with unknown id does nothing`() = runTest {
        val storage = FakeCityStorage().apply { cities.add(london) }
        val repo = CityRepositoryImpl(storage)
        repo.getSavedCities()

        repo.removeCity(999)

        assertEquals(1, storage.cities.size)
    }
}
