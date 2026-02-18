package com.example.mukmuk.ui.util

import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import com.example.mukmuk.R
import com.example.mukmuk.data.model.Category

@Composable
fun Category.localizedDisplayName(): String = when (this) {
    Category.KOREAN -> stringResource(R.string.category_korean)
    Category.CHICKEN -> stringResource(R.string.category_chicken)
    Category.JAPANESE -> stringResource(R.string.category_japanese)
    Category.CHINESE -> stringResource(R.string.category_chinese)
    Category.WESTERN -> stringResource(R.string.category_western)
    Category.SNACK -> stringResource(R.string.category_snack)
    Category.CAFE_DESSERT -> stringResource(R.string.category_cafe_dessert)
    Category.SOUTHEAST_ASIAN -> stringResource(R.string.category_southeast_asian)
}
