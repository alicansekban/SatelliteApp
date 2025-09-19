package com.alican.satellites.ui.screens.detail

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.toRoute
import com.alican.satellites.domain.interactor.SatelliteDetailInteractor
import com.alican.satellites.domain.interactor.SatelliteNotFoundException
import com.alican.satellites.navigation.ScreenRoutes
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class SatelliteDetailViewModel(
    private val satelliteDetailInteractor: SatelliteDetailInteractor,
    savedStateHandle: SavedStateHandle
) : ViewModel() {

    private val args = savedStateHandle.toRoute<ScreenRoutes.SatelliteDetail>()
    private val _uiState = MutableStateFlow(SatelliteDetailUiState())
    val uiState: StateFlow<SatelliteDetailUiState> = _uiState.asStateFlow()

    private var positionUpdateJob: Job? = null

    init {
        loadSatelliteData()
        startPositionUpdates()
    }

    private fun loadSatelliteData() {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, error = null)

            // Load satellite basic info
            satelliteDetailInteractor.getSatelliteById(args.id)
                .onSuccess { satellite ->
                    _uiState.value = _uiState.value.copy(
                        satellite = satellite,
                        isLoading = false
                    )
                    // Load satellite detail
                    loadSatelliteDetail()
                }
                .onFailure { exception ->
                    val errorMessage = when (exception) {
                        is SatelliteNotFoundException -> exception.message ?: "Satellite not found"
                        else -> "Failed to load satellite: ${exception.message}"
                    }
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        error = errorMessage
                    )
                }
        }
    }

    private fun loadSatelliteDetail() {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoadingDetail = true)

            satelliteDetailInteractor.getSatelliteDetail(args.id)
                .onSuccess { detail ->
                    _uiState.value = _uiState.value.copy(
                        satelliteDetail = detail,
                        isLoadingDetail = false
                    )
                }
                .onFailure { exception ->
                    _uiState.value = _uiState.value.copy(
                        isLoadingDetail = false,
                        error = "Failed to load satellite detail: ${exception.message}"
                    )
                }
        }
    }

    private fun startPositionUpdates() {
        positionUpdateJob?.cancel()
        positionUpdateJob = viewModelScope.launch {
            satelliteDetailInteractor.observePositionUpdates(args.id).collect { result ->
                result
                    .onSuccess { position ->
                        _uiState.value = _uiState.value.copy(currentPosition = position)
                    }
                    .onFailure { exception ->
                        // Position updates are not critical, but we can log or show a subtle error
                        _uiState.value = _uiState.value.copy(
                            error = "Position update failed: ${exception.message}"
                        )
                    }
            }
        }
    }

    fun retry() {
        loadSatelliteData()
        startPositionUpdates()
    }

    fun clearError() {
        _uiState.value = _uiState.value.copy(error = null)
    }

    override fun onCleared() {
        super.onCleared()
        positionUpdateJob?.cancel()
    }
}