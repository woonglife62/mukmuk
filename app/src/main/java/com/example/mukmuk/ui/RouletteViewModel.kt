package com.example.mukmuk.ui

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import com.example.mukmuk.data.model.Category
import com.example.mukmuk.data.model.HistoryEntry
import com.example.mukmuk.data.model.Menu
import com.example.mukmuk.data.repository.MenuRepository
import com.example.mukmuk.data.repository.RestaurantRepository

class RouletteViewModel : ViewModel() {
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

    private val _history = mutableListOf<HistoryEntry>()
    val history: List<HistoryEntry> get() = _history.toList()

    var showConfirmSnackbar by mutableStateOf(false)
        private set

    val categories = Category.entries.toList()

    val filteredMenus: List<Menu>
        get() = MenuRepository.getFilteredMenus(selectedCategories)

    val restaurants
        get() = selectedMenu?.let { RestaurantRepository.getRestaurants(it.name) } ?: emptyList()

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

    fun onSpinComplete(finalAngle: Float) {
        val menus = filteredMenus
        if (menus.isEmpty()) return
        val normalized = ((finalAngle % 360f) + 360f) % 360f
        val arc = 360f / menus.size
        val index = ((360f - normalized + arc / 2f) % 360f / arc).toInt() % menus.size
        selectedMenu = menus[index]
        isSpinning = false
    }

    fun showResultScreen() {
        showResult = true
    }

    fun resetToWheel() {
        showResult = false
        selectedMenu = null
    }

    fun confirmSelection() {
        selectedMenu?.let { menu ->
            _history.add(HistoryEntry(menu = menu))
            showConfirmSnackbar = true
            showResult = false
            selectedMenu = null
        }
    }

    fun dismissSnackbar() {
        showConfirmSnackbar = false
    }

    fun clearAllCategories() {
        selectedCategories = emptySet()
    }
}
