package com.example.mukmuk.ui

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.graphics.Color
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.mukmuk.data.local.FavoriteDao
import com.example.mukmuk.data.local.VisitRecordDao
import com.example.mukmuk.data.location.LocationService
import com.example.mukmuk.data.model.Category
import com.example.mukmuk.data.model.HistoryEntry
import com.example.mukmuk.data.model.Menu
import com.example.mukmuk.data.model.Restaurant
import com.example.mukmuk.data.model.VisitRecord
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
    private val remoteRestaurantRepository: RemoteRestaurantRepository,
    private val visitRecordDao: VisitRecordDao,
    private val favoriteDao: FavoriteDao
) : ViewModel() {

    val hapticEnabled = settingsRepository.hapticEnabled
        .stateIn(viewModelScope, SharingStarted.Eagerly, true)

    val soundEnabled = settingsRepository.soundEnabled
        .stateIn(viewModelScope, SharingStarted.Eagerly, true)

    val darkTheme = settingsRepository.darkTheme
        .stateIn(viewModelScope, SharingStarted.Eagerly, true)

    val favorites = favoriteDao.getAllFavorites()
        .stateIn(viewModelScope, SharingStarted.Eagerly, emptyList())

    var favoriteRouletteMode by mutableStateOf(false)
        private set

    var selectedFavoriteNames by mutableStateOf<Set<String>>(emptySet())
        private set

    var selectedFavoriteResult by mutableStateOf<String?>(null)
        private set

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

    var snackbarMessage by mutableStateOf("")
        private set

    val categories = Category.entries.toList()

    val filteredMenus: List<Menu>
        get() = MenuRepository.getFilteredMenus(selectedCategories)

    val favoriteMenusForWheel: List<Menu>
        get() {
            val colors = listOf(
                Color(0xFFE74C3C), Color(0xFF3498DB), Color(0xFF2ECC71), Color(0xFFF39C12),
                Color(0xFF9B59B6), Color(0xFF1ABC9C), Color(0xFFE67E22), Color(0xFF34495E)
            )
            return selectedFavoriteNames.toList().mapIndexed { index, name ->
                Menu(name, "⭐", Category.KOREAN, colors[index % colors.size])
            }
        }

    var restaurants by mutableStateOf<List<Restaurant>>(emptyList())
        private set

    var isLoadingRestaurants by mutableStateOf(false)
        private set

    fun toggleFavoriteRouletteMode() {
        // Reset result state when switching modes to prevent stale results
        resetToWheel()
        favoriteRouletteMode = !favoriteRouletteMode
    }

    fun toggleFavoriteForRoulette(name: String) {
        selectedFavoriteNames = if (name in selectedFavoriteNames) {
            selectedFavoriteNames - name
        } else {
            selectedFavoriteNames + name
        }
    }

    fun selectAllFavoritesForRoulette() {
        selectedFavoriteNames = favorites.value.map { it.restaurantName }.toSet()
    }

    fun clearFavoriteSelection() {
        selectedFavoriteNames = emptySet()
    }

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
        if (favoriteRouletteMode) {
            val menus = favoriteMenusForWheel
            if (menus.isEmpty()) return
            val normalized = ((finalAngle % 360f) + 360f) % 360f
            val arc = 360f / menus.size
            val index = ((360f - normalized + arc / 2f) % 360f / arc).toInt() % menus.size
            selectedMenu = menus[index]
            selectedFavoriteResult = menus[index].name
            isSpinning = false
            loadNearbyRestaurants(menus[index].name, hasLocationPermission)
        } else {
            val menus = filteredMenus
            if (menus.isEmpty()) return
            val normalized = ((finalAngle % 360f) + 360f) % 360f
            val arc = 360f / menus.size
            val index = ((360f - normalized + arc / 2f) % 360f / arc).toInt() % menus.size
            selectedMenu = menus[index]
            isSpinning = false
            loadNearbyRestaurants(menus[index].name, hasLocationPermission)
        }
    }

    fun showResultScreen() {
        showResult = true
    }

    fun resetToWheel() {
        showResult = false
        selectedMenu = null
        restaurants = emptyList()
        selectedFavoriteResult = null
    }

    fun selectRestaurant(restaurant: Restaurant) {
        viewModelScope.launch {
            val record = VisitRecord(
                restaurantName = restaurant.name,
                category = selectedMenu?.category?.displayName ?: "",
                address = restaurant.address,
                phone = restaurant.phone,
                placeUrl = restaurant.placeUrl,
                latitude = restaurant.latitude,
                longitude = restaurant.longitude,
                visited = true,
                visitDate = System.currentTimeMillis()
            )
            visitRecordDao.insert(record)
            snackbarMessage = "식당이 기록되었습니다! \uD83C\uDF7D\uFE0F"
            showConfirmSnackbar = true
            showResult = false
            selectedMenu = null
            restaurants = emptyList()
        }
    }

    fun confirmSelection() {
        showResult = false
        selectedMenu = null
        restaurants = emptyList()
    }

    fun dismissSnackbar() {
        showConfirmSnackbar = false
        snackbarMessage = ""
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

    val searchRadius = settingsRepository.searchRadius
        .stateIn(viewModelScope, SharingStarted.Eagerly, 2000)

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

    fun setSearchRadius(radius: Int) {
        viewModelScope.launch {
            settingsRepository.setSearchRadius(radius)
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
            } catch (e: java.net.UnknownHostException) {
                snackbarMessage = "\uC778\uD130\uB137 \uC5F0\uACB0\uC744 \uD655\uC778\uD574\uC8FC\uC138\uC694"
                showConfirmSnackbar = true
                restaurants = RestaurantRepository.getRestaurants(menuName)
            } catch (_: Exception) {
                restaurants = RestaurantRepository.getRestaurants(menuName)
            } finally {
                isLoadingRestaurants = false
            }
        }
    }
}
