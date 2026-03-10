package com.nanoweather

import android.app.Application
import android.content.Context
import com.nanoweather.data.local.SharedPrefsCityStorage
import com.nanoweather.data.location.AndroidReverseGeocoder
import com.nanoweather.data.location.FusedLocationProvider
import com.nanoweather.data.location.LocationProvider
import com.nanoweather.data.remote.RetrofitProvider
import com.nanoweather.data.repository.CityRepository
import com.nanoweather.data.repository.CityRepositoryImpl
import com.nanoweather.data.repository.GeocodingRepository
import com.nanoweather.data.repository.GeocodingRepositoryImpl
import com.nanoweather.data.repository.WeatherRepository
import com.nanoweather.data.repository.WeatherRepositoryImpl

class NanoWeatherApp : Application() {

    lateinit var geocodingRepository: GeocodingRepository
        private set
    lateinit var weatherRepository: WeatherRepository
        private set
    lateinit var cityRepository: CityRepository
        private set
    lateinit var locationProvider: LocationProvider
        private set

    override fun onCreate() {
        super.onCreate()

        val retrofitProvider = RetrofitProvider()
        val prefs = getSharedPreferences("nano_weather", Context.MODE_PRIVATE)
        val reverseGeocoder = AndroidReverseGeocoder(this)

        geocodingRepository = GeocodingRepositoryImpl(retrofitProvider.geocodingApi(), reverseGeocoder)
        weatherRepository = WeatherRepositoryImpl(retrofitProvider.weatherApi())
        cityRepository = CityRepositoryImpl(SharedPrefsCityStorage(prefs))
        locationProvider = FusedLocationProvider(this)
    }
}
