package com.nanoweather.data.repository

import com.nanoweather.data.remote.api.RainViewerApiService
import com.nanoweather.domain.model.RadarMapData
import kotlin.math.cos
import kotlin.math.floor
import kotlin.math.ln
import kotlin.math.pow
import kotlin.math.tan

class RadarRepositoryImpl(
    private val api: RainViewerApiService
) : RadarRepository {

    override suspend fun getRadarMapData(latitude: Double, longitude: Double): Result<RadarMapData> =
        runCatching {
            val response = api.getWeatherMaps()
            val latestFrame = response.radar.past.lastOrNull()
                ?: throw IllegalStateException("No radar data available")

            val zoom = 7
            val n = 2.0.pow(zoom)

            val exactX = (longitude + 180.0) / 360.0 * n
            val latRad = Math.toRadians(latitude)
            val exactY = (1.0 - ln(tan(latRad) + 1.0 / cos(latRad)) / Math.PI) / 2.0 * n

            val tileX = floor(exactX).toInt()
            val tileY = floor(exactY).toInt()
            val offsetXFraction = (exactX - tileX).toFloat()
            val offsetYFraction = (exactY - tileY).toFloat()

            val baseMapUrlTemplate = "https://basemaps.cartocdn.com/light_all/$zoom/{x}/{y}.png"
            val radarUrlTemplate = "${response.host}${latestFrame.path}/256/$zoom/{x}/{y}/2/1_1.png"

            RadarMapData(
                zoom = zoom,
                centerTileX = tileX,
                centerTileY = tileY,
                offsetXFraction = offsetXFraction,
                offsetYFraction = offsetYFraction,
                baseMapUrlTemplate = baseMapUrlTemplate,
                radarUrlTemplate = radarUrlTemplate
            )
        }
}
