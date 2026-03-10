package com.nanoweather.ui.components

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AcUnit
import androidx.compose.material.icons.filled.Cloud
import androidx.compose.material.icons.filled.FilterDrama
import androidx.compose.material.icons.filled.Grain
import androidx.compose.material.icons.filled.Thunderstorm
import androidx.compose.material.icons.filled.WaterDrop
import androidx.compose.material.icons.filled.WbCloudy
import androidx.compose.material.icons.filled.WbSunny
import androidx.compose.ui.graphics.vector.ImageVector

data class WeatherIcon(
    val icon: ImageVector,
    val description: String
)

fun weatherIcon(code: Int): WeatherIcon = when (code) {
    0 -> WeatherIcon(Icons.Default.WbSunny, "Clear sky")
    1 -> WeatherIcon(Icons.Default.WbSunny, "Mainly clear")
    2 -> WeatherIcon(Icons.Default.FilterDrama, "Partly cloudy")
    3 -> WeatherIcon(Icons.Default.Cloud, "Overcast")
    45, 48 -> WeatherIcon(Icons.Default.Cloud, "Fog")
    51, 53, 55 -> WeatherIcon(Icons.Default.Grain, "Drizzle")
    56, 57 -> WeatherIcon(Icons.Default.Grain, "Freezing drizzle")
    61, 63, 65 -> WeatherIcon(Icons.Default.WaterDrop, "Rain")
    66, 67 -> WeatherIcon(Icons.Default.WaterDrop, "Freezing rain")
    71, 73, 75, 77 -> WeatherIcon(Icons.Default.AcUnit, "Snow")
    80, 81, 82 -> WeatherIcon(Icons.Default.WaterDrop, "Rain showers")
    85, 86 -> WeatherIcon(Icons.Default.AcUnit, "Snow showers")
    95 -> WeatherIcon(Icons.Default.Thunderstorm, "Thunderstorm")
    96, 99 -> WeatherIcon(Icons.Default.Thunderstorm, "Thunderstorm with hail")
    else -> WeatherIcon(Icons.Default.WbCloudy, "Unknown")
}
