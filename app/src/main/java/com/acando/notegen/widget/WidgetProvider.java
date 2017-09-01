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

public class WidgetProvider extends AppWidgetProvider {

    public static final String ADD_NOTE = "com.acando.notegen.ADD_NOTE";
    public static final String EXTRA_EDIT_NOTE = "com.acando.notegen.EXTRA_EDIT_NOTE";

    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
        updateRoutine(context, appWidgetManager, appWidgetIds);
        super.onUpdate(context, appWidgetManager, appWidgetIds);
    }

    private static RemoteViews updateWidgetListView(Context context, int appWidgetId) {
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

        if(ADD_NOTE.equals(action)) {
            Intent noteIntent = new Intent(context, DetailActivity.class);
            noteIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            context.startActivity(noteIntent);
        } else if(EXTRA_EDIT_NOTE.equals(action)) {
            Intent noteIntent = new Intent(context, DetailActivity.class);
            noteIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            noteIntent.putExtra("note_id", intent.getIntExtra("note_id", -1));
            context.startActivity(noteIntent);
        }
    }

    public static void updateWidgets(Context context) {
        AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(context);
        int[] appWidgetIds = appWidgetManager.getAppWidgetIds(new ComponentName(context, WidgetProvider.class));
        updateRoutine(context, appWidgetManager, appWidgetIds);
    }

    public static void updateRoutine(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
        for (int appWidgetId : appWidgetIds) {
            RemoteViews remoteViews = updateWidgetListView(context, appWidgetId);

            //ListView Click Event - part 1
            Intent toastIntent = new Intent(context, WidgetProvider.class);
            toastIntent.setAction(WidgetProvider.EXTRA_EDIT_NOTE);
            toastIntent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, appWidgetId);
            PendingIntent toastPendingIntent = PendingIntent.getBroadcast(
                    context, 0, toastIntent, PendingIntent.FLAG_UPDATE_CURRENT);
            remoteViews.setPendingIntentTemplate(R.id.listViewWidget, toastPendingIntent);


            Intent myo2intent = new Intent(context, DetailActivity.class);
            myo2intent.setAction(ADD_NOTE);
            myo2intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            PendingIntent pending_myo2intent = PendingIntent.getActivity(context,
                    0, myo2intent, PendingIntent.FLAG_UPDATE_CURRENT);
            remoteViews.setOnClickPendingIntent(R.id.add, pending_myo2intent);

            appWidgetManager.updateAppWidget(appWidgetId, remoteViews);
            appWidgetManager.notifyAppWidgetViewDataChanged(appWidgetId, R.id.listViewWidget);
        }
    }
}
