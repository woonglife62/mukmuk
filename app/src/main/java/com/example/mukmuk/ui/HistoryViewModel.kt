package com.example.mukmuk.ui

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.mukmuk.data.local.AppDatabase
import com.example.mukmuk.data.local.CategoryCount
import com.example.mukmuk.data.local.MenuCount
import com.example.mukmuk.data.model.HistoryEntry
import com.example.mukmuk.data.repository.HistoryRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch

class HistoryViewModel(application: Application) : AndroidViewModel(application) {

    private val historyRepository: HistoryRepository

    init {
        val dao = AppDatabase.getInstance(application).historyDao()
        historyRepository = HistoryRepository(dao)
    }

    val history: Flow<List<HistoryEntry>> = historyRepository.allHistory
    val topMenus: Flow<List<MenuCount>> = historyRepository.topMenus
    val categoryCounts: Flow<List<CategoryCount>> = historyRepository.categoryCounts
    val totalCount: Flow<Int> = historyRepository.totalCount

    fun clearHistory() {
        viewModelScope.launch {
            historyRepository.deleteAll()
        }
    }

    fun deleteHistoryEntry(id: Long) {
        viewModelScope.launch { historyRepository.deleteById(id) }
    }
}
