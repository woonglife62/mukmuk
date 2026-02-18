package com.example.mukmuk.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.clickable
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import android.app.TimePickerDialog
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.mukmuk.BuildConfig
import com.example.mukmuk.R
import com.example.mukmuk.notification.NotificationScheduler
import com.example.mukmuk.ui.RouletteViewModel
import com.example.mukmuk.ui.theme.mukmukColors

@Composable
fun SettingsScreen(viewModel: RouletteViewModel) {
    val hapticEnabled by viewModel.hapticEnabled.collectAsState()
    val soundEnabled by viewModel.soundEnabled.collectAsState()
    val darkTheme by viewModel.darkTheme.collectAsState()
    val notificationEnabled by viewModel.notificationEnabled.collectAsState()
    val notificationHour by viewModel.notificationHour.collectAsState()
    val notificationMinute by viewModel.notificationMinute.collectAsState()
    val searchRadius by viewModel.searchRadius.collectAsState()
    var showDeleteDialog by remember { mutableStateOf(false) }
    val context = LocalContext.current
    val colorScheme = MaterialTheme.colorScheme
    val extColors = MaterialTheme.mukmukColors

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.linearGradient(
                    colors = listOf(
                        colorScheme.background,
                        colorScheme.surface,
                        colorScheme.surfaceVariant
                    )
                )
            )
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 20.dp)
    ) {
        Spacer(modifier = Modifier.height(24.dp))

        Text(
            text = stringResource(R.string.settings_title),
            color = colorScheme.primary,
            fontSize = 22.sp,
            fontWeight = FontWeight.Bold
        )

        Spacer(modifier = Modifier.height(24.dp))

        // General section
        SectionHeader(title = stringResource(R.string.settings_section_general))
        Spacer(modifier = Modifier.height(8.dp))

        SettingsToggleItem(
            icon = "\uD83D\uDCF3",
            title = stringResource(R.string.settings_haptic_title),
            subtitle = stringResource(R.string.settings_haptic_subtitle),
            checked = hapticEnabled,
            onCheckedChange = { viewModel.setHapticEnabled(it) }
        )

        Spacer(modifier = Modifier.height(8.dp))

        SettingsToggleItem(
            icon = "\uD83D\uDD14",
            title = stringResource(R.string.settings_sound_title),
            subtitle = stringResource(R.string.settings_sound_subtitle),
            checked = soundEnabled,
            onCheckedChange = { viewModel.setSoundEnabled(it) }
        )

        Spacer(modifier = Modifier.height(8.dp))

        SettingsToggleItem(
            icon = "\uD83C\uDF19",
            title = stringResource(R.string.settings_dark_mode_title),
            subtitle = stringResource(R.string.settings_dark_mode_subtitle),
            checked = darkTheme,
            onCheckedChange = { viewModel.setDarkTheme(it) }
        )

        Spacer(modifier = Modifier.height(24.dp))

        // Search settings section
        SectionHeader(title = "검색 설정")
        Spacer(modifier = Modifier.height(8.dp))

        val radiusOptions = listOf(500 to "500m", 1000 to "1km", 2000 to "2km", 3000 to "3km", 5000 to "5km")
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(12.dp))
                .background(MaterialTheme.colorScheme.surface.copy(alpha = 0.7f))
                .padding(horizontal = 16.dp, vertical = 14.dp)
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(modifier = Modifier.size(36.dp), contentAlignment = Alignment.Center) {
                    Text(text = "\uD83D\uDCCD", fontSize = 20.sp)
                }
                Spacer(modifier = Modifier.width(12.dp))
                Text(
                    text = "검색 반경",
                    color = colorScheme.onSurface,
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Medium
                )
            }
            Spacer(modifier = Modifier.height(12.dp))
            Row(
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                radiusOptions.forEach { (value, label) ->
                    val selected = searchRadius == value
                    Surface(
                        shape = RoundedCornerShape(20.dp),
                        color = if (selected) colorScheme.primary else colorScheme.surfaceVariant,
                        modifier = Modifier.clickable { viewModel.setSearchRadius(value) }
                    ) {
                        Text(
                            text = label,
                            modifier = Modifier.padding(horizontal = 14.dp, vertical = 8.dp),
                            color = if (selected) colorScheme.onPrimary else colorScheme.onSurfaceVariant,
                            fontSize = 13.sp,
                            fontWeight = if (selected) FontWeight.Bold else FontWeight.Normal
                        )
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        // Notification section
        SectionHeader(title = stringResource(R.string.settings_section_notification))
        Spacer(modifier = Modifier.height(8.dp))

        SettingsToggleItem(
            icon = "\uD83D\uDD14",
            title = stringResource(R.string.settings_notification_title),
            subtitle = stringResource(R.string.settings_notification_subtitle),
            checked = notificationEnabled,
            onCheckedChange = { enabled ->
                viewModel.setNotificationEnabled(enabled)
                if (enabled) {
                    NotificationScheduler.scheduleLunchReminder(context, notificationHour, notificationMinute)
                } else {
                    NotificationScheduler.cancelLunchReminder(context)
                }
            }
        )

        Spacer(modifier = Modifier.height(8.dp))

        SettingsActionItem(
            icon = "\uD83D\uDD50",
            title = stringResource(R.string.settings_notification_time_title),
            subtitle = String.format("%02d:%02d", notificationHour, notificationMinute),
            actionLabel = stringResource(R.string.settings_action_change),
            actionColor = colorScheme.primary,
            onClick = {
                TimePickerDialog(
                    context,
                    { _, selectedHour, selectedMinute ->
                        viewModel.setNotificationTime(selectedHour, selectedMinute)
                        if (notificationEnabled) {
                            NotificationScheduler.scheduleLunchReminder(context, selectedHour, selectedMinute)
                        }
                    },
                    notificationHour,
                    notificationMinute,
                    true
                ).show()
            }
        )

        Spacer(modifier = Modifier.height(24.dp))

        // Data section
        SectionHeader(title = stringResource(R.string.settings_section_data))
        Spacer(modifier = Modifier.height(8.dp))

        SettingsActionItem(
            icon = "\uD83D\uDDD1\uFE0F",
            title = stringResource(R.string.settings_clear_history_title),
            subtitle = stringResource(R.string.settings_clear_history_subtitle),
            actionLabel = stringResource(R.string.settings_clear_history_button),
            actionColor = extColors.error,
            onClick = { showDeleteDialog = true }
        )

        Spacer(modifier = Modifier.height(24.dp))

        // Info section
        SectionHeader(title = stringResource(R.string.settings_section_info))
        Spacer(modifier = Modifier.height(8.dp))

        SettingsInfoItem(
            icon = "\uD83D\uDCF1",
            title = stringResource(R.string.settings_version_title),
            value = BuildConfig.VERSION_NAME
        )

        Spacer(modifier = Modifier.height(8.dp))

        SettingsInfoItem(
            icon = "\uD83C\uDF7D\uFE0F",
            title = stringResource(R.string.settings_app_title),
            value = stringResource(R.string.settings_app_subtitle)
        )

        Spacer(modifier = Modifier.height(100.dp))
    }

    if (showDeleteDialog) {
        AlertDialog(
            onDismissRequest = { showDeleteDialog = false },
            title = { Text(stringResource(R.string.settings_clear_dialog_title)) },
            text = { Text(stringResource(R.string.settings_clear_dialog_message)) },
            confirmButton = {
                TextButton(onClick = {
                    viewModel.clearHistory()
                    showDeleteDialog = false
                }) {
                    Text(stringResource(R.string.delete), color = extColors.error)
                }
            },
            dismissButton = {
                TextButton(onClick = { showDeleteDialog = false }) {
                    Text(stringResource(R.string.cancel))
                }
            }
        )
    }
}

@Composable
private fun SectionHeader(title: String) {
    Text(
        text = title,
        color = MaterialTheme.colorScheme.primary,
        fontSize = 14.sp,
        fontWeight = FontWeight.SemiBold,
        modifier = Modifier.padding(horizontal = 4.dp)
    )
}

@Composable
private fun SettingsToggleItem(
    icon: String,
    title: String,
    subtitle: String,
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit
) {
    val colorScheme = MaterialTheme.colorScheme
    val extColors = MaterialTheme.mukmukColors
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .background(colorScheme.surface.copy(alpha = 0.7f))
            .padding(horizontal = 16.dp, vertical = 14.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier.size(36.dp),
            contentAlignment = Alignment.Center
        ) {
            Text(text = icon, fontSize = 20.sp)
        }
        Spacer(modifier = Modifier.width(12.dp))
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = title,
                color = colorScheme.onSurface,
                fontSize = 15.sp,
                fontWeight = FontWeight.Medium
            )
            Text(
                text = subtitle,
                color = extColors.textTertiary,
                fontSize = 12.sp
            )
        }
        Switch(
            checked = checked,
            onCheckedChange = onCheckedChange,
            colors = SwitchDefaults.colors(
                checkedThumbColor = colorScheme.background,
                checkedTrackColor = colorScheme.primary,
                uncheckedThumbColor = extColors.textSecondary,
                uncheckedTrackColor = colorScheme.surfaceVariant
            )
        )
    }
}

@Composable
private fun SettingsActionItem(
    icon: String,
    title: String,
    subtitle: String,
    actionLabel: String,
    actionColor: Color,
    onClick: () -> Unit
) {
    val colorScheme = MaterialTheme.colorScheme
    val extColors = MaterialTheme.mukmukColors
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .background(colorScheme.surface.copy(alpha = 0.7f))
            .padding(horizontal = 16.dp, vertical = 14.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier.size(36.dp),
            contentAlignment = Alignment.Center
        ) {
            Text(text = icon, fontSize = 20.sp)
        }
        Spacer(modifier = Modifier.width(12.dp))
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = title,
                color = colorScheme.onSurface,
                fontSize = 15.sp,
                fontWeight = FontWeight.Medium
            )
            Text(
                text = subtitle,
                color = extColors.textTertiary,
                fontSize = 12.sp
            )
        }
        TextButton(onClick = onClick) {
            Text(text = actionLabel, color = actionColor, fontSize = 13.sp)
        }
    }
}

@Composable
private fun SettingsInfoItem(
    icon: String,
    title: String,
    value: String
) {
    val colorScheme = MaterialTheme.colorScheme
    val extColors = MaterialTheme.mukmukColors
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .background(colorScheme.surface.copy(alpha = 0.7f))
            .padding(horizontal = 16.dp, vertical = 14.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Box(
                modifier = Modifier.size(36.dp),
                contentAlignment = Alignment.Center
            ) {
                Text(text = icon, fontSize = 20.sp)
            }
            Spacer(modifier = Modifier.width(12.dp))
            Text(
                text = title,
                color = colorScheme.onSurface,
                fontSize = 15.sp,
                fontWeight = FontWeight.Medium
            )
        }
        Text(
            text = value,
            color = extColors.textTertiary,
            fontSize = 14.sp
        )
    }
}
