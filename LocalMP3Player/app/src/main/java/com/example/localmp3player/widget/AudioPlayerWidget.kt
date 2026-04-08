package com.example.localmp3player.widget

import android.app.PendingIntent
import android.appwidget.AppWidgetManager
import android.appwidget.AppWidgetProvider
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.widget.RemoteViews
import com.example.localmp3player.MainActivity
import com.example.localmp3player.R
import com.example.localmp3player.player.AudioPlayerService

class AudioPlayerWidget : AppWidgetProvider() {
    override fun onUpdate(
        context: Context,
        appWidgetManager: AppWidgetManager,
        appWidgetIds: IntArray
    ) {
        // Update all widgets
        appWidgetIds.forEach { widgetId ->
            updateAppWidget(context, appWidgetManager, widgetId)
        }
    }

    override fun onEnabled(context: Context) {
        // When the first widget is created
    }

    override fun onDisabled(context: Context) {
        // When the last widget is disabled
    }

    private fun updateAppWidget(
        context: Context,
        appWidgetManager: AppWidgetManager,
        appWidgetId: Int
    ) {
        val views = RemoteViews(context.packageName, R.layout.widget_small)

        // Set up click intents
        val activityIntent = Intent(context, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP
        }
        val pendingIntent = PendingIntent.getActivity(
            context, 0, activityIntent,
            PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
        )
        views.setOnClickPendingIntent(R.id.widget_content, pendingIntent)

        // Play/Pause button
        val playPauseIntent = Intent(context, AudioPlayerService::class.java).apply {
            action = AudioPlayerService.ACTION_PLAY  // Will toggle based on state
        }
        val playPausePendingIntent = PendingIntent.getService(
            context, 1, playPauseIntent,
            PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
        )
        views.setOnClickPendingIntent(R.id.btn_play_pause, playPausePendingIntent)

        // Previous button
        val prevIntent = Intent(context, AudioPlayerService::class.java).apply {
            action = AudioPlayerService.ACTION_PREVIOUS
        }
        val prevPendingIntent = PendingIntent.getService(
            context, 2, prevIntent,
            PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
        )
        views.setOnClickPendingIntent(R.id.btn_prev, prevPendingIntent)

        // Next button
        val nextIntent = Intent(context, AudioPlayerService::class.java).apply {
            action = AudioPlayerService.ACTION_NEXT
        }
        val nextPendingIntent = PendingIntent.getService(
            context, 3, nextIntent,
            PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
        )
        views.setOnClickPendingIntent(R.id.btn_next, nextPendingIntent)

        // Update the widget
        appWidgetManager.updateAppWidget(appWidgetId, views)
    }

    companion object {
        fun triggerUpdate(context: Context) {
            val appWidgetManager = AppWidgetManager.getInstance(context)
            val componentName = ComponentName(context, AudioPlayerWidget::class.java)
            val appWidgetIds = appWidgetManager.getAppWidgetIds(componentName)
            appWidgetManager.notifyAppWidgetViewDataChanged(appWidgetIds, R.id.widget_content)
        }
    }
}