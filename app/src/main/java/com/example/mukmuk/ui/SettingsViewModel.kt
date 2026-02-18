package com.example.mukmuk.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.mukmuk.data.repository.SettingsRepository
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class SettingsViewModel(
    private val settingsRepository: SettingsRepository
) : ViewModel() {

    val hapticEnabled = settingsRepository.hapticEnabled
        .stateIn(viewModelScope, SharingStarted.Eagerly, true)

    val soundEnabled = settingsRepository.soundEnabled
        .stateIn(viewModelScope, SharingStarted.Eagerly, true)

    val darkTheme = settingsRepository.darkTheme
        .stateIn(viewModelScope, SharingStarted.Eagerly, true)

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
}
