package com.alican.satellites.ui.screens.list

import com.alican.satellites.data.model.Satellite

sealed interface SatelliteListUIEvent {
    data object RetryClicked : SatelliteListUIEvent
    data class SearchQueryChanged(val query: String) : SatelliteListUIEvent
}
data class SatelliteListUiState(
    val satellites: List<Satellite> = emptyList(),
    val filteredSatellites: List<Satellite> = emptyList(),
    val isLoading: Boolean = true,
    val searchQuery: String = "",
    val error: String? = null
)