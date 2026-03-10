package com.nanoweather.data.repository

import com.nanoweather.domain.model.RadarMapData

interface RadarRepository {
    suspend fun getRadarMapData(latitude: Double, longitude: Double): Result<RadarMapData>
}
