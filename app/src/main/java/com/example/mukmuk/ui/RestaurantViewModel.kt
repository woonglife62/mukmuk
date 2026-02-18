package com.example.mukmuk.ui

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.mukmuk.data.local.FavoriteDao
import com.example.mukmuk.data.local.VisitRecordDao
import com.example.mukmuk.data.location.LocationService
import com.example.mukmuk.data.model.Category
import com.example.mukmuk.data.model.FavoriteRestaurant
import com.example.mukmuk.data.model.Restaurant
import com.example.mukmuk.data.model.VisitRecord
import com.example.mukmuk.data.repository.RemoteRestaurantRepository
import com.example.mukmuk.data.repository.RestaurantRepository
import com.example.mukmuk.data.repository.SettingsRepository
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

sealed class RestaurantUiState {
    data object Idle : RestaurantUiState()
    data object Loading : RestaurantUiState()
    data class Success(val restaurants: List<Restaurant>) : RestaurantUiState()
    data class Error(val message: String) : RestaurantUiState()
}

class RestaurantViewModel(
    private val favoriteDao: FavoriteDao,
    private val locationService: LocationService,
    private val remoteRestaurantRepository: RemoteRestaurantRepository,
    private val settingsRepository: SettingsRepository,
    private val visitRecordDao: VisitRecordDao
) : ViewModel() {

    var searchQuery by mutableStateOf("")
        private set

    private val _searchQueryFlow = MutableStateFlow("")

    val searchRadius = settingsRepository.searchRadius
        .stateIn(viewModelScope, SharingStarted.Eagerly, 2000)

    var currentLatitude by mutableStateOf(37.4979)
        private set
    var currentLongitude by mutableStateOf(127.0276)
        private set

    var selectedCategory by mutableStateOf<Category?>(null)
        private set

    var showFavoritesOnly by mutableStateOf(false)
        private set

    var apiSearchState by mutableStateOf<RestaurantUiState>(RestaurantUiState.Idle)
        private set

    var temporaryRestaurants by mutableStateOf<List<Restaurant>>(emptyList())
        private set

    fun updateTemporaryRestaurants(restaurants: List<Restaurant>) {
        temporaryRestaurants = restaurants
    }

    val favorites: StateFlow<List<FavoriteRestaurant>> = favoriteDao.getAllFavorites()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    init {
        @OptIn(FlowPreview::class)
        _searchQueryFlow
            .debounce(300L)
            .distinctUntilChanged()
            .onEach { query ->
                if (query.isNotBlank()) searchFromApi(query)
                else apiSearchState = RestaurantUiState.Idle
            }
            .launchIn(viewModelScope)

        viewModelScope.launch {
            searchFromApi("맛집")
        }
    }

    val filteredRestaurants: List<Restaurant>
        get() {
            val apiResults = (apiSearchState as? RestaurantUiState.Success)?.restaurants
            val base = when {
                apiResults != null -> apiResults
                searchQuery.isNotBlank() -> RestaurantRepository.searchRestaurants(searchQuery)
                else -> RestaurantRepository.getRestaurantsByCategory(selectedCategory)
            }
            return if (showFavoritesOnly) {
                val favoriteNames = favorites.value.map { it.restaurantName }.toSet()
                base.filter { it.name in favoriteNames }
            } else {
                base
            }
        }

    fun updateSearchQuery(query: String) {
        searchQuery = query
        _searchQueryFlow.value = query
    }

    fun updateSelectedCategory(category: Category?) {
        selectedCategory = category
        if (category != null) showFavoritesOnly = false
        apiSearchState = RestaurantUiState.Idle
    }

    fun toggleFavoritesOnly() {
        showFavoritesOnly = !showFavoritesOnly
        if (showFavoritesOnly) selectedCategory = null
    }

    fun toggleFavorite(restaurantName: String, onResult: ((added: Boolean) -> Unit)? = null) {
        viewModelScope.launch {
            val isFav = favorites.value.any { it.restaurantName == restaurantName }
            if (isFav) {
                favoriteDao.deleteByName(restaurantName)
                onResult?.invoke(false)
            } else {
                favoriteDao.insert(FavoriteRestaurant(restaurantName = restaurantName))
                onResult?.invoke(true)
            }
        }
    }

    fun isFavorite(restaurantName: String): Boolean {
        return favorites.value.any { it.restaurantName == restaurantName }
    }

    private fun searchFromApi(query: String) {
        viewModelScope.launch {
            apiSearchState = RestaurantUiState.Loading
            try {
                val location = locationService.getCurrentLocation()
                currentLatitude = location.latitude
                currentLongitude = location.longitude
                val radius = searchRadius.value
                val results = remoteRestaurantRepository.searchNearby(
                    query, location.latitude, location.longitude, radius
                )
                apiSearchState = RestaurantUiState.Success(results)
            } catch (_: Exception) {
                apiSearchState = RestaurantUiState.Error("검색에 실패했습니다")
            }
        }
    }

    fun addVisitRecord(restaurant: Restaurant) {
        viewModelScope.launch {
            val existing = visitRecordDao.findByName(restaurant.name)
            if (existing == null) {
                visitRecordDao.insert(
                    VisitRecord(
                        restaurantName = restaurant.name,
                        category = restaurant.category?.name ?: "",
                        address = restaurant.address,
                        phone = restaurant.phone,
                        placeUrl = restaurant.placeUrl,
                        latitude = restaurant.latitude,
                        longitude = restaurant.longitude
                    )
                )
            }
        }
    }
}
