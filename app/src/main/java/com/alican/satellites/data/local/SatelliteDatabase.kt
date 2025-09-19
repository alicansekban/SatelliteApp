package com.alican.satellites.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import com.alican.satellites.data.local.dao.SatelliteDetailDao
import com.alican.satellites.data.local.entity.SatelliteDetailEntity

@Database(
    entities = [SatelliteDetailEntity::class],
    version = 1,
    exportSchema = false
)
abstract class SatelliteDatabase : RoomDatabase() {

    abstract fun satelliteDetailDao(): SatelliteDetailDao
}