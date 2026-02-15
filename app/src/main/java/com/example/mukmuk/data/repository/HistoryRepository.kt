package com.example.mukmuk.data.repository

import com.example.mukmuk.data.local.HistoryDao
import com.example.mukmuk.data.model.HistoryEntry
import kotlinx.coroutines.flow.Flow

class HistoryRepository(private val historyDao: HistoryDao) {
    val allHistory: Flow<List<HistoryEntry>> = historyDao.getAllHistory()

    suspend fun insert(entry: HistoryEntry) {
        historyDao.insert(entry)
    }

    suspend fun deleteAll() {
        historyDao.deleteAll()
    }
}
