package com.acando.todohelper.widget;

import android.appwidget.AppWidgetManager;
import android.content.Context;
import android.content.Intent;
import android.widget.RemoteViews;
import android.widget.RemoteViewsService.RemoteViewsFactory;

import com.acando.todohelper.R;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.GregorianCalendar;

public final class ListProvider implements RemoteViewsFactory {
    private ArrayList<Article> listItemList = new ArrayList<>();
    private Context context = null;
    private int appWidgetId;

    public ListProvider(Context context, Intent intent) {
        this.context = context;
        appWidgetId = intent.getIntExtra(AppWidgetManager.EXTRA_APPWIDGET_ID,
                AppWidgetManager.INVALID_APPWIDGET_ID);
    }

    private void initList() {
        listItemList = FeedTable.getAll(context);
    }

    @Override
    public int getCount() {
        return listItemList.size();
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public RemoteViews getViewAt(int position) {
        RemoteViews remoteView = new RemoteViews(context.getPackageName(), R.layout.widget_list_item);

        Article article = listItemList.get(position);
        remoteView.setTextViewText(R.id.title, article.title);
        remoteView.setTextViewText(R.id.desc, article.desc);

        Calendar cal = new GregorianCalendar();
        SimpleDateFormat sdf = new SimpleDateFormat("dd.MM.YYYY HH:mm");
        cal.setTimeInMillis(article.date);
        remoteView.setTextViewText(R.id.date, sdf.format(cal.getTime()));

        Source source = SourceTable.get(context, article.source);
        remoteView.setTextViewText(R.id.source, source.name);

        //ListView click event part 2
        Intent fillInIntent = new Intent();
        fillInIntent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, appWidgetId);
        fillInIntent.putExtra("source_internal_name", article.source);
        fillInIntent.putExtra(WidgetProvider.EXTRA_LIST_VIEW_ITEM_URL, article.link);
        remoteView.setOnClickFillInIntent(R.id.widget_item_layout, fillInIntent);

        return remoteView;
    }


    @Override
    public RemoteViews getLoadingView() {
        return null;
    }

    @Override
    public int getViewTypeCount() {
        return 1;
    }

    @Override
    public boolean hasStableIds() {
        return true;
    }

    @Override
    public void onCreate() {
    }

    @Override
    public void onDataSetChanged() {
        initList();
    }

    @Override
    public void onDestroy() {
        listItemList.clear();
    }
}
