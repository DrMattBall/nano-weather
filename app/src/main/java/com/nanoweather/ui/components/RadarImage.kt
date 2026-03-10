package com.nanoweather.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Fullscreen
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import coil.compose.AsyncImage
import com.nanoweather.domain.model.RadarMapData

@Composable
fun RadarImage(
    radarMapData: RadarMapData,
    modifier: Modifier = Modifier
) {
    var isFullscreen by remember { mutableStateOf(false) }

    Box(
        modifier = modifier
            .fillMaxWidth()
            .aspectRatio(1f)
            .clip(RoundedCornerShape(12.dp))
            .background(MaterialTheme.colorScheme.surfaceContainerHighest)
            .clickable { isFullscreen = true }
    ) {
        RadarTileGrid(radarMapData = radarMapData)

        IconButton(
            onClick = { isFullscreen = true },
            modifier = Modifier
                .align(Alignment.TopEnd)
                .size(32.dp)
        ) {
            Icon(
                imageVector = Icons.Default.Fullscreen,
                contentDescription = "Expand radar",
                tint = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }

        Text(
            text = "Radar",
            style = MaterialTheme.typography.labelSmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier
                .align(Alignment.TopStart)
                .padding(6.dp)
        )
    }

    if (isFullscreen) {
        Dialog(
            onDismissRequest = { isFullscreen = false },
            properties = DialogProperties(usePlatformDefaultWidth = false)
        ) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(MaterialTheme.colorScheme.surface)
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .aspectRatio(1f)
                        .align(Alignment.Center)
                ) {
                    RadarTileGrid(radarMapData = radarMapData)
                }

                IconButton(
                    onClick = { isFullscreen = false },
                    modifier = Modifier
                        .align(Alignment.TopEnd)
                        .padding(8.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Close,
                        contentDescription = "Close",
                        tint = MaterialTheme.colorScheme.onSurface
                    )
                }
            }
        }
    }
}

@Composable
private fun RadarTileGrid(
    radarMapData: RadarMapData,
    modifier: Modifier = Modifier
) {
    BoxWithConstraints(modifier = modifier.fillMaxSize()) {
        // Scale tile display size so the 3x3 grid fills the container
        val containerPx = with(LocalDensity.current) { maxWidth.toPx() }
        val tileDisplayPx = containerPx / 2f
        val tileDisplayDp = with(LocalDensity.current) { (tileDisplayPx / density).dp }

        val offsetX = Math.round(-(radarMapData.offsetXFraction * tileDisplayPx))
        val offsetY = Math.round(-(radarMapData.offsetYFraction * tileDisplayPx))

        for (dy in -1..1) {
            for (dx in -1..1) {
                val tileX = radarMapData.centerTileX + dx
                val tileY = radarMapData.centerTileY + dy

                val baseX = Math.round((dx + 1) * tileDisplayPx) + offsetX
                val baseY = Math.round((dy + 1) * tileDisplayPx) + offsetY

                val baseMapUrl = radarMapData.baseMapUrlTemplate
                    .replace("{x}", tileX.toString())
                    .replace("{y}", tileY.toString())
                val radarUrl = radarMapData.radarUrlTemplate
                    .replace("{x}", tileX.toString())
                    .replace("{y}", tileY.toString())

                AsyncImage(
                    model = baseMapUrl,
                    contentDescription = null,
                    modifier = Modifier
                        .size(tileDisplayDp)
                        .offset { IntOffset(baseX, baseY) },
                    contentScale = ContentScale.FillBounds
                )
                AsyncImage(
                    model = radarUrl,
                    contentDescription = null,
                    modifier = Modifier
                        .size(tileDisplayDp)
                        .offset { IntOffset(baseX, baseY) }
                        .alpha(0.6f),
                    contentScale = ContentScale.FillBounds
                )
            }
        }
    }
}
