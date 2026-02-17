package com.example.mukmuk.ui

import android.app.Application
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.mukmuk.data.local.AppDatabase
import com.example.mukmuk.data.model.Category
import com.example.mukmuk.data.model.FavoriteRestaurant
import com.example.mukmuk.data.model.Restaurant
import com.example.mukmuk.data.repository.RestaurantRepository
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class RestaurantViewModel(application: Application) : AndroidViewModel(application) {

    private val favoriteDao = AppDatabase.getInstance(application).favoriteDao()

    var searchQuery by mutableStateOf("")
        private set

    var selectedCategory by mutableStateOf<Category?>(null)
        private set

    var showFavoritesOnly by mutableStateOf(false)
        private set

    val favorites: StateFlow<List<FavoriteRestaurant>> = favoriteDao.getAllFavorites()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val filteredRestaurants: List<Restaurant>
        get() {
            val base = when {
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
    }

    fun updateSelectedCategory(category: Category?) {
        selectedCategory = category
        if (category != null) showFavoritesOnly = false
    }

    fun toggleFavoritesOnly() {
        showFavoritesOnly = !showFavoritesOnly
        if (showFavoritesOnly) selectedCategory = null
    }

    fun toggleFavorite(restaurantName: String) {
        viewModelScope.launch {
            val isFav = favorites.value.any { it.restaurantName == restaurantName }
            if (isFav) {
                favoriteDao.deleteByName(restaurantName)
            } else {
                favoriteDao.insert(FavoriteRestaurant(restaurantName = restaurantName))
            }
        }
    }

    fun isFavorite(restaurantName: String): Boolean {
        return favorites.value.any { it.restaurantName == restaurantName }
    }
}
