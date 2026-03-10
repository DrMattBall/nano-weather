package com.nanoweather.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.nanoweather.domain.model.HourlyForecast
import kotlin.math.roundToInt

@Composable
fun HourlyTimeline(
    forecasts: List<HourlyForecast>,
    modifier: Modifier = Modifier
) {
    LazyRow(
        modifier = modifier,
        contentPadding = PaddingValues(horizontal = 4.dp),
        horizontalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        items(forecasts) { forecast ->
            HourlyItem(forecast)
        }
    }
}

@Composable
private fun HourlyItem(forecast: HourlyForecast) {
    Column(
        modifier = Modifier
            .width(56.dp)
            .padding(vertical = 4.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = formatHour(forecast.time),
            style = MaterialTheme.typography.labelSmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Text(
            text = "${forecast.temperature.roundToInt()}°",
            style = MaterialTheme.typography.bodyMedium,
            modifier = Modifier.padding(vertical = 2.dp)
        )
        if (forecast.precipitation > 0) {
            Text(
                text = "${forecast.precipitation} mm",
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.primary,
                textAlign = TextAlign.Center
            )
        }
    }
}

private fun formatHour(isoTime: String): String {
    // Format: "2024-01-15T14:00" -> "2 PM"
    val hour = isoTime.substringAfter("T").substringBefore(":").toIntOrNull() ?: return isoTime
    return when {
        hour == 0 -> "12 AM"
        hour < 12 -> "$hour AM"
        hour == 12 -> "12 PM"
        else -> "${hour - 12} PM"
    }
}
