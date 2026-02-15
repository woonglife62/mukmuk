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
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.mukmuk.ui.RouletteViewModel
import com.example.mukmuk.ui.theme.DarkBackground
import com.example.mukmuk.ui.theme.DarkSurface
import com.example.mukmuk.ui.theme.DarkSurfaceVariant
import com.example.mukmuk.ui.theme.GoldAccent
import com.example.mukmuk.ui.theme.TextPrimary
import com.example.mukmuk.ui.theme.TextSecondary
import com.example.mukmuk.ui.theme.TextTertiary

@Composable
fun SettingsScreen(viewModel: RouletteViewModel) {
    val hapticEnabled by viewModel.hapticEnabled.collectAsState()
    val soundEnabled by viewModel.soundEnabled.collectAsState()
    val darkTheme by viewModel.darkTheme.collectAsState()
    var showDeleteDialog by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.linearGradient(
                    colors = listOf(DarkBackground, DarkSurface, DarkSurfaceVariant)
                )
            )
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 20.dp)
    ) {
        Spacer(modifier = Modifier.height(24.dp))

        Text(
            text = "설정",
            color = GoldAccent,
            fontSize = 22.sp,
            fontWeight = FontWeight.Bold
        )

        Spacer(modifier = Modifier.height(24.dp))

        // General section
        SectionHeader(title = "일반")
        Spacer(modifier = Modifier.height(8.dp))

        SettingsToggleItem(
            icon = "📳",
            title = "햅틱 피드백",
            subtitle = "룰렛 조작 시 진동 피드백",
            checked = hapticEnabled,
            onCheckedChange = { viewModel.setHapticEnabled(it) }
        )

        Spacer(modifier = Modifier.height(8.dp))

        SettingsToggleItem(
            icon = "🔔",
            title = "사운드 효과",
            subtitle = "룰렛 회전 시 효과음",
            checked = soundEnabled,
            onCheckedChange = { viewModel.setSoundEnabled(it) }
        )

        Spacer(modifier = Modifier.height(8.dp))

        SettingsToggleItem(
            icon = "🌙",
            title = "다크 모드",
            subtitle = "어두운 테마 사용 (라이트 모드 준비 중)",
            checked = darkTheme,
            onCheckedChange = { viewModel.setDarkTheme(it) }
        )

        Spacer(modifier = Modifier.height(24.dp))

        // Data section
        SectionHeader(title = "데이터")
        Spacer(modifier = Modifier.height(8.dp))

        SettingsActionItem(
            icon = "🗑️",
            title = "기록 초기화",
            subtitle = "모든 선택 기록을 삭제합니다",
            actionColor = Color(0xFFFF6B6B),
            onClick = { showDeleteDialog = true }
        )

        Spacer(modifier = Modifier.height(24.dp))

        // Info section
        SectionHeader(title = "정보")
        Spacer(modifier = Modifier.height(8.dp))

        SettingsInfoItem(
            icon = "📱",
            title = "앱 버전",
            value = "1.0"
        )

        Spacer(modifier = Modifier.height(8.dp))

        SettingsInfoItem(
            icon = "🍽️",
            title = "먹먹",
            value = "오늘 뭐 먹지?"
        )

        Spacer(modifier = Modifier.height(100.dp))
    }

    if (showDeleteDialog) {
        AlertDialog(
            onDismissRequest = { showDeleteDialog = false },
            title = { Text("기록 초기화") },
            text = { Text("모든 선택 기록을 삭제하시겠습니까?\n이 작업은 되돌릴 수 없습니다.") },
            confirmButton = {
                TextButton(onClick = {
                    viewModel.clearHistory()
                    showDeleteDialog = false
                }) {
                    Text("삭제", color = Color(0xFFFF6B6B))
                }
            },
            dismissButton = {
                TextButton(onClick = { showDeleteDialog = false }) {
                    Text("취소")
                }
            }
        )
    }
}

@Composable
private fun SectionHeader(title: String) {
    Text(
        text = title,
        color = GoldAccent,
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
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .background(DarkSurface.copy(alpha = 0.7f))
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
                color = TextPrimary,
                fontSize = 15.sp,
                fontWeight = FontWeight.Medium
            )
            Text(
                text = subtitle,
                color = TextTertiary,
                fontSize = 12.sp
            )
        }
        Switch(
            checked = checked,
            onCheckedChange = onCheckedChange,
            colors = SwitchDefaults.colors(
                checkedThumbColor = DarkBackground,
                checkedTrackColor = GoldAccent,
                uncheckedThumbColor = TextSecondary,
                uncheckedTrackColor = DarkSurfaceVariant
            )
        )
    }
}

@Composable
private fun SettingsActionItem(
    icon: String,
    title: String,
    subtitle: String,
    actionColor: Color,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .background(DarkSurface.copy(alpha = 0.7f))
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
                color = TextPrimary,
                fontSize = 15.sp,
                fontWeight = FontWeight.Medium
            )
            Text(
                text = subtitle,
                color = TextTertiary,
                fontSize = 12.sp
            )
        }
        TextButton(onClick = onClick) {
            Text(text = "초기화", color = actionColor, fontSize = 13.sp)
        }
    }
}

@Composable
private fun SettingsInfoItem(
    icon: String,
    title: String,
    value: String
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .background(DarkSurface.copy(alpha = 0.7f))
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
                color = TextPrimary,
                fontSize = 15.sp,
                fontWeight = FontWeight.Medium
            )
        }
        Text(
            text = value,
            color = TextTertiary,
            fontSize = 14.sp
        )
    }
}
