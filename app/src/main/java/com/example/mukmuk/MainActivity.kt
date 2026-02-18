package com.example.mukmuk

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import com.example.mukmuk.ui.theme.MukmukTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        val app = application as MukmukApplication
        val settingsRepository = app.container.settingsRepository
        setContent {
            val darkTheme by settingsRepository.darkTheme.collectAsState(initial = true)
            MukmukTheme(darkTheme = darkTheme) {
                MukmukApp()
            }
        }
    }
}
