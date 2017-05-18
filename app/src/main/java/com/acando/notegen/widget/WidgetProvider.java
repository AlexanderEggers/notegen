package com.acando.notegen.widget;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.widget.RemoteViews;

import com.acando.notegen.DetailActivity;
import com.acando.notegen.R;
import com.acando.notegen.database.NoteTable;

public class WidgetProvider extends AppWidgetProvider {

    public static final String UPDATE_FORCE = "com.acando.notegen.UPDATE_FORCE";
    public static final String ADD_NOTE = "com.acando.notegen.ADD_NOTE";
    public static final String EXTRA_LIST_VIEW_ITEM_URL = "com.acando.notegen.EXTRA_LIST_VIEW_ITEM_URL";

    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
        for (int appWidgetId : appWidgetIds) {
            RemoteViews remoteViews = updateWidgetListView(context, appWidgetId);

            //ListView Click Event - part 1
            Intent toastIntent = new Intent(context, WidgetProvider.class);
            toastIntent.setAction(WidgetProvider.EXTRA_LIST_VIEW_ITEM_URL);
            toastIntent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, appWidgetId);
            PendingIntent toastPendingIntent = PendingIntent.getBroadcast(
                    context, 0, toastIntent, PendingIntent.FLAG_UPDATE_CURRENT);
            remoteViews.setPendingIntentTemplate(R.id.listViewWidget, toastPendingIntent);

            //Add Note Button Click Event
            Intent updateIntent = new Intent();
            updateIntent.setAction(ADD_NOTE);
            updateIntent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, appWidgetId);
            PendingIntent pendingIntent = PendingIntent.getBroadcast(context.getApplicationContext(), 0, updateIntent, PendingIntent.FLAG_UPDATE_CURRENT);
            remoteViews.setOnClickPendingIntent(R.id.reload, pendingIntent);

            appWidgetManager.updateAppWidget(appWidgetId, remoteViews);
        }
        super.onUpdate(context, appWidgetManager, appWidgetIds);
    }

    private RemoteViews updateWidgetListView(Context context, int appWidgetId) {
        // which layout to show on widget
        RemoteViews remoteViews = new RemoteViews(context.getPackageName(), R.layout.widget_layout);

        Intent svcIntent = new Intent(context, WidgetService.class);
        svcIntent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, appWidgetId);
        svcIntent.setData(Uri.parse(svcIntent.toUri(Intent.URI_INTENT_SCHEME)));

        remoteViews.setRemoteAdapter(R.id.listViewWidget, svcIntent);
        remoteViews.setEmptyView(R.id.listViewWidget, R.id.empty_view);

        return remoteViews;
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        super.onReceive(context, intent);

        String action = intent.getAction();
        System.out.println(action);

        if (UPDATE_FORCE.equals(action)) {
            AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(context);
            int appWidgetIds[] = appWidgetManager.getAppWidgetIds(new ComponentName(context, WidgetProvider.class));

            for (int appWidgetId : appWidgetIds) {
                RemoteViews remoteViews = updateWidgetListView(context, appWidgetId);

                //ListView Click Event - part 1
                Intent toastIntent = new Intent(context, WidgetProvider.class);
                toastIntent.setAction(WidgetProvider.EXTRA_LIST_VIEW_ITEM_URL);
                toastIntent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, appWidgetId);
                PendingIntent toastPendingIntent = PendingIntent.getBroadcast(
                        context, 0, toastIntent, PendingIntent.FLAG_UPDATE_CURRENT);
                remoteViews.setPendingIntentTemplate(R.id.listViewWidget, toastPendingIntent);

                //Add Note Button Click Event
                Intent updateIntent = new Intent();
                updateIntent.setAction(ADD_NOTE);
                updateIntent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, appWidgetId);
                PendingIntent pendingIntent = PendingIntent.getBroadcast(context.getApplicationContext(), 0, updateIntent, PendingIntent.FLAG_UPDATE_CURRENT);
                remoteViews.setOnClickPendingIntent(R.id.reload, pendingIntent);

                appWidgetManager.notifyAppWidgetViewDataChanged(appWidgetId, R.id.listViewWidget);
                appWidgetManager.updateAppWidget(appWidgetId, remoteViews);
            }
        } else if(ADD_NOTE.equals(action)) {
            Intent noteIntent = new Intent(context, DetailActivity.class);
            noteIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            context.startActivity(noteIntent);
        } else if(EXTRA_LIST_VIEW_ITEM_URL.equals(action)) {
            Intent noteIntent = new Intent(context, DetailActivity.class);
            noteIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            noteIntent.putExtra("dataInline", true);
            noteIntent.putExtra("note_id", intent.getIntExtra("note_id", -1));
            noteIntent.putExtra("note_title", intent.getStringExtra("note_title"));
            noteIntent.putExtra("note_text", intent.getStringExtra("note_text"));
            noteIntent.putExtra("note_image", intent.getByteArrayExtra("note_image"));
            noteIntent.putExtra("note_archive", intent.getIntExtra("note_archive", NoteTable.FALSE));
            noteIntent.putExtra("note_bin", intent.getIntExtra("note_bin", NoteTable.FALSE));
            noteIntent.putExtra("modify_date", intent.getLongExtra("modify_date", -1));
            context.startActivity(noteIntent);
        }
    }
}
