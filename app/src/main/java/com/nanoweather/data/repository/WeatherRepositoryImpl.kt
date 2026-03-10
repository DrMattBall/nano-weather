package com.nanoweather.data.repository

import com.nanoweather.data.remote.api.WeatherApiService
import com.nanoweather.domain.model.CityWeather
import com.nanoweather.domain.model.CurrentWeather
import com.nanoweather.domain.model.DailyForecast
import com.nanoweather.domain.model.HourlyForecast
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

class WeatherRepositoryImpl(
    private val api: WeatherApiService,
    private val clock: () -> LocalDateTime = { LocalDateTime.now() }
) : WeatherRepository {

    override suspend fun getWeather(latitude: Double, longitude: Double): Result<CityWeather> =
        runCatching {
            val response = api.getForecast(latitude = latitude, longitude = longitude)

            val current = CurrentWeather(
                temperature = response.currentWeather.temperature,
                weatherCode = response.currentWeather.weatherCode
            )

            val dailyForecasts = response.daily.time.mapIndexed { index, date ->
                DailyForecast(
                    date = date,
                    highTemp = response.daily.temperatureMax[index],
                    lowTemp = response.daily.temperatureMin[index],
                    uvIndexMax = response.daily.uvIndexMax[index],
                    precipitationProbability = response.daily.precipitationProbabilityMax[index],
                    weatherCode = response.daily.weatherCode[index]
                )
            }

            val allHourly = response.hourly.time.mapIndexed { index, time ->
                HourlyForecast(
                    time = time,
                    temperature = response.hourly.temperature[index],
                    precipitation = response.hourly.precipitation[index]
                )
            }

            val now = clock()
            val currentHour = now.withMinute(0).withSecond(0).withNano(0)
            val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm")
            val cutoff = currentHour.format(formatter)

            val currentHourIndex = response.hourly.time.indexOf(cutoff)
            val currentUvIndex = if (currentHourIndex >= 0) {
                response.hourly.uvIndex[currentHourIndex]
            } else {
                0.0
            }

            val hourly = allHourly
                .filter { it.time >= cutoff }
                .take(24)

            CityWeather(
                current = current,
                dailyForecasts = dailyForecasts,
                hourly = hourly,
                currentUvIndex = currentUvIndex
            )
        }
}
