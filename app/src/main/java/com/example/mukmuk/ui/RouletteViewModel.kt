package com.example.mukmuk.ui

import android.app.Application
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.mukmuk.data.local.AppDatabase
import com.example.mukmuk.data.model.Category
import com.example.mukmuk.data.model.HistoryEntry
import com.example.mukmuk.data.model.Menu
import com.example.mukmuk.data.repository.HistoryRepository
import com.example.mukmuk.data.repository.MenuRepository
import com.example.mukmuk.data.repository.RestaurantRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch

class RouletteViewModel(application: Application) : AndroidViewModel(application) {

    private val historyRepository: HistoryRepository

    init {
        val dao = AppDatabase.getInstance(application).historyDao()
        historyRepository = HistoryRepository(dao)
    }

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
            viewModelScope.launch {
                historyRepository.insert(HistoryEntry.fromMenu(menu))
            }
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

    fun clearHistory() {
        viewModelScope.launch {
            historyRepository.deleteAll()
        }
    }
}
