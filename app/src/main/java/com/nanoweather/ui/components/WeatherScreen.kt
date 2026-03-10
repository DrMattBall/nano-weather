package com.nanoweather.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.nanoweather.ui.MainViewModel

@Composable
fun WeatherScreen(viewModel: MainViewModel) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    Scaffold { innerPadding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
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

            if (uiState.cities.isEmpty()) {
                item {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 48.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "Search for a city to get started",
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
                    onToggle = { viewModel.onCityToggled(cityWeatherState.city.id) },
                    onRemove = { viewModel.onCityRemoved(cityWeatherState.city.id) }
                )
            }
        }
    }
}
