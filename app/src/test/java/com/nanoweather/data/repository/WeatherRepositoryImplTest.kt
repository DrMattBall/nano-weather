package com.nanoweather.data.repository

import com.nanoweather.data.remote.api.WeatherApiService
import com.nanoweather.data.remote.dto.CurrentWeatherDto
import com.nanoweather.data.remote.dto.DailyDto
import com.nanoweather.data.remote.dto.HourlyDto
import com.nanoweather.data.remote.dto.WeatherResponseDto
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class WeatherRepositoryImplTest {

    private fun createFakeApi(response: WeatherResponseDto) = object : WeatherApiService {
        override suspend fun getForecast(
            latitude: Double, longitude: Double, currentWeather: Boolean,
            daily: String, hourly: String, forecastDays: Int, timezone: String
        ) = response
    }

    private val sampleResponse = WeatherResponseDto(
        currentWeather = CurrentWeatherDto(temperature = 22.5, weatherCode = 0),
        daily = DailyDto(
            time = listOf("2024-01-15"),
            temperatureMax = listOf(25.0),
            temperatureMin = listOf(18.0)
        ),
        hourly = HourlyDto(
            time = listOf("2024-01-15T00:00", "2024-01-15T01:00"),
            temperature = listOf(20.0, 19.5),
            precipitation = listOf(0.0, 0.5)
        )
    )

    @Test
    fun `getWeather maps current weather correctly`() = runTest {
        val repo = WeatherRepositoryImpl(createFakeApi(sampleResponse))

        val result = repo.getWeather(51.5, -0.1)

        assertTrue(result.isSuccess)
        val weather = result.getOrThrow()
        assertEquals(22.5, weather.current.temperature, 0.001)
        assertEquals(0, weather.current.weatherCode)
    }

    @Test
    fun `getWeather maps daily forecast correctly`() = runTest {
        val repo = WeatherRepositoryImpl(createFakeApi(sampleResponse))

        val result = repo.getWeather(51.5, -0.1)

        val weather = result.getOrThrow()
        assertEquals(25.0, weather.daily.highTemp, 0.001)
        assertEquals(18.0, weather.daily.lowTemp, 0.001)
    }

    @Test
    fun `getWeather maps hourly forecasts correctly`() = runTest {
        val repo = WeatherRepositoryImpl(createFakeApi(sampleResponse))

        val result = repo.getWeather(51.5, -0.1)

        val weather = result.getOrThrow()
        assertEquals(2, weather.hourly.size)
        assertEquals(0.5, weather.hourly[1].precipitation, 0.001)
    }
}
