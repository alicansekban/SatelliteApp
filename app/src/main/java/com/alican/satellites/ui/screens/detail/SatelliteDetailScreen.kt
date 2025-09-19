package com.alican.satellites.ui.screens.detail

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import com.alican.satellites.R
import com.alican.satellites.data.model.Position
import com.alican.satellites.data.model.Satellite
import com.alican.satellites.data.model.SatelliteDetail
import com.alican.satellites.ui.screens.detail.components.ErrorContent
import com.alican.satellites.ui.screens.detail.components.LoadingContent
import com.alican.satellites.ui.screens.detail.components.SatelliteDetailContent
import com.alican.satellites.ui.theme.SatellitesTheme
import org.koin.androidx.compose.koinViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SatelliteDetailScreen(
    viewModel: SatelliteDetailViewModel = koinViewModel(),
    onBackClicked: () -> Unit = {}
) {
    val uiState by viewModel.uiState.collectAsState()

    Surface(
        modifier = Modifier.fillMaxSize(),
        color = MaterialTheme.colorScheme.background,
    ) {
        SatelliteDetailScreenContent(
            uiState = uiState,
            onBackClicked = onBackClicked
        )
    }
}
@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun SatelliteDetailScreenContent(
    uiState: SatelliteDetailUiState,
    onBackClicked: () -> Unit = {},
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = uiState.satellite?.name ?: stringResource(R.string.satellite_detail),
                        style = MaterialTheme.typography.headlineSmall
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onBackClicked) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = stringResource(R.string.back)
                        )
                    }
                }
            )
        }
    ) { paddingValues ->
        when {
            uiState.isLoading -> {
                LoadingContent(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(paddingValues)
                )
            }

            uiState.error != null -> {
                ErrorContent(
                    error = uiState.error,
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(paddingValues)
                )
            }

            uiState.satellite != null -> {
                SatelliteDetailContent(
                    satellite = uiState.satellite,
                    satelliteDetail = uiState.satelliteDetail,
                    currentPosition = uiState.currentPosition,
                    isLoadingDetail = uiState.isLoadingDetail,
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(paddingValues)
                )
            }
        }
    }
}

// Preview Data
private val previewSatellite = Satellite(id = 1, name = "Starship-1", active = true)
private val previewInactiveSatellite = Satellite(id = 2, name = "Dragon", active = false)
private val previewPosition = Position(posX = 0.864328541, posY = 0.646450811)
private val previewSatelliteDetail = SatelliteDetail(
    id = 1,
    cost_per_launch = 7200000,
    first_flight = "2006-03-24",
    height = 22,
    mass = 30146
)

@Preview(showBackground = true, name = "Satellite Detail - Complete Data")
@Composable
private fun SatelliteDetailScreenCompletePreview() {
    SatellitesTheme {
        SatelliteDetailScreenContent(
            uiState = SatelliteDetailUiState(
                satellite = previewSatellite,
                satelliteDetail = previewSatelliteDetail,
                currentPosition = previewPosition,
                isLoading = false,
                isLoadingDetail = false,
                error = null
            )
        )
    }
}

@Preview(showBackground = true, name = "Satellite Detail - Loading State")
@Composable
private fun SatelliteDetailScreenLoadingPreview() {
    SatellitesTheme {
        SatelliteDetailScreenContent(
            uiState = SatelliteDetailUiState(
                satellite = null,
                isLoading = true,
                isLoadingDetail = false,
                error = null
            )
        )
    }
}

@Preview(showBackground = true, name = "Satellite Detail - Error State")
@Composable
private fun SatelliteDetailScreenErrorPreview() {
    SatellitesTheme {
        SatelliteDetailScreenContent(
            uiState = SatelliteDetailUiState(
                satellite = null,
                isLoading = false,
                isLoadingDetail = false,
                error = "Failed to load satellite: Satellite with id 999 not found"
            )
        )
    }
}

@Preview(showBackground = true, name = "Satellite Detail - Loading Details")
@Composable
private fun SatelliteDetailScreenLoadingDetailsPreview() {
    SatellitesTheme {
        SatelliteDetailScreenContent(
            uiState = SatelliteDetailUiState(
                satellite = previewSatellite,
                satelliteDetail = null,
                currentPosition = previewPosition,
                isLoading = false,
                isLoadingDetail = true,
                error = null
            )
        )
    }
}

@Preview(showBackground = true, name = "Satellite Detail - No Position Data")
@Composable
private fun SatelliteDetailScreenNoPositionPreview() {
    SatellitesTheme {
        SatelliteDetailScreenContent(
            uiState = SatelliteDetailUiState(
                satellite = previewInactiveSatellite,
                satelliteDetail = previewSatelliteDetail,
                currentPosition = null,
                isLoading = false,
                isLoadingDetail = false,
                error = null
            )
        )
    }
}

@Preview(showBackground = true, name = "Satellite Detail - No Detail Data")
@Composable
private fun SatelliteDetailScreenNoDetailPreview() {
    SatellitesTheme {
        SatelliteDetailScreenContent(
            uiState = SatelliteDetailUiState(
                satellite = previewSatellite,
                satelliteDetail = null,
                currentPosition = previewPosition,
                isLoading = false,
                isLoadingDetail = false,
                error = null
            )
        )
    }
}