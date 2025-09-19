package com.alican.satellites.di

import androidx.room.Room
import com.alican.satellites.data.local.SatelliteDatabase
import com.alican.satellites.data.repository.SatelliteRepository
import com.alican.satellites.data.repository.SatelliteRepositoryImpl
import com.alican.satellites.domain.interactor.SatelliteDetailInteractor
import com.alican.satellites.domain.interactor.SatelliteDetailInteractorImpl
import com.alican.satellites.domain.interactor.SatelliteListInteractor
import com.alican.satellites.domain.interactor.SatelliteListInteractorImpl
import com.alican.satellites.ui.screens.detail.SatelliteDetailViewModel
import com.alican.satellites.ui.screens.list.SatelliteListViewModel
import com.alican.satellites.utils.AppConstants
import org.koin.android.ext.koin.androidContext
import org.koin.core.module.dsl.singleOf
import org.koin.core.module.dsl.viewModelOf
import org.koin.dsl.bind
import org.koin.dsl.module

val appModule = module {

    // Database
    single<SatelliteDatabase> {
        Room.databaseBuilder(
            androidContext(),
            SatelliteDatabase::class.java,
            AppConstants.APP_DB_NAME
        ).build()
    }

    // DAO
    single { get<SatelliteDatabase>().satelliteDetailDao() }

    // Repository
    singleOf(::SatelliteRepositoryImpl) bind SatelliteRepository::class

    // Interactors
    singleOf(::SatelliteListInteractorImpl) bind SatelliteListInteractor::class
    singleOf(::SatelliteDetailInteractorImpl) bind SatelliteDetailInteractor::class

    // ViewModels
    viewModelOf(::SatelliteListViewModel)
    viewModelOf(::SatelliteDetailViewModel)
}