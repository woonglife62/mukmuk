package com.example.mukmuk.data.repository

import com.example.mukmuk.data.local.CategoryCount
import com.example.mukmuk.data.local.HistoryDao
import com.example.mukmuk.data.local.MenuCount
import com.example.mukmuk.data.model.HistoryEntry
import kotlinx.coroutines.flow.Flow

class HistoryRepository(private val historyDao: HistoryDao) {
    val allHistory: Flow<List<HistoryEntry>> = historyDao.getAllHistory()
    val topMenus: Flow<List<MenuCount>> = historyDao.getTopMenus()
    val categoryCounts: Flow<List<CategoryCount>> = historyDao.getCategoryCounts()
    val totalCount: Flow<Int> = historyDao.getTotalCount()

    suspend fun insert(entry: HistoryEntry) {
        historyDao.insert(entry)
    }

    suspend fun deleteAll() {
        historyDao.deleteAll()
    }

    suspend fun deleteById(id: Long) = historyDao.deleteById(id)
}
