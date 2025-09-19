package com.alican.satellites.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.alican.satellites.data.model.SatelliteDetail

@Entity(tableName = "satellite_details")
data class SatelliteDetailEntity(
    @PrimaryKey
    val id: Int,
    val costPerLaunch: Long,
    val firstFlight: String,
    val height: Int,
    val mass: Int
)

fun SatelliteDetailEntity.toSatelliteDetail(): SatelliteDetail {
    return SatelliteDetail(
        id = id,
        cost_per_launch = costPerLaunch,
        first_flight = firstFlight,
        height = height,
        mass = mass
    )
}

fun SatelliteDetail.toEntity(): SatelliteDetailEntity {
    return SatelliteDetailEntity(
        id = id,
        costPerLaunch = cost_per_launch,
        firstFlight = first_flight,
        height = height,
        mass = mass
    )
}