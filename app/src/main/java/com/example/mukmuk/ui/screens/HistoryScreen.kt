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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Snackbar
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
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
import com.example.mukmuk.R
import com.example.mukmuk.data.local.CategoryCount
import com.example.mukmuk.data.local.MenuCount
import com.example.mukmuk.data.model.HistoryEntry
import com.example.mukmuk.ui.HistoryViewModel
import com.example.mukmuk.ui.theme.mukmukColors
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

@Composable
fun HistoryScreen(viewModel: HistoryViewModel) {
    val historyList by viewModel.history.collectAsState(initial = emptyList())
    val topMenus by viewModel.topMenus.collectAsState(initial = emptyList())
    val categoryCounts by viewModel.categoryCounts.collectAsState(initial = emptyList())
    val totalCount by viewModel.totalCount.collectAsState(initial = 0)
    var showDeleteDialog by remember { mutableStateOf(false) }
    val context = LocalContext.current
    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()
    val deletedMessage = stringResource(R.string.history_deleted_snackbar)

    Scaffold(
        snackbarHost = {
            SnackbarHost(hostState = snackbarHostState) { data ->
                Snackbar(
                    snackbarData = data,
                    containerColor = MaterialTheme.colorScheme.inverseSurface,
                    contentColor = MaterialTheme.colorScheme.inverseOnSurface,
                    actionColor = MaterialTheme.colorScheme.inversePrimary
                )
            }
        },
        containerColor = Color.Transparent
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .background(
                    Brush.linearGradient(
                        colors = listOf(
                            MaterialTheme.colorScheme.background,
                            MaterialTheme.colorScheme.surface,
                            MaterialTheme.colorScheme.surfaceVariant
                        )
                    )
                )
        ) {
            if (historyList.isEmpty()) {
                EmptyHistoryContent()
            } else {
                Column(modifier = Modifier.fillMaxSize()) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 20.dp, vertical = 16.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = stringResource(R.string.history_title),
                            color = MaterialTheme.colorScheme.primary,
                            fontSize = 22.sp,
                            fontWeight = FontWeight.Bold
                        )
                        TextButton(onClick = { showDeleteDialog = true }) {
                            Text(
                                text = stringResource(R.string.history_delete_all),
                                color = MaterialTheme.mukmukColors.textTertiary,
                                fontSize = 13.sp
                            )
                        }
                    }

                    val grouped = groupByDate(historyList, context)

                    LazyColumn(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(horizontal = 16.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        item {
                            StatisticsCard(
                                totalCount = totalCount,
                                topMenus = topMenus,
                                categoryCounts = categoryCounts
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                        }

                        grouped.forEach { (dateLabel, entries) ->
                            item {
                                Text(
                                    text = dateLabel,
                                    color = MaterialTheme.mukmukColors.textSecondary,
                                    fontSize = 13.sp,
                                    fontWeight = FontWeight.Medium,
                                    modifier = Modifier.padding(vertical = 8.dp, horizontal = 4.dp)
                                )
                            }
                            items(entries, key = { it.id }) { entry ->
                                HistoryCard(
                                    entry = entry,
                                    onDelete = {
                                        viewModel.deleteHistoryEntry(entry.id)
                                        scope.launch {
                                            snackbarHostState.showSnackbar(message = deletedMessage)
                                        }
                                    }
                                )
                            }
                        }
                        item { Spacer(modifier = Modifier.height(16.dp)) }
                    }
                }
            }
        }
    }

    if (showDeleteDialog) {
        AlertDialog(
            onDismissRequest = { showDeleteDialog = false },
            title = { Text(stringResource(R.string.history_delete_dialog_title)) },
            text = { Text(stringResource(R.string.history_delete_dialog_message)) },
            confirmButton = {
                TextButton(onClick = {
                    viewModel.clearHistory()
                    showDeleteDialog = false
                }) {
                    Text(stringResource(R.string.delete), color = MaterialTheme.mukmukColors.error)
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
private fun StatisticsCard(
    totalCount: Int,
    topMenus: List<MenuCount>,
    categoryCounts: List<CategoryCount>
) {
    val categoryColors = listOf(
        Color(0xFFFF6B6B),
        Color(0xFF4ECDC4),
        Color(0xFF45B7D1),
        Color(0xFF96CEB4),
        Color(0xFFFECEA8),
        Color(0xFFDDA0DD),
        Color(0xFF98D8C8)
    )
    val total = categoryCounts.sumOf { it.count }.coerceAtLeast(1)

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .background(MaterialTheme.colorScheme.surface.copy(alpha = 0.85f))
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Text(
            text = stringResource(R.string.history_stats_total, totalCount),
            color = MaterialTheme.colorScheme.primary,
            fontSize = 18.sp,
            fontWeight = FontWeight.Bold
        )

        if (topMenus.isNotEmpty()) {
            Text(
                text = stringResource(R.string.history_stats_top_menus),
                color = MaterialTheme.mukmukColors.textSecondary,
                fontSize = 13.sp,
                fontWeight = FontWeight.Medium
            )
            val menuEmojis = listOf("\uD83E\uDD47", "\uD83E\uDD48", "\uD83E\uDD49")
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                topMenus.forEachIndexed { index, menu ->
                    Column(
                        modifier = Modifier
                            .weight(1f)
                            .clip(RoundedCornerShape(10.dp))
                            .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.6f))
                            .padding(8.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(text = menuEmojis.getOrElse(index) { "\uD83C\uDF7D\uFE0F" }, fontSize = 18.sp)
                        Spacer(modifier = Modifier.height(2.dp))
                        Text(
                            text = menu.menuName,
                            color = MaterialTheme.colorScheme.onBackground,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.SemiBold,
                            maxLines = 1
                        )
                        Text(
                            text = stringResource(R.string.history_stats_count, menu.count),
                            color = MaterialTheme.mukmukColors.textTertiary,
                            fontSize = 10.sp
                        )
                    }
                }
            }
        }

        if (categoryCounts.isNotEmpty()) {
            Text(
                text = stringResource(R.string.history_stats_categories),
                color = MaterialTheme.mukmukColors.textSecondary,
                fontSize = 13.sp,
                fontWeight = FontWeight.Medium
            )
            categoryCounts.forEachIndexed { index, cat ->
                val pct = (cat.count * 100 / total)
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Box(
                        modifier = Modifier
                            .size(10.dp)
                            .clip(CircleShape)
                            .background(categoryColors.getOrElse(index) { Color.Gray })
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = cat.category,
                        color = MaterialTheme.colorScheme.onBackground,
                        fontSize = 13.sp,
                        modifier = Modifier.weight(1f)
                    )
                    Text(
                        text = "$pct%",
                        color = MaterialTheme.mukmukColors.textTertiary,
                        fontSize = 12.sp
                    )
                }
            }
        }
    }
}

@Composable
private fun EmptyHistoryContent() {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(text = "\uD83D\uDCCB", fontSize = 48.sp)
            Spacer(modifier = Modifier.height(12.dp))
            Text(
                text = stringResource(R.string.history_empty_title),
                color = MaterialTheme.colorScheme.primary,
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = stringResource(R.string.history_empty_subtitle),
                color = MaterialTheme.mukmukColors.textTertiary,
                fontSize = 14.sp
            )
        }
    }
}

@Composable
private fun HistoryCard(entry: HistoryEntry, onDelete: () -> Unit) {
    val timeFormat = remember { SimpleDateFormat("HH:mm", Locale.getDefault()) }

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .background(MaterialTheme.colorScheme.surface.copy(alpha = 0.7f))
            .padding(horizontal = 16.dp, vertical = 14.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(44.dp)
                .clip(RoundedCornerShape(10.dp))
                .background(Color(entry.color.toULong()).copy(alpha = 0.2f)),
            contentAlignment = Alignment.Center
        ) {
            Text(text = entry.emoji, fontSize = 22.sp)
        }
        Spacer(modifier = Modifier.width(14.dp))
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = entry.menuName,
                color = MaterialTheme.colorScheme.onBackground,
                fontSize = 16.sp,
                fontWeight = FontWeight.SemiBold
            )
            Text(
                text = entry.category,
                color = MaterialTheme.mukmukColors.textTertiary,
                fontSize = 12.sp
            )
        }
        Text(
            text = timeFormat.format(Date(entry.timestamp)),
            color = MaterialTheme.mukmukColors.textTertiary,
            fontSize = 12.sp
        )
        Spacer(modifier = Modifier.width(4.dp))
        IconButton(
            onClick = onDelete,
            modifier = Modifier.size(32.dp)
        ) {
            Text(text = "\uD83D\uDDD1\uFE0F", fontSize = 16.sp)
        }
    }
}

private fun groupByDate(entries: List<HistoryEntry>, context: android.content.Context): List<Pair<String, List<HistoryEntry>>> {
    val today = Calendar.getInstance()
    val yesterday = Calendar.getInstance().apply { add(Calendar.DAY_OF_YEAR, -1) }
    val dateFormat = SimpleDateFormat("M월 d일 (E)", Locale.KOREAN)
    val todayLabel = context.getString(R.string.history_today)
    val yesterdayLabel = context.getString(R.string.history_yesterday)

    return entries.groupBy { entry ->
        val cal = Calendar.getInstance().apply { timeInMillis = entry.timestamp }
        when {
            isSameDay(cal, today) -> todayLabel
            isSameDay(cal, yesterday) -> yesterdayLabel
            else -> dateFormat.format(Date(entry.timestamp))
        }
    }.toList()
}

private fun isSameDay(a: Calendar, b: Calendar): Boolean {
    return a.get(Calendar.YEAR) == b.get(Calendar.YEAR) &&
            a.get(Calendar.DAY_OF_YEAR) == b.get(Calendar.DAY_OF_YEAR)
}
