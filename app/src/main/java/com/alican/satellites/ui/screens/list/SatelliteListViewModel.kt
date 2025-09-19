package com.alican.satellites.ui.screens.list

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.alican.satellites.domain.interactor.SatelliteListInteractor
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.launch

class SatelliteListViewModel(
    private val satelliteListInteractor: SatelliteListInteractor
) : ViewModel() {

    private val _uiState = MutableStateFlow(SatelliteListUiState())
    val uiState: StateFlow<SatelliteListUiState> = _uiState.asStateFlow()

    init {
        loadSatellites()
        observeSearch()
    }

    private fun loadSatellites() {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, error = null)

            satelliteListInteractor.getSatellites()
                .onSuccess { satellites ->
                    satelliteListInteractor.updateSatellitesList(satellites)
                    _uiState.value = _uiState.value.copy(
                        satellites = satellites,
                        isLoading = false
                    )
                }
                .onFailure { exception ->
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        error = "Failed to load satellites: ${exception.message}"
                    )
                }
        }
    }

    private fun observeSearch() {
        viewModelScope.launch {
            combine(
                satelliteListInteractor.observeSearchQuery(),
                satelliteListInteractor.getFilteredSatellites()
            ) { query, filtered ->
                query to filtered
            }.collect { (query, filtered) ->
                _uiState.value = _uiState.value.copy(
                    searchQuery = query,
                    filteredSatellites = filtered
                )
            }
        }
    }

    fun onSearchQueryChanged(query: String) {
        satelliteListInteractor.updateSearchQuery(query)
    }

    fun retry() {
        loadSatellites()
    }
}