
package com.alican.satellites.data.model

import kotlinx.serialization.Serializable

@Serializable
data class SatellitePositionResponse(
    val list: List<SatellitePosition>
)

@Serializable
data class SatellitePosition(
    val id: String,
    val positions: List<Position>
)

@Serializable
data class Position(
    val posX: Double,
    val posY: Double
)