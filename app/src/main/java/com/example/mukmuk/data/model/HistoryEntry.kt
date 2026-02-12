package com.example.mukmuk.data.model

data class HistoryEntry(
    val menu: Menu,
    val timestamp: Long = System.currentTimeMillis()
)
