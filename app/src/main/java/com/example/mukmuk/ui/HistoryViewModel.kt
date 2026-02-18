package com.example.mukmuk.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.mukmuk.data.local.CategoryCount
import com.example.mukmuk.data.local.VisitRecordDao
import com.example.mukmuk.data.model.VisitRecord
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

enum class VisitFilter(val label: String) {
    ALL("전체"),
    VISITED("방문완료"),
    NOT_VISITED("가볼곳")
}

class HistoryViewModel(
    private val visitRecordDao: VisitRecordDao
) : ViewModel() {

    private val _selectedFilter = MutableStateFlow(VisitFilter.ALL)
    val selectedFilter: StateFlow<VisitFilter> = _selectedFilter

    private val _selectedCategory = MutableStateFlow<String?>(null)
    val selectedCategory: StateFlow<String?> = _selectedCategory

    val filteredRecords: Flow<List<VisitRecord>> = combine(
        _selectedFilter,
        _selectedCategory,
        visitRecordDao.getAllRecords()
    ) { filter, category, allList ->
        var result = allList
        when (filter) {
            VisitFilter.VISITED -> result = result.filter { it.visited }
            VisitFilter.NOT_VISITED -> result = result.filter { !it.visited }
            VisitFilter.ALL -> { /* no filter */ }
        }
        if (category != null) {
            result = result.filter { it.category == category }
        }
        result
    }

    val totalCount: Flow<Int> = visitRecordDao.getTotalCount()
    val visitedCount: Flow<Int> = visitRecordDao.getVisitedCount()
    val categoryCounts: Flow<List<CategoryCount>> = visitRecordDao.getCategoryCounts()

    fun setFilter(filter: VisitFilter) {
        _selectedFilter.value = filter
    }

    fun setCategory(category: String?) {
        _selectedCategory.value = category
    }

    fun updateRating(id: Long, rating: Float) {
        viewModelScope.launch {
            val list = visitRecordDao.getAllRecords().first()
            list.find { it.id == id }?.let { record ->
                visitRecordDao.update(record.copy(myRating = rating))
            }
        }
    }

    fun toggleVisited(id: Long) {
        viewModelScope.launch {
            val list = visitRecordDao.getAllRecords().first()
            list.find { it.id == id }?.let { record ->
                visitRecordDao.update(
                    record.copy(
                        visited = !record.visited,
                        visitDate = if (!record.visited) System.currentTimeMillis() else null
                    )
                )
            }
        }
    }

    fun deleteRecord(id: Long) {
        viewModelScope.launch {
            visitRecordDao.deleteById(id)
        }
    }

    fun addRecord(record: VisitRecord) {
        viewModelScope.launch {
            visitRecordDao.insert(record)
        }
    }
}
