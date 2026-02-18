package com.example.mukmuk.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.mukmuk.data.model.VisitRecord
import com.example.mukmuk.ui.HistoryViewModel
import com.example.mukmuk.ui.VisitFilter
import com.example.mukmuk.ui.theme.mukmukColors
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

private val CATEGORY_LABELS = mapOf(
    "KOREAN" to "\uD83C\uDDF0\uD83C\uDDF7 한식",
    "JAPANESE" to "\uD83C\uDDEF\uD83C\uDDF5 일식",
    "CHINESE" to "\uD83C\uDDE8\uD83C\uDDF3 중식",
    "WESTERN" to "\uD83C\uDF55 양식",
    "SNACK" to "\uD83C\uDF62 분식",
    "SOUTHEAST_ASIAN" to "\uD83C\uDF5C 동남아"
)

private fun categoryLabel(category: String): String =
    CATEGORY_LABELS[category] ?: if (category.isBlank()) "기타" else category

@Composable
fun HistoryScreen(viewModel: HistoryViewModel) {
    val filteredRecords by viewModel.filteredRecords.collectAsState(initial = emptyList())
    val totalCount by viewModel.totalCount.collectAsState(initial = 0)
    val visitedCount by viewModel.visitedCount.collectAsState(initial = 0)
    val selectedFilter by viewModel.selectedFilter.collectAsState()
    val selectedCategory by viewModel.selectedCategory.collectAsState()

    Box(
        modifier = Modifier
            .fillMaxSize()
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
        Column(modifier = Modifier.fillMaxSize()) {
            // Header
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp, vertical = 16.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "나의 맛집 기록",
                    color = MaterialTheme.colorScheme.primary,
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold
                )
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = "총 $totalCount",
                        color = MaterialTheme.mukmukColors.textSecondary,
                        fontSize = 13.sp
                    )
                    Text(
                        text = " | ",
                        color = MaterialTheme.mukmukColors.textTertiary,
                        fontSize = 13.sp
                    )
                    Text(
                        text = "방문 $visitedCount",
                        color = MaterialTheme.colorScheme.primary,
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Medium
                    )
                }
            }

            // Visit filter chips
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                VisitFilter.values().forEach { filter ->
                    val isSelected = selectedFilter == filter
                    val label = when (filter) {
                        VisitFilter.ALL -> "전체"
                        VisitFilter.VISITED -> "\u2705 방문완료"
                        VisitFilter.NOT_VISITED -> "\uD83D\uDCCC 가볼곳"
                    }
                    FilterChip(
                        label = label,
                        selected = isSelected,
                        onClick = { viewModel.setFilter(filter) }
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Category filter chips (horizontally scrollable)
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .horizontalScroll(rememberScrollState())
                    .padding(horizontal = 16.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                FilterChip(
                    label = "전체",
                    selected = selectedCategory == null,
                    onClick = { viewModel.setCategory(null) }
                )
                CATEGORY_LABELS.entries.forEach { (key, label) ->
                    FilterChip(
                        label = label,
                        selected = selectedCategory == key,
                        onClick = { viewModel.setCategory(key) }
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            if (filteredRecords.isEmpty()) {
                EmptyVisitContent()
            } else {
                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(horizontal = 16.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(filteredRecords, key = { it.id }) { record ->
                        VisitRecordCard(
                            record = record,
                            onToggleVisited = { viewModel.toggleVisited(record.id) },
                            onRatingChange = { rating -> viewModel.updateRating(record.id, rating) },
                            onDelete = { viewModel.deleteRecord(record.id) }
                        )
                    }
                    item { Spacer(modifier = Modifier.height(16.dp)) }
                }
            }
        }
    }
}

@Composable
private fun FilterChip(
    label: String,
    selected: Boolean,
    onClick: () -> Unit
) {
    val bgColor = if (selected) MaterialTheme.colorScheme.primary else MaterialTheme.mukmukColors.cardBackground
    val textColor = if (selected) MaterialTheme.colorScheme.onPrimary else MaterialTheme.mukmukColors.textSecondary

    Surface(
        shape = RoundedCornerShape(20.dp),
        color = bgColor,
        shadowElevation = if (selected) 2.dp else 0.dp,
        modifier = Modifier.clickable(onClick = onClick)
    ) {
        Text(
            text = label,
            color = textColor,
            fontSize = 13.sp,
            fontWeight = if (selected) FontWeight.SemiBold else FontWeight.Normal,
            modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp)
        )
    }
}

@Composable
private fun VisitRecordCard(
    record: VisitRecord,
    onToggleVisited: () -> Unit,
    onRatingChange: (Float) -> Unit,
    onDelete: () -> Unit
) {
    val dateFormat = SimpleDateFormat("yyyy.MM.dd", Locale.getDefault())

    Surface(
        shape = RoundedCornerShape(16.dp),
        color = MaterialTheme.colorScheme.surface,
        shadowElevation = 2.dp,
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Visit status toggle
                Text(
                    text = if (record.visited) "\u2705" else "\uD83D\uDCCC",
                    fontSize = 22.sp,
                    modifier = Modifier.clickable(onClick = onToggleVisited)
                )
                Spacer(modifier = Modifier.width(10.dp))
                // Restaurant name
                Text(
                    text = record.restaurantName,
                    color = MaterialTheme.colorScheme.onSurface,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.weight(1f)
                )
                // Category badge
                if (record.category.isNotBlank()) {
                    Surface(
                        shape = RoundedCornerShape(8.dp),
                        color = MaterialTheme.colorScheme.surfaceVariant
                    ) {
                        Text(
                            text = categoryLabel(record.category),
                            color = MaterialTheme.mukmukColors.textSecondary,
                            fontSize = 11.sp,
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                        )
                    }
                }
            }

            if (record.address.isNotBlank()) {
                Spacer(modifier = Modifier.height(6.dp))
                Text(
                    text = "\uD83D\uDCCD ${record.address}",
                    color = MaterialTheme.mukmukColors.textTertiary,
                    fontSize = 12.sp
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Star rating
            StarRatingBar(
                rating = record.myRating,
                onRatingChange = onRatingChange
            )

            Spacer(modifier = Modifier.height(8.dp))

            // Date + delete
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                val displayDate = if (record.visited && record.visitDate != null) {
                    dateFormat.format(Date(record.visitDate))
                } else {
                    dateFormat.format(Date(record.createdAt))
                }
                Text(
                    text = displayDate,
                    color = MaterialTheme.mukmukColors.textTertiary,
                    fontSize = 12.sp
                )
                Text(
                    text = "\uD83D\uDDD1\uFE0F",
                    fontSize = 18.sp,
                    modifier = Modifier.clickable(onClick = onDelete)
                )
            }
        }
    }
}

@Composable
private fun StarRatingBar(
    rating: Float,
    onRatingChange: (Float) -> Unit,
    modifier: Modifier = Modifier
) {
    Row(modifier = modifier, verticalAlignment = Alignment.CenterVertically) {
        for (i in 1..5) {
            Text(
                text = if (i <= rating) "\u2B50" else "\u2606",
                fontSize = 20.sp,
                modifier = Modifier.clickable { onRatingChange(i.toFloat()) }
            )
        }
        if (rating > 0) {
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = String.format("%.1f", rating),
                fontSize = 14.sp,
                color = MaterialTheme.colorScheme.primary
            )
        }
    }
}

@Composable
private fun EmptyVisitContent() {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(text = "\uD83C\uDF7D\uFE0F", fontSize = 48.sp)
            Spacer(modifier = Modifier.height(12.dp))
            Text(
                text = "아직 기록된 맛집이 없어요",
                color = MaterialTheme.colorScheme.primary,
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = "맛집을 검색하고 기록을 시작해보세요!",
                color = MaterialTheme.mukmukColors.textTertiary,
                fontSize = 14.sp
            )
        }
    }
}
