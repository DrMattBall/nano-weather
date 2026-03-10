package com.nanoweather.domain.model

data class RadarMapData(
    val zoom: Int,
    val centerTileX: Int,
    val centerTileY: Int,
    val offsetXFraction: Float,
    val offsetYFraction: Float,
    val baseMapUrlTemplate: String,
    val radarUrlTemplate: String
)
