package com.example.mukmuk.ui

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.mukmuk.data.location.LocationService
import com.example.mukmuk.data.model.Category
import com.example.mukmuk.data.model.HistoryEntry
import com.example.mukmuk.data.model.Menu
import com.example.mukmuk.data.model.Restaurant
import com.example.mukmuk.data.repository.HistoryRepository
import com.example.mukmuk.data.repository.MenuRepository
import com.example.mukmuk.data.repository.RemoteRestaurantRepository
import com.example.mukmuk.data.repository.RestaurantRepository
import com.example.mukmuk.data.repository.SettingsRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class RouletteViewModel(
    private val historyRepository: HistoryRepository,
    val settingsRepository: SettingsRepository,
    private val locationService: LocationService,
    private val remoteRestaurantRepository: RemoteRestaurantRepository
) : ViewModel() {

    val hapticEnabled = settingsRepository.hapticEnabled
        .stateIn(viewModelScope, SharingStarted.Eagerly, true)

    val soundEnabled = settingsRepository.soundEnabled
        .stateIn(viewModelScope, SharingStarted.Eagerly, true)

    val darkTheme = settingsRepository.darkTheme
        .stateIn(viewModelScope, SharingStarted.Eagerly, true)

    var selectedCategories by mutableStateOf(emptySet<Category>())
        private set

    var isSpinning by mutableStateOf(false)
        private set

    var showResult by mutableStateOf(false)
        private set

    var selectedMenu by mutableStateOf<Menu?>(null)
        private set

    var rotation by mutableFloatStateOf(0f)
        private set

    val history: Flow<List<HistoryEntry>> = historyRepository.allHistory

    var showConfirmSnackbar by mutableStateOf(false)
        private set

    val categories = Category.entries.toList()

    val filteredMenus: List<Menu>
        get() = MenuRepository.getFilteredMenus(selectedCategories)

    var restaurants by mutableStateOf<List<Restaurant>>(emptyList())
        private set

    var isLoadingRestaurants by mutableStateOf(false)
        private set

    fun toggleCategory(category: Category) {
        selectedCategories = if (category in selectedCategories) {
            selectedCategories - category
        } else {
            selectedCategories + category
        }
    }

    fun updateSpinning(spinning: Boolean) {
        isSpinning = spinning
    }

    fun updateRotation(newRotation: Float) {
        rotation = newRotation
    }

    fun selectMenuAtIndex(index: Int) {
        val menus = filteredMenus
        if (index in menus.indices) {
            selectedMenu = menus[index]
        }
    }

    fun onSpinComplete(finalAngle: Float, hasLocationPermission: Boolean = true) {
        val menus = filteredMenus
        if (menus.isEmpty()) return
        val normalized = ((finalAngle % 360f) + 360f) % 360f
        val arc = 360f / menus.size
        val index = ((360f - normalized + arc / 2f) % 360f / arc).toInt() % menus.size
        selectedMenu = menus[index]
        isSpinning = false
        loadNearbyRestaurants(menus[index].name, hasLocationPermission)
    }

    fun showResultScreen() {
        showResult = true
    }

    fun resetToWheel() {
        showResult = false
        selectedMenu = null
        restaurants = emptyList()
    }

    fun confirmSelection() {
        selectedMenu?.let { menu ->
            viewModelScope.launch {
                historyRepository.insert(HistoryEntry.fromMenu(menu))
            }
            showConfirmSnackbar = true
            showResult = false
            selectedMenu = null
            restaurants = emptyList()
        }
    }

    fun dismissSnackbar() {
        showConfirmSnackbar = false
    }

    fun clearAllCategories() {
        selectedCategories = emptySet()
    }

    fun clearHistory() {
        viewModelScope.launch {
            historyRepository.deleteAll()
        }
    }

    fun setHapticEnabled(enabled: Boolean) {
        viewModelScope.launch {
            settingsRepository.setHapticEnabled(enabled)
        }
    }

    fun setSoundEnabled(enabled: Boolean) {
        viewModelScope.launch {
            settingsRepository.setSoundEnabled(enabled)
        }
    }

    fun setDarkTheme(enabled: Boolean) {
        viewModelScope.launch {
            settingsRepository.setDarkTheme(enabled)
        }
    }

    val notificationEnabled = settingsRepository.notificationEnabled
        .stateIn(viewModelScope, SharingStarted.Eagerly, false)

    val notificationHour = settingsRepository.notificationHour
        .stateIn(viewModelScope, SharingStarted.Eagerly, 12)

    val notificationMinute = settingsRepository.notificationMinute
        .stateIn(viewModelScope, SharingStarted.Eagerly, 0)

    fun setNotificationEnabled(enabled: Boolean) {
        viewModelScope.launch {
            settingsRepository.setNotificationEnabled(enabled)
        }
    }

    fun setNotificationTime(hour: Int, minute: Int) {
        viewModelScope.launch {
            settingsRepository.setNotificationTime(hour, minute)
        }
    }

    private fun loadNearbyRestaurants(menuName: String, hasLocationPermission: Boolean = true) {
        viewModelScope.launch {
            isLoadingRestaurants = true
            try {
                if (!hasLocationPermission) {
                    restaurants = RestaurantRepository.getRestaurants(menuName)
                    return@launch
                }
                val location = locationService.getCurrentLocation()
                restaurants = remoteRestaurantRepository.searchNearby(
                    menuName, location.latitude, location.longitude
                )
            } catch (_: Exception) {
                restaurants = RestaurantRepository.getRestaurants(menuName)
            } finally {
                isLoadingRestaurants = false
            }
        }
    }
}
