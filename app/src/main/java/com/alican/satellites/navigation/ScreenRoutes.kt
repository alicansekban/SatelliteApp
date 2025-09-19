package com.alican.satellites.navigation

import kotlinx.serialization.Serializable

sealed class ScreenRoutes {
    @Serializable
    data object SatelliteList : ScreenRoutes()

    @Serializable
    data class SatelliteDetail(val id: Int) : ScreenRoutes()
}