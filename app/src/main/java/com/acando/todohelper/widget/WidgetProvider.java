package com.acando.todohelper.widget;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.view.View;
import android.widget.RemoteViews;

import com.acando.todohelper.R;

public class WidgetProvider extends AppWidgetProvider {

    public static final String UPDATE_FORCE = "com.wordpress.laaptu.UPDATE_FORCE";
    public static final String DATA_FETCHED = "com.wordpress.laaptu.DATA_FETCHED";
    public static final String EXTRA_LIST_VIEW_ITEM_URL = "com.wordpress.laaptu.EXTRA_LIST_VIEW_ITEM_URL";

    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
        for (int appWidgetId : appWidgetIds) {
            Intent serviceIntent = new Intent(context, RemoteFetchService.class);
            serviceIntent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, appWidgetId);
            context.startService(serviceIntent);
        }
        super.onUpdate(context, appWidgetManager, appWidgetIds);
    }

    private RemoteViews updateWidgetListView(Context context, int appWidgetId) {
        // which layout to show on widget
        RemoteViews remoteViews = new RemoteViews(context.getPackageName(), R.layout.widget_layout);

        // RemoteViews Service needed to provide adapter for ListView
        Intent svcIntent = new Intent(context, WidgetService.class);
        // passing app widget id to that RemoteViews Service
        svcIntent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, appWidgetId);
        // setting a unique Uri to the intent
        // don't know its purpose to me right now
        svcIntent.setData(Uri.parse(svcIntent.toUri(Intent.URI_INTENT_SCHEME)));
        // setting adapter to listview of the widget
        remoteViews.setRemoteAdapter(R.id.listViewWidget, svcIntent);
        // setting an empty view in case of no data
        remoteViews.setEmptyView(R.id.listViewWidget, R.id.empty_view);

        return remoteViews;
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        super.onReceive(context, intent);

        System.out.println(intent.getAction());

        if (intent.getAction().equals(DATA_FETCHED)) {
            int appWidgetId = intent.getIntExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, AppWidgetManager.INVALID_APPWIDGET_ID);
            AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(context);
            RemoteViews remoteViews = updateWidgetListView(context, appWidgetId);

            //ListView Click Event - part 1
            Intent toastIntent = new Intent(context, WidgetProvider.class);
            toastIntent.setAction(WidgetProvider.EXTRA_LIST_VIEW_ITEM_URL);
            toastIntent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, appWidgetId);
            PendingIntent toastPendingIntent = PendingIntent.getBroadcast(
                    context, 0, toastIntent, PendingIntent.FLAG_UPDATE_CURRENT);
            remoteViews.setPendingIntentTemplate(R.id.listViewWidget, toastPendingIntent);

            //Reload Button Click Event
            Intent updateIntent = new Intent();
            updateIntent.setAction(UPDATE_FORCE);
            updateIntent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, appWidgetId);
            PendingIntent pendingIntent = PendingIntent.getBroadcast(context.getApplicationContext(), 0, updateIntent, PendingIntent.FLAG_UPDATE_CURRENT);
            remoteViews.setOnClickPendingIntent(R.id.reload, pendingIntent);

            remoteViews.setTextViewText(R.id.reload, "RELOAD");
            remoteViews.setViewVisibility(R.id.progressbar, View.INVISIBLE);
            remoteViews.setViewVisibility(R.id.listViewWidget, View.VISIBLE);
            appWidgetManager.notifyAppWidgetViewDataChanged(appWidgetId, R.id.listViewWidget);

            appWidgetManager.updateAppWidget(appWidgetId, remoteViews);
        } else if (intent.getAction().equals(EXTRA_LIST_VIEW_ITEM_URL)) {
            String url = intent.getStringExtra(EXTRA_LIST_VIEW_ITEM_URL);

            Intent i = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
            intent.setPackage("com.android.chrome");

            try {
                context.startActivity(i);
            } catch(ActivityNotFoundException e) {
                // Chrome is not installed
                intent.setPackage(null);
                context.startActivity(i);
            }
        } else if (intent.getAction().equals(UPDATE_FORCE)) {
            int appWidgetId = intent.getIntExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, AppWidgetManager.INVALID_APPWIDGET_ID);
            Intent serviceIntent = new Intent(context, RemoteFetchService.class);
            serviceIntent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, appWidgetId);
            context.startService(serviceIntent);

            RemoteViews remoteViews = updateWidgetListView(context, appWidgetId);
            remoteViews.setTextViewText(R.id.reload, "LOADING");
            remoteViews.setViewVisibility(R.id.progressbar, View.VISIBLE);
            remoteViews.setViewVisibility(R.id.listViewWidget, View.INVISIBLE);
            remoteViews.setOnClickPendingIntent(R.id.reload, null);

            AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(context);
            appWidgetManager.updateAppWidget(appWidgetId, remoteViews);
        }
    }
}
