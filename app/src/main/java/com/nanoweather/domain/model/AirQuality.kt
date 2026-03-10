package com.nanoweather.domain.model

data class AirQuality(
    val usAqi: Int,
    val pm25: Double,
    val pm10: Double,
    val grassPollen: Double,
    val birchPollen: Double,
    val ragweedPollen: Double
) {
    val aqiLabel: String get() = when {
        usAqi <= 50 -> "Good"
        usAqi <= 100 -> "Moderate"
        usAqi <= 150 -> "Unhealthy (SG)"
        usAqi <= 200 -> "Unhealthy"
        usAqi <= 300 -> "Very Unhealthy"
        else -> "Hazardous"
    }

    val pollenLevel: String get() {
        val max = maxOf(grassPollen, birchPollen, ragweedPollen)
        return when {
            max < 10 -> "Low"
            max < 50 -> "Moderate"
            max < 100 -> "High"
            else -> "Very High"
        }
    }
}
