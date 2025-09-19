package com.alican.satellites.ui.screens.list

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.alican.satellites.R
import com.alican.satellites.data.model.Satellite
import com.alican.satellites.ui.screens.list.components.SatelliteListEmptyContent
import com.alican.satellites.ui.screens.list.components.SatelliteListErrorContent
import com.alican.satellites.ui.screens.list.components.SatelliteListItem
import com.alican.satellites.ui.screens.list.components.SatelliteListLoading
import com.alican.satellites.ui.theme.SatellitesTheme
import org.koin.androidx.compose.koinViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SatelliteListScreen(
    onNavigateToDetail: (Int) -> Unit = {},
    viewModel: SatelliteListViewModel = koinViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()

    Surface(
        modifier = Modifier
            .fillMaxSize(),
        color = MaterialTheme.colorScheme.background,
    ) {
        SatelliteListScreenContent(
            uiState = uiState,
            onNavigateToDetail = onNavigateToDetail,
            onSearchQueryChanged = { query ->
                viewModel.screenEvent(SatelliteListUIEvent.SearchQueryChanged(query = query.trim()))
            },
            onEvent = viewModel::screenEvent,
        )
    }
}
@Composable
private fun SatelliteListScreenContent(
    uiState: SatelliteListUiState,
    onNavigateToDetail: (Int) -> Unit = {},
    onSearchQueryChanged: (String) -> Unit = {},
    onEvent: (SatelliteListUIEvent) -> Unit = {},
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
            singleLine = true,
            shape = RoundedCornerShape(20.dp)
        )

        when {
            uiState.isLoading -> {
                SatelliteListLoading()
            }

            uiState.error != null -> {
                SatelliteListErrorContent(
                    errorMessage = uiState.error,
                    onRetryClicked = { onEvent(SatelliteListUIEvent.RetryClicked) }
                )
            }

            uiState.filteredSatellites.isEmpty() && uiState.searchQuery.isNotBlank() -> {
                SatelliteListEmptyContent(
                    searchQuery = uiState.searchQuery,
                )
            }

            else -> {
                LazyColumn(
                    verticalArrangement = Arrangement.spacedBy(12.dp)
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
        SatelliteListScreenContent(
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
        SatelliteListScreenContent(
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
        SatelliteListScreenContent(
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
        SatelliteListScreenContent(
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
        SatelliteListScreenContent(
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