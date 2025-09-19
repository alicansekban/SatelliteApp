package com.alican.satellites.ui.screens.detail.components


import android.annotation.SuppressLint
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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.alican.satellites.R
import com.alican.satellites.data.model.Position
import com.alican.satellites.data.model.Satellite
import com.alican.satellites.data.model.SatelliteDetail
import com.alican.satellites.ui.theme.SatellitesTheme

@Composable
fun SatelliteDetailContent(
    satellite: Satellite,
    satelliteDetail: SatelliteDetail?,
    currentPosition: Position?,
    isLoadingDetail: Boolean,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .verticalScroll(rememberScrollState())
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Basic Satellite Info Card
        SatelliteBasicInfoCard(satellite = satellite)

        // Position Card
        SatellitePositionCard(position = currentPosition)

        // Detail Card
        SatelliteDetailInfoCard(
            satelliteDetail = satelliteDetail,
            isLoading = isLoadingDetail
        )
    }
}

@Composable
private fun SatelliteBasicInfoCard(
    satellite: Satellite,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = satellite.name,
                    style = MaterialTheme.typography.headlineMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface
                )

                // Status Indicator
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Box(
                        modifier = Modifier.size(12.dp)
                    ) {
                        Card(
                            colors = CardDefaults.cardColors(
                                containerColor = if (satellite.active) Color.Green else Color.Red
                            ),
                            modifier = Modifier.fillMaxSize()
                        ) {}
                    }
                    Text(
                        text = stringResource(if (satellite.active) R.string.active else R.string.inactive),
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        fontWeight = FontWeight.Medium
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = stringResource(R.string.satellite_id, satellite.id),
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

@SuppressLint("DefaultLocale")
@Composable
private fun SatellitePositionCard(
    position: Position?,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = stringResource(R.string.current_position),
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface
                )

                if (position != null) {
                    // Live indicator
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        Box(
                            modifier = Modifier.size(8.dp)
                        ) {
                            Card(
                                colors = CardDefaults.cardColors(
                                    containerColor = Color.Green
                                ),
                                modifier = Modifier.fillMaxSize(),
                                content = {}
                            )
                        }
                        Text(
                            text = stringResource(R.string.live),
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            if (position != null) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceEvenly
                ) {
                    PositionItem(
                        label = stringResource(R.string.x_position),
                        value = String.format("%.6f", position.posX),
                        modifier = Modifier.weight(1f)
                    )

                    Spacer(modifier = Modifier.width(16.dp))

                    PositionItem(
                        label = stringResource(R.string.y_position),
                        value = String.format("%.6f", position.posY),
                        modifier = Modifier.weight(1f)
                    )
                }
            } else {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(60.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = stringResource(R.string.position_data_not_available),
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        textAlign = TextAlign.Center
                    )
                }
            }
        }
    }
}

@Composable
private fun PositionItem(
    label: String,
    value: String,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Spacer(modifier = Modifier.height(4.dp))
        Text(
            text = value,
            style = MaterialTheme.typography.headlineSmall,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onSurface
        )
    }
}

@SuppressLint("DefaultLocale")
@Composable
private fun SatelliteDetailInfoCard(
    satelliteDetail: SatelliteDetail?,
    isLoading: Boolean,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Text(
                text = stringResource(R.string.detailed_information),
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface
            )

            Spacer(modifier = Modifier.height(12.dp))

            when {
                isLoading -> {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(120.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            CircularProgressIndicator(
                                modifier = Modifier.size(24.dp),
                                strokeWidth = 2.dp
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                text = stringResource(R.string.loading_details),
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                }

                satelliteDetail != null -> {
                    Column(
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        DetailInfoRow(
                            label = stringResource(R.string.cost_per_launch),
                            value = stringResource(
                                R.string.cost_format,
                                String.format("%,d", satelliteDetail.cost_per_launch)
                            )
                        )

                        DetailInfoRow(
                            label = stringResource(R.string.first_flight),
                            value = satelliteDetail.first_flight
                        )

                        DetailInfoRow(
                            label = stringResource(R.string.height),
                            value = stringResource(R.string.height_format, satelliteDetail.height)
                        )

                        DetailInfoRow(
                            label = stringResource(R.string.mass),
                            value = stringResource(
                                R.string.mass_format,
                                String.format("%,d", satelliteDetail.mass)
                            )
                        )
                    }
                }

                else -> {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(80.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = stringResource(R.string.detail_information_not_available),
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            textAlign = TextAlign.Center
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun DetailInfoRow(
    label: String,
    value: String,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.weight(1f)
        )

        Text(
            text = value,
            style = MaterialTheme.typography.bodyMedium,
            fontWeight = FontWeight.Medium,
            color = MaterialTheme.colorScheme.onSurface,
            textAlign = TextAlign.End,
            modifier = Modifier.weight(1f)
        )
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

@Preview(showBackground = true, name = "Satellite Detail Content")
@Composable
private fun SatelliteDetailContentPreview() {
    SatellitesTheme {
        SatelliteDetailContent(
            satellite = previewSatellite,
            satelliteDetail = previewSatelliteDetail,
            currentPosition = previewPosition,
            isLoadingDetail = false,
            modifier = Modifier.fillMaxSize()
        )
    }
}


@Preview(showBackground = true, name = "Basic Info Card - Active")
@Composable
private fun SatelliteBasicInfoCardActivePreview() {
    SatellitesTheme {
        SatelliteBasicInfoCard(satellite = previewSatellite)
    }
}

@Preview(showBackground = true, name = "Basic Info Card - Inactive")
@Composable
private fun SatelliteBasicInfoCardInactivePreview() {
    SatellitesTheme {
        SatelliteBasicInfoCard(satellite = previewInactiveSatellite)
    }
}

@Preview(showBackground = true, name = "Position Card - With Data")
@Composable
private fun SatellitePositionCardWithDataPreview() {
    SatellitesTheme {
        SatellitePositionCard(position = previewPosition)
    }
}

@Preview(showBackground = true, name = "Position Card - No Data")
@Composable
private fun SatellitePositionCardNoDataPreview() {
    SatellitesTheme {
        SatellitePositionCard(position = null)
    }
}

@Preview(showBackground = true, name = "Detail Info Card - With Data")
@Composable
private fun SatelliteDetailInfoCardWithDataPreview() {
    SatellitesTheme {
        SatelliteDetailInfoCard(
            satelliteDetail = previewSatelliteDetail,
            isLoading = false
        )
    }
}

@Preview(showBackground = true, name = "Detail Info Card - Loading")
@Composable
private fun SatelliteDetailInfoCardLoadingPreview() {
    SatellitesTheme {
        SatelliteDetailInfoCard(
            satelliteDetail = null,
            isLoading = true
        )
    }
}

@Preview(showBackground = true, name = "Detail Info Card - No Data")
@Composable
private fun SatelliteDetailInfoCardNoDataPreview() {
    SatellitesTheme {
        SatelliteDetailInfoCard(
            satelliteDetail = null,
            isLoading = false
        )
    }
}
