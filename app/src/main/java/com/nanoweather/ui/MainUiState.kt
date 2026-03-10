package com.nanoweather.ui

import com.nanoweather.domain.model.City
import com.nanoweather.domain.model.HourlyForecast

data class CityWeatherState(
    val city: City,
    val currentTemp: Double? = null,
    val highTemp: Double? = null,
    val lowTemp: Double? = null,
    val hourlyForecasts: List<HourlyForecast> = emptyList(),
    val isExpanded: Boolean = false,
    val isLoading: Boolean = true,
    val error: String? = null,
    val isSelected: Boolean = false
)

data class MainUiState(
    val searchQuery: String = "",
    val searchResults: List<City> = emptyList(),
    val isSearching: Boolean = false,
    val cities: List<CityWeatherState> = emptyList(),
    val isSelectionMode: Boolean = false
)
