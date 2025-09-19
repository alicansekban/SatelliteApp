package com.alican.satellites.ui.screens.list

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.alican.satellites.R
import com.alican.satellites.data.model.Satellite
import com.alican.satellites.ui.theme.SatellitesTheme
import org.koin.androidx.compose.koinViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SatelliteListScreen(
    onNavigateToDetail: (Int) -> Unit = {},
    viewModel: SatelliteListViewModel = koinViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        // Search Bar
        OutlinedTextField(
            value = uiState.searchQuery,
            onValueChange = { query ->
                viewModel.screenEvent(SatelliteListUIEvent.SearchQueryChanged(query = query.trim()))
            },
            label = { Text(stringResource(R.string.search_satellites)) },
            leadingIcon = { Icon(Icons.Default.Search, contentDescription = null) },
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 16.dp),
            singleLine = true
        )

        when {
            uiState.isLoading -> {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        CircularProgressIndicator()
                        Spacer(modifier = Modifier.height(16.dp))
                        Text(
                            text = stringResource(R.string.loading),
                            style = MaterialTheme.typography.bodyLarge
                        )
                    }
                }
            }

            uiState.error != null -> {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        Text(
                            text = uiState.error.orEmpty(),
                            style = MaterialTheme.typography.bodyLarge,
                            color = MaterialTheme.colorScheme.error
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                        Button(
                            onClick = {
                                viewModel.screenEvent(event = SatelliteListUIEvent.RetryClicked)
                            }
                        ) {
                            Text(stringResource(R.string.retry))
                        }
                    }
                }
            }

            uiState.filteredSatellites.isEmpty() && uiState.searchQuery.isNotBlank() -> {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = stringResource(R.string.no_satellites_found, uiState.searchQuery),
                        style = MaterialTheme.typography.bodyLarge
                    )
                }
            }

            else -> {
                LazyColumn(
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(
                        items = uiState.filteredSatellites,
                        key = { it.id }
                    ) { satellite ->
                        SatelliteListItem(
                            satellite = satellite,
                            onClick = { onNavigateToDetail(satellite.id) }
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun SatelliteListItem(
    satellite: Satellite,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .clickable { onClick() },
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    text = satellite.name,
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = stringResource(R.string.satellite_id, satellite.id),
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            // Active Status Indicator
            Box(
                modifier = Modifier
                    .size(12.dp)
                    .padding(4.dp),
                contentAlignment = Alignment.Center
            ) {
                Box(
                    modifier = Modifier
                        .size(12.dp)
                        .padding(2.dp)
                ) {
                    Card(
                        colors = CardDefaults.cardColors(
                            containerColor = if (satellite.active) Color.Green else Color.Red
                        ),
                        modifier = Modifier.fillMaxSize()
                    ) {}
                }
            }
        }
    }
}

// Preview Components for different states
@Composable
private fun SatelliteListScreenPreview(
    uiState: SatelliteListUiState,
    onNavigateToDetail: (Int) -> Unit = {},
    onSearchQueryChanged: (String) -> Unit = {},
    onRetry: () -> Unit = {}
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        // Search Bar
        OutlinedTextField(
            value = uiState.searchQuery,
            onValueChange = onSearchQueryChanged,
            label = { Text(stringResource(R.string.search_satellites)) },
            leadingIcon = { Icon(Icons.Default.Search, contentDescription = null) },
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 16.dp),
            singleLine = true
        )

        when {
            uiState.isLoading -> {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        CircularProgressIndicator()
                        Spacer(modifier = Modifier.height(16.dp))
                        Text(
                            text = stringResource(R.string.loading),
                            style = MaterialTheme.typography.bodyLarge
                        )
                    }
                }
            }

            uiState.error != null -> {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        Text(
                            text = uiState.error,
                            style = MaterialTheme.typography.bodyLarge,
                            color = MaterialTheme.colorScheme.error
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                        Button(onClick = onRetry) {
                            Text(stringResource(R.string.retry))
                        }
                    }
                }
            }

            uiState.filteredSatellites.isEmpty() && uiState.searchQuery.isNotBlank() -> {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = stringResource(R.string.no_satellites_found, uiState.searchQuery),
                        style = MaterialTheme.typography.bodyLarge
                    )
                }
            }

            else -> {
                LazyColumn(
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(
                        items = uiState.filteredSatellites,
                        key = { it.id }
                    ) { satellite ->
                        SatelliteListItem(
                            satellite = satellite,
                            onClick = { onNavigateToDetail(satellite.id) }
                        )
                    }
                }
            }
        }
    }
}

// Preview Data
private val previewSatellites = listOf(
    Satellite(id = 1, name = "Starship-1", active = true),
    Satellite(id = 2, name = "Dragon", active = false),
    Satellite(id = 3, name = "Falcon Heavy", active = true),
    Satellite(id = 4, name = "Starship-10", active = true),
    Satellite(id = 5, name = "Crew Dragon", active = false),
)

@Preview(showBackground = true, name = "Satellite List - Loaded State")
@Composable
private fun SatelliteListScreenLoadedPreview() {
    SatellitesTheme {
        SatelliteListScreenPreview(
            uiState = SatelliteListUiState(
                satellites = previewSatellites,
                filteredSatellites = previewSatellites,
                isLoading = false,
                searchQuery = "",
                error = null
            )
        )
    }
}

@Preview(showBackground = true, name = "Satellite List - Loading State")
@Composable
private fun SatelliteListScreenLoadingPreview() {
    SatellitesTheme {
        SatelliteListScreenPreview(
            uiState = SatelliteListUiState(
                isLoading = true
            )
        )
    }
}

@Preview(showBackground = true, name = "Satellite List - Error State")
@Composable
private fun SatelliteListScreenErrorPreview() {
    SatellitesTheme {
        SatelliteListScreenPreview(
            uiState = SatelliteListUiState(
                isLoading = false,
                error = "Failed to load satellites: Network connection error"
            )
        )
    }
}

@Preview(showBackground = true, name = "Satellite List - Search No Results")
@Composable
private fun SatelliteListScreenNoResultsPreview() {
    SatellitesTheme {
        SatelliteListScreenPreview(
            uiState = SatelliteListUiState(
                satellites = previewSatellites,
                filteredSatellites = emptyList(),
                isLoading = false,
                searchQuery = "Tesla",
                error = null
            )
        )
    }
}

@Preview(showBackground = true, name = "Satellite List - Search Results")
@Composable
private fun SatelliteListScreenSearchResultsPreview() {
    SatellitesTheme {
        SatelliteListScreenPreview(
            uiState = SatelliteListUiState(
                satellites = previewSatellites,
                filteredSatellites = listOf(
                    Satellite(id = 1, name = "Starship-1", active = true),
                    Satellite(id = 4, name = "Starship-10", active = true)
                ),
                isLoading = false,
                searchQuery = "Starship",
                error = null
            )
        )
    }
}

@Preview(showBackground = true, name = "Satellite List Item - Active")
@Composable
private fun SatelliteListItemActivePreview() {
    SatellitesTheme {
        SatelliteListItem(
            satellite = Satellite(id = 1, name = "Starship-1", active = true),
            onClick = {}
        )
    }
}

@Preview(showBackground = true, name = "Satellite List Item - Inactive")
@Composable
private fun SatelliteListItemInactivePreview() {
    SatellitesTheme {
        SatelliteListItem(
            satellite = Satellite(id = 2, name = "Dragon", active = false),
            onClick = {}
        )
    }
}