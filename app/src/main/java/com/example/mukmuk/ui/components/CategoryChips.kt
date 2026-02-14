package com.example.mukmuk.ui.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.mukmuk.data.model.Category
import com.example.mukmuk.ui.theme.CardBackground
import com.example.mukmuk.ui.theme.ChipBorder
import com.example.mukmuk.ui.theme.GoldAccent
import com.example.mukmuk.ui.theme.TextSecondary

@Composable
fun CategoryChips(
    categories: List<Category>,
    selectedCategories: Set<Category>,
    onToggle: (Category) -> Unit,
    onClearAll: () -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .horizontalScroll(rememberScrollState())
            .padding(horizontal = 20.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        // "전체" chip - active when no categories are selected
        FilterChip(
            selected = selectedCategories.isEmpty(),
            onClick = { onClearAll() },
            label = {
                Text(
                    text = "\uC804\uCCB4",
                    fontSize = 13.sp,
                    fontWeight = FontWeight.SemiBold
                )
            },
            shape = RoundedCornerShape(20.dp),
            border = BorderStroke(
                1.5.dp,
                if (selectedCategories.isEmpty()) GoldAccent else ChipBorder
            ),
            colors = FilterChipDefaults.filterChipColors(
                containerColor = CardBackground,
                labelColor = TextSecondary,
                selectedContainerColor = GoldAccent.copy(alpha = 0.15f),
                selectedLabelColor = GoldAccent
            )
        )

        categories.forEach { category ->
            val isSelected = category in selectedCategories
            FilterChip(
                selected = isSelected,
                onClick = { onToggle(category) },
                label = {
                    Text(
                        text = category.displayName,
                        fontSize = 13.sp,
                        fontWeight = FontWeight.SemiBold
                    )
                },
                shape = RoundedCornerShape(20.dp),
                border = BorderStroke(
                    1.5.dp,
                    if (isSelected) GoldAccent else ChipBorder
                ),
                colors = FilterChipDefaults.filterChipColors(
                    containerColor = CardBackground,
                    labelColor = TextSecondary,
                    selectedContainerColor = GoldAccent.copy(alpha = 0.15f),
                    selectedLabelColor = GoldAccent
                )
            )
        }
    }
}
