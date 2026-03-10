package com.nanoweather.ui.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.nanoweather.domain.model.City

@Composable
fun CitySearchBar(
    query: String,
    searchResults: List<City>,
    isSearching: Boolean,
    onQueryChanged: (String) -> Unit,
    onCitySelected: (City) -> Unit,
    modifier: Modifier = Modifier
) {
    Column(modifier = modifier) {
        OutlinedTextField(
            value = query,
            onValueChange = onQueryChanged,
            modifier = Modifier.fillMaxWidth(),
            placeholder = { Text("Search for a city") },
            leadingIcon = { Icon(Icons.Default.Search, contentDescription = "Search") },
            singleLine = true
        )

        if (searchResults.isNotEmpty()) {
            Surface(
                modifier = Modifier.fillMaxWidth(),
                tonalElevation = 2.dp,
                shadowElevation = 4.dp,
                shape = MaterialTheme.shapes.small
            ) {
                Column {
                    searchResults.forEachIndexed { index, city ->
                        Text(
                            text = buildCityLabel(city),
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable { onCitySelected(city) }
                                .padding(horizontal = 16.dp, vertical = 12.dp),
                            style = MaterialTheme.typography.bodyLarge
                        )
                        if (index < searchResults.lastIndex) {
                            HorizontalDivider()
                        }
                    }
                }
            }
        }
    }
}

private fun buildCityLabel(city: City): String {
    val parts = mutableListOf(city.name)
    city.admin1?.let { parts.add(it) }
    if (city.country.isNotBlank()) parts.add(city.country)
    return parts.joinToString(", ")
}
