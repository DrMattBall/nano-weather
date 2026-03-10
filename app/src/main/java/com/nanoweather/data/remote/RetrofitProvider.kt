package com.nanoweather.data.remote

import com.nanoweather.data.remote.api.AirQualityApiService
import com.nanoweather.data.remote.api.GeocodingApiService
import com.nanoweather.data.remote.api.RainViewerApiService
import com.nanoweather.data.remote.api.WeatherApiService
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

class RetrofitProvider {

    private val okHttpClient = OkHttpClient.Builder()
        .connectTimeout(15, TimeUnit.SECONDS)
        .readTimeout(15, TimeUnit.SECONDS)
        .build()

    private val geocodingRetrofit = Retrofit.Builder()
        .baseUrl("https://geocoding-api.open-meteo.com/")
        .client(okHttpClient)
        .addConverterFactory(GsonConverterFactory.create())
        .build()

    private val weatherRetrofit = Retrofit.Builder()
        .baseUrl("https://api.open-meteo.com/")
        .client(okHttpClient)
        .addConverterFactory(GsonConverterFactory.create())
        .build()

    fun geocodingApi(): GeocodingApiService =
        geocodingRetrofit.create(GeocodingApiService::class.java)

    fun weatherApi(): WeatherApiService =
        weatherRetrofit.create(WeatherApiService::class.java)

    private val rainViewerRetrofit = Retrofit.Builder()
        .baseUrl("https://api.rainviewer.com/")
        .client(okHttpClient)
        .addConverterFactory(GsonConverterFactory.create())
        .build()

    fun rainViewerApi(): RainViewerApiService =
        rainViewerRetrofit.create(RainViewerApiService::class.java)

    private val airQualityRetrofit = Retrofit.Builder()
        .baseUrl("https://air-quality-api.open-meteo.com/")
        .client(okHttpClient)
        .addConverterFactory(GsonConverterFactory.create())
        .build()

    fun airQualityApi(): AirQualityApiService =
        airQualityRetrofit.create(AirQualityApiService::class.java)
}
