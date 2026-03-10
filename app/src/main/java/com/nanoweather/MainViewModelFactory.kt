package com.nanoweather

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.nanoweather.data.location.LocationProvider
import com.nanoweather.data.repository.CityRepository
import com.nanoweather.data.repository.GeocodingRepository
import com.nanoweather.data.repository.SettingsRepository
import com.nanoweather.data.repository.WeatherRepository
import com.nanoweather.ui.MainViewModel

class MainViewModelFactory(
    private val geocodingRepository: GeocodingRepository,
    private val weatherRepository: WeatherRepository,
    private val cityRepository: CityRepository,
    private val settingsRepository: SettingsRepository,
    private val locationProvider: LocationProvider?
) : ViewModelProvider.Factory {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return MainViewModel(geocodingRepository, weatherRepository, cityRepository, settingsRepository, locationProvider) as T
    }
}
