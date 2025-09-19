package com.alican.satellites.ui.screens.detail

import com.alican.satellites.data.model.Position
import com.alican.satellites.data.model.Satellite
import com.alican.satellites.data.model.SatelliteDetail

sealed interface SatelliteDetailUIEvent {
    data object RetryClicked : SatelliteDetailUIEvent
    data object ClearError : SatelliteDetailUIEvent
}
data class SatelliteDetailUiState(
    val satellite: Satellite? = null,
    val satelliteDetail: SatelliteDetail? = null,
    val currentPosition: Position? = null,
    val isLoading: Boolean = true,
    val isLoadingDetail: Boolean = false,
    val error: String? = null
)