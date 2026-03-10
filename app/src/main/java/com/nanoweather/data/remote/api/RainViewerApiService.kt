package com.nanoweather.data.remote.api

import com.nanoweather.data.remote.dto.RainViewerResponseDto
import retrofit2.http.GET

interface RainViewerApiService {
    @GET("public/weather-maps.json")
    suspend fun getWeatherMaps(): RainViewerResponseDto
}
