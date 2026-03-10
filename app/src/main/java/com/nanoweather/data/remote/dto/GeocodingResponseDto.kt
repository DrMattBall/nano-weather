package com.nanoweather.data.remote.dto

data class GeocodingResponseDto(
    val results: List<GeocodingResultDto>?
)

data class GeocodingResultDto(
    val id: Int,
    val name: String,
    val latitude: Double,
    val longitude: Double,
    val country: String?,
    val admin1: String?
)
