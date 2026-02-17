package com.example.mukmuk.ui

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.mukmuk.data.repository.SettingsRepository
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class SettingsViewModel(application: Application) : AndroidViewModel(application) {

    private val settingsRepository = SettingsRepository(application)

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
}
