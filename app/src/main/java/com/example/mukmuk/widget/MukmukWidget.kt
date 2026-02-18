package com.example.mukmuk.widget

import android.content.Context
import androidx.compose.runtime.Composable
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.glance.GlanceId
import androidx.glance.GlanceModifier
import androidx.glance.GlanceTheme
import androidx.glance.action.actionStartActivity
import androidx.glance.action.clickable
import androidx.glance.appwidget.GlanceAppWidget
import androidx.glance.appwidget.action.actionRunCallback
import androidx.glance.appwidget.provideContent
import androidx.glance.background
import androidx.glance.currentState
import androidx.glance.layout.Alignment
import androidx.glance.layout.Box
import androidx.glance.layout.Column
import androidx.glance.layout.Spacer
import androidx.glance.layout.fillMaxSize
import androidx.glance.layout.fillMaxWidth
import androidx.glance.layout.height
import androidx.glance.layout.padding
import androidx.glance.state.GlanceStateDefinition
import androidx.glance.state.PreferencesGlanceStateDefinition
import androidx.glance.text.FontWeight
import androidx.glance.text.Text
import androidx.glance.text.TextAlign
import androidx.glance.text.TextStyle
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.stringPreferencesKey
import com.example.mukmuk.MainActivity

class MukmukWidget : GlanceAppWidget() {

    override val stateDefinition: GlanceStateDefinition<*> = PreferencesGlanceStateDefinition

    override suspend fun provideGlance(context: Context, id: GlanceId) {
        provideContent {
            GlanceTheme {
                MukmukWidgetContent()
            }
        }
    }

    companion object {
        val MENU_NAME_KEY = stringPreferencesKey("widget_menu_name")
        val MENU_EMOJI_KEY = stringPreferencesKey("widget_menu_emoji")

        // Default menus for random selection within the widget
        val widgetMenus = listOf(
            Pair("김치찌개", "\uD83C\uDF72"),
            Pair("돈까스", "\uD83E\uDD69"),
            Pair("짜장면", "\uD83C\uDF5C"),
            Pair("파스타", "\uD83C\uDF5D"),
            Pair("초밥", "\uD83C\uDF63"),
            Pair("떡볶이", "\uD83C\uDF36\uFE0F"),
            Pair("삼겹살", "\uD83E\uDD53"),
            Pair("햄버거", "\uD83C\uDF54"),
            Pair("쌀국수", "\uD83C\uDF5C"),
            Pair("피자", "\uD83C\uDF55"),
            Pair("비빔밥", "\uD83C\uDF5A"),
            Pair("라멘", "\uD83C\uDF5C"),
        )
    }
}

@Composable
private fun MukmukWidgetContent() {
    val prefs = currentState<Preferences>()
    val menuName = prefs[MukmukWidget.MENU_NAME_KEY] ?: "오늘 뭐 먹지?"
    val menuEmoji = prefs[MukmukWidget.MENU_EMOJI_KEY] ?: "\uD83E\uDD14"

    Box(
        modifier = GlanceModifier
            .fillMaxSize()
            .background(GlanceTheme.colors.widgetBackground)
            .clickable(actionStartActivity<MainActivity>())
            .padding(12.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(
            modifier = GlanceModifier.fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Emoji
            Text(
                text = menuEmoji,
                style = TextStyle(fontSize = 36.sp, textAlign = TextAlign.Center),
                modifier = GlanceModifier.fillMaxWidth()
            )

            Spacer(modifier = GlanceModifier.height(6.dp))

            // Menu name
            Text(
                text = menuName,
                style = TextStyle(
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = GlanceTheme.colors.onSurface,
                    textAlign = TextAlign.Center
                ),
                modifier = GlanceModifier.fillMaxWidth()
            )

            Spacer(modifier = GlanceModifier.height(10.dp))

            // Re-roll button
            Box(
                modifier = GlanceModifier
                    .clickable(actionRunCallback<RefreshMenuAction>())
                    .background(GlanceTheme.colors.primary)
                    .padding(horizontal = 16.dp, vertical = 6.dp),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "\uD83C\uDFB2 다시 뽑기",
                    style = TextStyle(
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Medium,
                        color = GlanceTheme.colors.onPrimary
                    )
                )
            }
        }
    }
}
