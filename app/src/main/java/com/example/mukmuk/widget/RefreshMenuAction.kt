package com.example.mukmuk.widget

import android.content.Context
import androidx.datastore.preferences.core.MutablePreferences
import androidx.glance.GlanceId
import androidx.glance.action.ActionParameters
import androidx.glance.appwidget.action.ActionCallback
import androidx.glance.appwidget.state.updateAppWidgetState

class RefreshMenuAction : ActionCallback {
    override suspend fun onAction(
        context: Context,
        glanceId: GlanceId,
        parameters: ActionParameters
    ) {
        val randomMenu = MukmukWidget.widgetMenus.random()

        updateAppWidgetState(context, glanceId) { prefs: MutablePreferences ->
            prefs[MukmukWidget.MENU_NAME_KEY] = randomMenu.first
            prefs[MukmukWidget.MENU_EMOJI_KEY] = randomMenu.second
        }

        MukmukWidget().update(context, glanceId)
    }
}
