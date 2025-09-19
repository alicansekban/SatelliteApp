package com.alican.satellites.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.alican.satellites.data.local.entity.SatelliteDetailEntity

@Dao
interface SatelliteDetailDao {

    @Query("SELECT * FROM satellite_details WHERE id = :satelliteId")
    suspend fun getSatelliteDetail(satelliteId: Int): SatelliteDetailEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertSatelliteDetail(satelliteDetail: SatelliteDetailEntity)

    @Query("DELETE FROM satellite_details")
    suspend fun clearAll()
}