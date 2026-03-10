package com.nanoweather.ui.components

import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.foundation.background
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Card
import androidx.compose.material3.Icon
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.nanoweather.ui.CityWeatherState
import com.nanoweather.ui.TemperatureUnit
import kotlin.math.roundToInt

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun CityRow(
    state: CityWeatherState,
    isSelectionMode: Boolean,
    temperatureUnit: TemperatureUnit,
    contrastBubbles: Boolean,
    backgroundImageResId: Int?,
    onToggle: () -> Unit,
    onLongPress: () -> Unit,
    onSelectionToggle: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .animateContentSize(
                animationSpec = spring(stiffness = Spring.StiffnessMediumLow)
            )
            .combinedClickable(
                onClick = {
                    if (isSelectionMode) onSelectionToggle() else onToggle()
                },
                onLongClick = onLongPress
            ),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceContainerLow
        )
    ) {
        Column {
            Box {
                if (backgroundImageResId != null) {
                    Image(
                        painter = painterResource(id = backgroundImageResId),
                        contentDescription = null,
                        modifier = Modifier
                            .matchParentSize()
                            .alpha(0.2f),
                        contentScale = ContentScale.Crop
                    )
                }

                Column(modifier = Modifier.padding(16.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        if (isSelectionMode) {
                            Checkbox(
                                checked = state.isSelected,
                                onCheckedChange = { onSelectionToggle() }
                            )
                        }

                        Row(
                            modifier = Modifier
                                .bubbleBackground(contrastBubbles),
                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = state.city.name,
                                style = MaterialTheme.typography.titleMedium
                            )
                            if (!state.isLoading && state.error == null && state.currentWeatherCode != null) {
                                val weather = weatherIcon(state.currentWeatherCode)
                                Icon(
                                    imageVector = weather.icon,
                                    contentDescription = weather.description,
                                    modifier = Modifier.size(20.dp)
                                )
                            }
                            if (!state.isLoading && state.error == null) {
                                Text(
                                    text = "${formatTemp(state.currentTemp, temperatureUnit)}°",
                                    style = MaterialTheme.typography.headlineSmall
                                )
                                Column {
                                    Text(
                                        text = "H: ${formatTemp(state.highTemp, temperatureUnit)}°",
                                        style = MaterialTheme.typography.labelMedium,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                    Text(
                                        text = "L: ${formatTemp(state.lowTemp, temperatureUnit)}°",
                                        style = MaterialTheme.typography.labelMedium,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                }
                            }
                        }

                        Spacer(modifier = Modifier.weight(1f))

                        if (state.isLoading) {
                            Text(
                                text = "...",
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        } else if (state.error != null) {
                            Text(
                                text = "Error",
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.error
                            )
                        } else {
                            Column(
                                horizontalAlignment = Alignment.End,
                                modifier = Modifier.bubbleBackground(contrastBubbles)
                            ) {
                                Text(
                                    text = "UV: ${formatUv(state.currentUvIndex)}",
                                    style = MaterialTheme.typography.labelMedium,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                                Text(
                                    text = "Max: ${formatUv(state.maxUvIndex)}",
                                    style = MaterialTheme.typography.labelMedium,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }
                    }

                    if (state.isExpanded && !state.isLoading && state.error == null) {
                        HourlyTimeline(
                            forecasts = state.hourlyForecasts,
                            temperatureUnit = temperatureUnit,
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(top = 12.dp)
                        )
                    }
                }
            }

            if (state.isExpanded && !state.isLoading && state.error == null) {
                if (state.dailyForecasts.isNotEmpty()) {
                    DailyForecastList(
                        forecasts = state.dailyForecasts,
                        temperatureUnit = temperatureUnit,
                        contrastBubbles = contrastBubbles,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(start = 16.dp, end = 16.dp, top = 12.dp)
                    )
                }

                if (state.radarMapData != null || state.airQuality != null) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(start = 16.dp, end = 16.dp, top = 12.dp, bottom = 16.dp),
                        verticalAlignment = Alignment.Top
                    ) {
                        if (state.radarMapData != null) {
                            RadarImage(
                                radarMapData = state.radarMapData,
                                modifier = Modifier.weight(1f)
                            )
                        }
                        if (state.airQuality != null) {
                            AirQualitySummary(
                                airQuality = state.airQuality,
                                modifier = Modifier.weight(1f)
                            )
                        }
                    }
                }
            }
        }
    }
}

private fun Modifier.bubbleBackground(enabled: Boolean): Modifier =
    if (enabled) {
        this
            .background(
                color = Color.White.copy(alpha = 0.75f),
                shape = RoundedCornerShape(12.dp)
            )
            .padding(horizontal = 10.dp, vertical = 6.dp)
    } else {
        this
    }

private fun formatTemp(celsius: Double?, unit: TemperatureUnit): String {
    if (celsius == null) return "--"
    val value = when (unit) {
        TemperatureUnit.CELSIUS -> celsius
        TemperatureUnit.FAHRENHEIT -> celsius * 9.0 / 5.0 + 32.0
    }
    return value.roundToInt().toString()
}

private fun formatUv(value: Double?): String {
    if (value == null) return "--"
    return value.roundToInt().toString()
}
