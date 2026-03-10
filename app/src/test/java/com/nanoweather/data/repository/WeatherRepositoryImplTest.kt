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
import java.time.LocalDateTime

class WeatherRepositoryImplTest {

    private val fixedClock = { LocalDateTime.of(2024, 1, 15, 14, 30) }

    private fun createFakeApi(response: WeatherResponseDto) = object : WeatherApiService {
        override suspend fun getForecast(
            latitude: Double, longitude: Double, currentWeather: Boolean,
            daily: String, hourly: String, forecastDays: Int, timezone: String
        ) = response
    }

    private val sampleHourlyTimes = (0..23).map { "2024-01-15T%02d:00".format(it) } +
        (0..23).map { "2024-01-16T%02d:00".format(it) }
    private val sampleHourlyTemps = (0..47).map { 20.0 - it * 0.1 }
    private val sampleHourlyPrecip = (0..47).map { if (it == 15) 0.5 else 0.0 }
    private val sampleHourlyUv = (0..47).map { if (it == 14) 5.2 else 1.0 }

    private val sampleResponse = WeatherResponseDto(
        currentWeather = CurrentWeatherDto(temperature = 22.5, weatherCode = 0),
        daily = DailyDto(
            time = listOf("2024-01-15", "2024-01-16"),
            temperatureMax = listOf(25.0, 24.0),
            temperatureMin = listOf(18.0, 17.0),
            uvIndexMax = listOf(7.5, 6.0)
        ),
        hourly = HourlyDto(
            time = sampleHourlyTimes,
            temperature = sampleHourlyTemps,
            precipitation = sampleHourlyPrecip,
            uvIndex = sampleHourlyUv
        )
    )

    @Test
    fun `getWeather maps current weather correctly`() = runTest {
        val repo = WeatherRepositoryImpl(createFakeApi(sampleResponse), fixedClock)

        val result = repo.getWeather(51.5, -0.1)

        assertTrue(result.isSuccess)
        val weather = result.getOrThrow()
        assertEquals(22.5, weather.current.temperature, 0.001)
        assertEquals(0, weather.current.weatherCode)
    }

    @Test
    fun `getWeather maps daily forecast correctly`() = runTest {
        val repo = WeatherRepositoryImpl(createFakeApi(sampleResponse), fixedClock)

        val result = repo.getWeather(51.5, -0.1)

        val weather = result.getOrThrow()
        assertEquals(25.0, weather.daily.highTemp, 0.001)
        assertEquals(18.0, weather.daily.lowTemp, 0.001)
    }

    @Test
    fun `getWeather filters hourly from current hour`() = runTest {
        val repo = WeatherRepositoryImpl(createFakeApi(sampleResponse), fixedClock)

        val result = repo.getWeather(51.5, -0.1)

        val weather = result.getOrThrow()
        assertEquals(24, weather.hourly.size)
        assertEquals("2024-01-15T14:00", weather.hourly.first().time)
        assertEquals("2024-01-16T13:00", weather.hourly.last().time)
    }

    @Test
    fun `getWeather includes precipitation in filtered results`() = runTest {
        val repo = WeatherRepositoryImpl(createFakeApi(sampleResponse), fixedClock)

        val result = repo.getWeather(51.5, -0.1)

        val weather = result.getOrThrow()
        val precipEntry = weather.hourly.find { it.time == "2024-01-15T15:00" }
        assertEquals(0.5, precipEntry!!.precipitation, 0.001)
    }

    @Test
    fun `getWeather maps UV index correctly`() = runTest {
        val repo = WeatherRepositoryImpl(createFakeApi(sampleResponse), fixedClock)

        val result = repo.getWeather(51.5, -0.1)

        val weather = result.getOrThrow()
        assertEquals(5.2, weather.currentUvIndex, 0.001)
        assertEquals(7.5, weather.daily.uvIndexMax, 0.001)
    }
}
