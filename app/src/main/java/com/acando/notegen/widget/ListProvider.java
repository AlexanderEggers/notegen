package com.acando.notegen.widget;

import android.appwidget.AppWidgetManager;
import android.content.ContentProviderClient;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.RemoteException;
import android.widget.RemoteViews;
import android.widget.RemoteViewsService;

import com.acando.notegen.R;
import com.acando.notegen.api.NoteContentProvider;
import com.acando.notegen.api.PublicNoteContentProvider;
import com.acando.notegen.database.NoteTable;
import com.acando.notegen.database.UtilDatabase;
import com.acando.notegen.internal.Note;

import java.util.ArrayList;

public final class ListProvider implements RemoteViewsService.RemoteViewsFactory {
    private ArrayList<Note> mNotes = new ArrayList<>();
    private Context mContext = null;
    private int appWidgetId;

    public ListProvider(Context mContext, Intent intent) {
        this.mContext = mContext;
        appWidgetId = intent.getIntExtra(AppWidgetManager.EXTRA_APPWIDGET_ID,
                AppWidgetManager.INVALID_APPWIDGET_ID);
    }

    private void initList() {
        Uri uri = Uri.parse(PublicNoteContentProvider.CONTENT_URI + NoteContentProvider.NOTE_BASE);
        String selection = NoteTable.COLUMN_ARCHIVE + " = " + NoteTable.FALSE + " AND " +
                NoteTable.COLUMN_BIN + " = " + NoteTable.FALSE;
        ContentProviderClient cr = mContext.getContentResolver().acquireContentProviderClient(
                PublicNoteContentProvider.AUTHORITY);

        try {
            Cursor data = cr.query(uri, null, selection, null, null);
            mNotes = UtilDatabase.getNotes(data);
        } catch (RemoteException e) {
            e.printStackTrace();
        }

        cr.release();
    }

    @Override
    public int getCount() {
        return mNotes.size();
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public RemoteViews getViewAt(int position) {
        RemoteViews remoteView = new RemoteViews(mContext.getPackageName(), R.layout.widget_list_item);

        Note note = mNotes.get(position);
        remoteView.setTextViewText(R.id.title, note.title);
        remoteView.setTextViewText(R.id.desc, note.text);

        //ListView click event part 2
        Intent fillInIntent = new Intent();
        fillInIntent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, appWidgetId);
        fillInIntent.putExtra("note_id", note.id);
        fillInIntent.putExtra("note_title", note.title);
        fillInIntent.putExtra("note_text", note.text);
        fillInIntent.putExtra("note_image", note.imageByte);
        fillInIntent.putExtra("note_archive", note.isArchive);
        fillInIntent.putExtra("note_bin", note.isTrash);
        fillInIntent.putExtra("modify_date", note.lastModifyDate);
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
        mNotes.clear();
    }
}
