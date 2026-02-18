package com.example.mukmuk.di

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.mukmuk.data.local.AppDatabase
import com.example.mukmuk.data.local.FavoriteDao
import com.example.mukmuk.data.local.HistoryDao
import com.example.mukmuk.data.local.MenuDao
import com.example.mukmuk.data.location.LocationService
import com.example.mukmuk.data.remote.NetworkModule
import com.example.mukmuk.data.repository.HistoryRepository
import com.example.mukmuk.data.repository.RemoteRestaurantRepository
import com.example.mukmuk.data.repository.SettingsRepository
import com.example.mukmuk.ui.HistoryViewModel
import com.example.mukmuk.ui.RestaurantViewModel
import com.example.mukmuk.ui.RouletteViewModel
import com.example.mukmuk.ui.SettingsViewModel

class AppContainer(context: Context) {
    val database: AppDatabase = AppDatabase.getInstance(context)
    val historyDao: HistoryDao = database.historyDao()
    val menuDao: MenuDao = database.menuDao()
    val favoriteDao: FavoriteDao = database.favoriteDao()
    val historyRepository: HistoryRepository = HistoryRepository(historyDao)
    val settingsRepository: SettingsRepository = SettingsRepository(context)
    val locationService: LocationService = LocationService(context)
    val remoteRestaurantRepository: RemoteRestaurantRepository =
        RemoteRestaurantRepository(NetworkModule.kakaoApi)
}

class MukmukViewModelFactory(private val container: AppContainer) : ViewModelProvider.Factory {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T = when {
        modelClass.isAssignableFrom(RouletteViewModel::class.java) ->
            RouletteViewModel(
                container.historyRepository,
                container.settingsRepository,
                container.locationService,
                container.remoteRestaurantRepository
            ) as T
        modelClass.isAssignableFrom(HistoryViewModel::class.java) ->
            HistoryViewModel(container.historyRepository) as T
        modelClass.isAssignableFrom(RestaurantViewModel::class.java) ->
            RestaurantViewModel(
                container.favoriteDao,
                container.locationService,
                container.remoteRestaurantRepository
            ) as T
        modelClass.isAssignableFrom(SettingsViewModel::class.java) ->
            SettingsViewModel(container.settingsRepository) as T
        else -> throw IllegalArgumentException("Unknown ViewModel class: ${modelClass.name}")
    }
}
