package com.nanoweather.data.repository

import com.nanoweather.data.remote.api.WeatherApiService
import com.nanoweather.domain.model.CityWeather
import com.nanoweather.domain.model.CurrentWeather
import com.nanoweather.domain.model.DailyForecast
import com.nanoweather.domain.model.HourlyForecast

class WeatherRepositoryImpl(
    private val api: WeatherApiService
) : WeatherRepository {

    override suspend fun getWeather(latitude: Double, longitude: Double): Result<CityWeather> =
        runCatching {
            val response = api.getForecast(latitude = latitude, longitude = longitude)

            val current = CurrentWeather(
                temperature = response.currentWeather.temperature,
                weatherCode = response.currentWeather.weatherCode
            )

            val daily = DailyForecast(
                highTemp = response.daily.temperatureMax.first(),
                lowTemp = response.daily.temperatureMin.first()
            )

            val hourly = response.hourly.time.mapIndexed { index, time ->
                HourlyForecast(
                    time = time,
                    temperature = response.hourly.temperature[index],
                    precipitation = response.hourly.precipitation[index]
                )
            }

            CityWeather(current = current, daily = daily, hourly = hourly)
        }
}
