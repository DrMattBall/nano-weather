package com.nanoweather.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.nanoweather.domain.model.AirQuality

@Composable
fun AirQualitySummary(
    airQuality: AirQuality,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier.padding(start = 24.dp),
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = "Air Quality",
            style = MaterialTheme.typography.labelSmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Text(
            text = "${airQuality.usAqi} AQI",
            style = MaterialTheme.typography.titleMedium
        )
        Text(
            text = airQuality.aqiLabel,
            style = MaterialTheme.typography.labelSmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )

        Text(
            text = "Pollen",
            style = MaterialTheme.typography.labelSmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.padding(top = 8.dp)
        )
        Text(
            text = airQuality.pollenLevel,
            style = MaterialTheme.typography.titleMedium
        )
    }
}
