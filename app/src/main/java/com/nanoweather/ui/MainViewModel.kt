package com.nanoweather.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.nanoweather.data.location.LocationProvider
import com.nanoweather.data.repository.CityRepository
import com.nanoweather.data.repository.GeocodingRepository
import com.nanoweather.data.repository.SettingsRepository
import com.nanoweather.data.repository.WeatherRepository
import com.nanoweather.domain.model.City
import kotlinx.coroutines.Job
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class MainViewModel(
    private val geocodingRepository: GeocodingRepository,
    private val weatherRepository: WeatherRepository,
    private val cityRepository: CityRepository,
    private val settingsRepository: SettingsRepository,
    private var locationProvider: LocationProvider? = null
) : ViewModel() {

    private val _uiState = MutableStateFlow(MainUiState())
    val uiState: StateFlow<MainUiState> = _uiState.asStateFlow()

    private var searchJob: Job? = null

    init {
        _uiState.update { it.copy(contrastBubbles = settingsRepository.getContrastBubbles()) }
        loadSavedCities()
    }

    private fun loadSavedCities() {
        viewModelScope.launch {
            val cities = cityRepository.getSavedCities()
            _uiState.update { state ->
                state.copy(
                    cities = cities.map { CityWeatherState(city = it) }
                )
            }
            refreshAllWeather()
        }
    }

    fun onSearchQueryChanged(query: String) {
        _uiState.update { it.copy(searchQuery = query) }

        searchJob?.cancel()
        if (query.length < 2) {
            _uiState.update { it.copy(searchResults = emptyList(), isSearching = false) }
            return
        }

        searchJob = viewModelScope.launch {
            delay(300)
            _uiState.update { it.copy(isSearching = true) }
            geocodingRepository.searchCities(query)
                .onSuccess { results ->
                    _uiState.update { it.copy(searchResults = results, isSearching = false) }
                }
                .onFailure {
                    _uiState.update { it.copy(searchResults = emptyList(), isSearching = false) }
                }
        }
    }

    fun onLocationPermissionGranted(provider: LocationProvider) {
        locationProvider = provider
    }

    fun onShowSearch() {
        _uiState.update { it.copy(isSearchVisible = true) }
        fetchNearbyCities()
    }

    private fun fetchNearbyCities() {
        val provider = locationProvider ?: return
        viewModelScope.launch {
            val location = provider.getLastLocation() ?: return@launch
            geocodingRepository.nearbyCities(location.latitude, location.longitude)
                .onSuccess { cities ->
                    _uiState.update { it.copy(nearbyCities = cities) }
                }
        }
    }

    fun onDismissSearch() {
        searchJob?.cancel()
        _uiState.update {
            it.copy(isSearchVisible = false, searchQuery = "", searchResults = emptyList(), nearbyCities = emptyList())
        }
    }

    fun onCitySelected(city: City) {
        viewModelScope.launch {
            cityRepository.addCity(city)
            _uiState.update { state ->
                val alreadyExists = state.cities.any { it.city.id == city.id }
                if (alreadyExists) {
                    state.copy(
                        searchQuery = "", searchResults = emptyList(),
                        isSearchVisible = false
                    )
                } else {
                    state.copy(
                        searchQuery = "",
                        searchResults = emptyList(),
                        isSearchVisible = false,
                        cities = state.cities + CityWeatherState(city = city)
                    )
                }
            }
            fetchWeatherForCity(city)
        }
    }

    fun onToggleTemperatureUnit() {
        _uiState.update { state ->
            state.copy(
                temperatureUnit = when (state.temperatureUnit) {
                    TemperatureUnit.CELSIUS -> TemperatureUnit.FAHRENHEIT
                    TemperatureUnit.FAHRENHEIT -> TemperatureUnit.CELSIUS
                }
            )
        }
    }

    fun onToggleContrastBubbles() {
        val newValue = !_uiState.value.contrastBubbles
        settingsRepository.setContrastBubbles(newValue)
        _uiState.update { it.copy(contrastBubbles = newValue) }
    }

    fun onEnterSelectionMode() {
        _uiState.update { it.copy(isSelectionMode = true) }
    }

    fun onExitSelectionMode() {
        _uiState.update { state ->
            state.copy(
                isSelectionMode = false,
                cities = state.cities.map { it.copy(isSelected = false) }
            )
        }
    }

    fun onToggleSelection(cityId: Int) {
        _uiState.update { state ->
            state.copy(
                cities = state.cities.map { cityState ->
                    if (cityState.city.id == cityId) {
                        cityState.copy(isSelected = !cityState.isSelected)
                    } else {
                        cityState
                    }
                }
            )
        }
    }

    fun onRemoveSelected() {
        viewModelScope.launch {
            val selectedIds = _uiState.value.cities
                .filter { it.isSelected }
                .map { it.city.id }
            selectedIds.forEach { cityRepository.removeCity(it) }
            _uiState.update { state ->
                state.copy(
                    isSelectionMode = false,
                    cities = state.cities.filter { it.city.id !in selectedIds }
                )
            }
        }
    }

    fun onCityToggled(cityId: Int) {
        _uiState.update { state ->
            state.copy(
                cities = state.cities.map { cityState ->
                    if (cityState.city.id == cityId) {
                        cityState.copy(isExpanded = !cityState.isExpanded)
                    } else {
                        cityState
                    }
                }
            )
        }
    }

    private suspend fun refreshAllWeather() {
        val cities = _uiState.value.cities.map { it.city }
        val deferreds = cities.map { city ->
            viewModelScope.async { fetchWeatherForCity(city) }
        }
        deferreds.awaitAll()
    }

    private suspend fun fetchWeatherForCity(city: City) {
        weatherRepository.getWeather(city.latitude, city.longitude)
            .onSuccess { weather ->
                _uiState.update { state ->
                    state.copy(
                        cities = state.cities.map { cityState ->
                            if (cityState.city.id == city.id) {
                                val today = weather.dailyForecasts.first()
                                cityState.copy(
                                    currentTemp = weather.current.temperature,
                                    currentWeatherCode = weather.current.weatherCode,
                                    highTemp = today.highTemp,
                                    lowTemp = today.lowTemp,
                                    hourlyForecasts = weather.hourly,
                                    dailyForecasts = weather.dailyForecasts,
                                    currentUvIndex = weather.currentUvIndex,
                                    maxUvIndex = today.uvIndexMax,
                                    isLoading = false,
                                    error = null
                                )
                            } else {
                                cityState
                            }
                        }
                    )
                }
            }
            .onFailure { e ->
                _uiState.update { state ->
                    state.copy(
                        cities = state.cities.map { cityState ->
                            if (cityState.city.id == city.id) {
                                cityState.copy(
                                    isLoading = false,
                                    error = e.message ?: "Failed to load weather"
                                )
                            } else {
                                cityState
                            }
                        }
                    )
                }
            }
    }
}
