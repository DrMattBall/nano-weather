package com.nanoweather.ui.components

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.nanoweather.data.local.CityImageProvider
import com.nanoweather.data.local.ResourceCityImageProvider
import com.nanoweather.ui.MainViewModel
import com.nanoweather.ui.TemperatureUnit

@Composable
fun WeatherScreen(viewModel: MainViewModel) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val context = LocalContext.current
    val imageProvider: CityImageProvider = remember {
        ResourceCityImageProvider(context.resources, context.packageName)
    }

    BackHandler(enabled = uiState.isSearchVisible) {
        viewModel.onDismissSearch()
    }

    Scaffold { innerPadding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            if (uiState.isSelectionMode) {
                item {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        TextButton(onClick = viewModel::onExitSelectionMode) {
                            Text("Cancel")
                        }
                        Button(
                            onClick = viewModel::onRemoveSelected,
                            enabled = uiState.cities.any { it.isSelected },
                            colors = ButtonDefaults.buttonColors(
                                containerColor = MaterialTheme.colorScheme.error
                            )
                        ) {
                            Text("Remove")
                        }
                    }
                }
            }

            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    IconButton(onClick = viewModel::onShowSearch) {
                        Icon(
                            imageVector = Icons.Default.Add,
                            contentDescription = "Add city"
                        )
                    }
                    TextButton(onClick = viewModel::onToggleTemperatureUnit) {
                        Text(
                            text = if (uiState.temperatureUnit == TemperatureUnit.CELSIUS) "°C" else "°F",
                            style = MaterialTheme.typography.titleMedium
                        )
                    }
                }
            }

            if (uiState.isSearchVisible) {
                item {
                    CitySearchBar(
                        query = uiState.searchQuery,
                        searchResults = uiState.searchResults,
                        isSearching = uiState.isSearching,
                        onQueryChanged = viewModel::onSearchQueryChanged,
                        onCitySelected = viewModel::onCitySelected,
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            }

            if (uiState.cities.isEmpty() && !uiState.isSearchVisible) {
                item {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 48.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "Tap + to add a city",
                            style = MaterialTheme.typography.bodyLarge,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }

            items(
                items = uiState.cities,
                key = { it.city.id }
            ) { cityWeatherState ->
                CityRow(
                    state = cityWeatherState,
                    isSelectionMode = uiState.isSelectionMode,
                    temperatureUnit = uiState.temperatureUnit,
                    backgroundImageResId = imageProvider.getImageResId(cityWeatherState.city.id),
                    onToggle = { viewModel.onCityToggled(cityWeatherState.city.id) },
                    onLongPress = {
                        viewModel.onEnterSelectionMode()
                        viewModel.onToggleSelection(cityWeatherState.city.id)
                    },
                    onSelectionToggle = { viewModel.onToggleSelection(cityWeatherState.city.id) }
                )
            }
        }
    }
}
