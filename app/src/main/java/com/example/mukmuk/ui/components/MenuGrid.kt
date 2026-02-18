package com.example.mukmuk.ui.components

import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.mukmuk.data.model.Menu
import com.example.mukmuk.ui.theme.mukmukColors

@Composable
fun MenuGrid(
    menus: List<Menu>,
    modifier: Modifier = Modifier
) {
    val extColors = MaterialTheme.mukmukColors
    Column(modifier = modifier.padding(horizontal = 24.dp)) {
        Text(
            text = "\uD83D\uDCA1 TIP: \uCE74\uD14C\uACE0\uB9AC \uD544\uD130\uB85C \uC6D0\uD558\uB294 \uC74C\uC2DD\uB9CC \uACE8\uB77C\uBCF4\uC138\uC694",
            color = extColors.textHint,
            fontSize = 12.sp,
            modifier = Modifier.padding(bottom = 10.dp)
        )
        val displayMenus = menus.take(8)
        val rows = displayMenus.chunked(4)
        rows.forEachIndexed { rowIndex, rowMenus ->
            Row(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                rowMenus.forEach { menu ->
                    Surface(
                        shape = RoundedCornerShape(12.dp),
                        color = extColors.cardBackground,
                        modifier = Modifier
                            .weight(1f)
                            .border(1.dp, extColors.cardBorder, RoundedCornerShape(12.dp))
                    ) {
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            modifier = Modifier.padding(vertical = 10.dp, horizontal = 4.dp)
                        ) {
                            Text(text = menu.emoji, fontSize = 22.sp)
                            Text(
                                text = menu.name,
                                color = extColors.textSecondary,
                                fontSize = 10.sp,
                                textAlign = TextAlign.Center,
                                modifier = Modifier.padding(top = 2.dp)
                            )
                        }
                    }
                }
                // Fill remaining cells with spacers if row is not full
                repeat(4 - rowMenus.size) {
                    Spacer(modifier = Modifier.weight(1f))
                }
            }
            if (rowIndex < rows.lastIndex) {
                Spacer(modifier = Modifier.height(8.dp))
            }
        }
    }
}
