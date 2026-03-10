package com.nanoweather.ui

import com.nanoweather.domain.model.AirQuality
import com.nanoweather.domain.model.City
import com.nanoweather.domain.model.DailyForecast
import com.nanoweather.domain.model.HourlyForecast
import com.nanoweather.domain.model.RadarMapData

data class CityWeatherState(
    val city: City,
    val currentTemp: Double? = null,
    val highTemp: Double? = null,
    val lowTemp: Double? = null,
    val hourlyForecasts: List<HourlyForecast> = emptyList(),
    val dailyForecasts: List<DailyForecast> = emptyList(),
    val currentWeatherCode: Int? = null,
    val currentUvIndex: Double? = null,
    val maxUvIndex: Double? = null,
    val isExpanded: Boolean = false,
    val radarMapData: RadarMapData? = null,
    val airQuality: AirQuality? = null,
    val isLoading: Boolean = true,
    val error: String? = null,
    val isSelected: Boolean = false
)

enum class TemperatureUnit { CELSIUS, FAHRENHEIT }

data class MainUiState(
    val searchQuery: String = "",
    val searchResults: List<City> = emptyList(),
    val nearbyCities: List<City> = emptyList(),
    val isSearching: Boolean = false,
    val cities: List<CityWeatherState> = emptyList(),
    val isSelectionMode: Boolean = false,
    val temperatureUnit: TemperatureUnit = TemperatureUnit.CELSIUS,
    val isSearchVisible: Boolean = false,
    val contrastBubbles: Boolean = false
)
