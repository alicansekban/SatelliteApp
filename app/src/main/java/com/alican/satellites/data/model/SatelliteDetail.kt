package com.alican.satellites.data.model

import kotlinx.serialization.Serializable

@Serializable
data class SatelliteDetail(
    val id: Int,
    val cost_per_launch: Long,
    val first_flight: String,
    val height: Int,
    val mass: Int
)