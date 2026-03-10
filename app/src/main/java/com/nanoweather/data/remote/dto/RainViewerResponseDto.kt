package com.nanoweather.data.remote.dto

data class RainViewerResponseDto(
    val host: String,
    val radar: RadarDto
)

data class RadarDto(
    val past: List<RadarFrameDto>,
    val nowcast: List<RadarFrameDto>
)

data class RadarFrameDto(
    val time: Long,
    val path: String
)
