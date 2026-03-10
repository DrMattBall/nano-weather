package com.nanoweather.ui.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.nanoweather.domain.model.HourlyForecast
import kotlin.math.roundToInt

private val ITEM_WIDTH = 56.dp
private val GRAPH_HEIGHT = 40.dp

@Composable
fun HourlyTimeline(
    forecasts: List<HourlyForecast>,
    modifier: Modifier = Modifier
) {
    if (forecasts.isEmpty()) return

    val minTemp = forecasts.minOf { it.temperature }
    val maxTemp = forecasts.maxOf { it.temperature }
    val tempRange = (maxTemp - minTemp).coerceAtLeast(1.0)

    val scrollState = rememberScrollState()
    val lineColor = MaterialTheme.colorScheme.primary
    val precipColor = MaterialTheme.colorScheme.primary
    val textStyleLabel = MaterialTheme.typography.labelSmall
    val textStyleBody = MaterialTheme.typography.bodyMedium
    val onSurfaceVariant = MaterialTheme.colorScheme.onSurfaceVariant

    Box(modifier = modifier.horizontalScroll(scrollState)) {
        Column {
            // Precipitation row
            Row {
                forecasts.forEach { forecast ->
                    Box(
                        modifier = Modifier.width(ITEM_WIDTH),
                        contentAlignment = Alignment.Center
                    ) {
                        if (forecast.precipitation > 0) {
                            Text(
                                text = "${forecast.precipitation} mm",
                                style = textStyleLabel,
                                color = precipColor,
                                textAlign = TextAlign.Center
                            )
                        }
                    }
                }
            }

            // Temperature row
            Row {
                forecasts.forEach { forecast ->
                    Box(
                        modifier = Modifier.width(ITEM_WIDTH),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "${forecast.temperature.roundToInt()}°",
                            style = textStyleBody
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(4.dp))

            // Line graph
            Box(
                modifier = Modifier
                    .width(ITEM_WIDTH * forecasts.size)
                    .height(GRAPH_HEIGHT)
            ) {
                Canvas(
                    modifier = Modifier
                        .width(ITEM_WIDTH * forecasts.size)
                        .height(GRAPH_HEIGHT)
                ) {
                    val itemWidthPx = size.width / forecasts.size
                    val graphPadding = 4.dp.toPx()
                    val usableHeight = size.height - graphPadding * 2

                    val path = Path()
                    forecasts.forEachIndexed { index, forecast ->
                        val x = itemWidthPx * index + itemWidthPx / 2
                        val normalized = (forecast.temperature - minTemp) / tempRange
                        val y = graphPadding + usableHeight * (1 - normalized).toFloat()

                        if (index == 0) {
                            path.moveTo(x, y)
                        } else {
                            path.lineTo(x, y)
                        }
                    }

                    drawPath(
                        path = path,
                        color = lineColor,
                        style = Stroke(width = 2.dp.toPx())
                    )

                    // Draw dots at each data point
                    forecasts.forEachIndexed { index, forecast ->
                        val x = itemWidthPx * index + itemWidthPx / 2
                        val normalized = (forecast.temperature - minTemp) / tempRange
                        val y = graphPadding + usableHeight * (1 - normalized).toFloat()

                        drawCircle(
                            color = lineColor,
                            radius = 3.dp.toPx(),
                            center = Offset(x, y)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(4.dp))

            // Time row (at bottom)
            Row {
                forecasts.forEach { forecast ->
                    Box(
                        modifier = Modifier.width(ITEM_WIDTH),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = formatHour(forecast.time),
                            style = textStyleLabel,
                            color = onSurfaceVariant
                        )
                    }
                }
            }
        }
    }
}

private fun formatHour(isoTime: String): String {
    val hour = isoTime.substringAfter("T").substringBefore(":").toIntOrNull() ?: return isoTime
    return when {
        hour == 0 -> "12 AM"
        hour < 12 -> "$hour AM"
        hour == 12 -> "12 PM"
        else -> "${hour - 12} PM"
    }
}
