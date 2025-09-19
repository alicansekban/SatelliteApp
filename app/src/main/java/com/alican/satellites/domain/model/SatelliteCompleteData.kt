package com.alican.satellites.domain.model

import com.alican.satellites.data.model.Satellite
import com.alican.satellites.data.model.SatelliteDetail

data class SatelliteCompleteData(
    val satellite: Satellite,
    val satelliteDetail: SatelliteDetail?
)