package com.acando.notegen.api;

import android.content.ContentProvider;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.SQLException;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.acando.notegen.database.DatabaseHelper;
import com.acando.notegen.database.LabelTable;
import com.acando.notegen.database.NoteLabelTable;
import com.acando.notegen.database.NoteTable;

public class PublicNoteContentProvider extends ContentProvider {

    private DatabaseHelper mDatabase;

    private static final int
            LABEL_SINGLE = 1, LABEL_MULTI = 2,
            NOTE_SINGLE = 3, NOTE_MULTI = 4,
            NOTE_LABELS = 5, LABEL_NOTES = 6;

    public static final String AUTHORITY = "com.acando.notegen.api.PublicNoteContentProvider";

    public static final String LABEL_BASE = "label";
    public static final String NOTE_BASE = "note";
    public static final String NOTE_LABELS_BASE = "note-labels";
    public static final String LABEL_NOTES_BASE = "label-notes";

    public static final Uri CONTENT_URI = Uri.parse("content://" + AUTHORITY + "/");

    private static final UriMatcher sURIMatcher = new UriMatcher(UriMatcher.NO_MATCH);
    static {
        sURIMatcher.addURI(AUTHORITY, LABEL_BASE + "/#", LABEL_SINGLE);
        sURIMatcher.addURI(AUTHORITY, LABEL_BASE, LABEL_MULTI);

        sURIMatcher.addURI(AUTHORITY, NOTE_BASE + "/#", NOTE_SINGLE);
        sURIMatcher.addURI(AUTHORITY, NOTE_BASE, NOTE_MULTI);

        sURIMatcher.addURI(AUTHORITY, NOTE_LABELS_BASE + "/#", NOTE_LABELS);
        sURIMatcher.addURI(AUTHORITY, LABEL_NOTES_BASE + "/#", LABEL_NOTES);
    }


    @Override
    public boolean onCreate() {
        mDatabase = new DatabaseHelper(getContext());
        return false;
    }

    @Nullable
    @Override
    public Cursor query(@NonNull Uri uri, @Nullable String[] projection, @Nullable String selection,
                        @Nullable String[] selectionArgs, @Nullable String sortOrder) {
        int uriType = sURIMatcher.match(uri);
        switch (uriType) {
            case LABEL_SINGLE:
                return mDatabase.queue(LabelTable.TABLE_NAME, uri.getLastPathSegment(), projection,
                        selection, selectionArgs, sortOrder);
            case LABEL_MULTI:
                return mDatabase.queue(LabelTable.TABLE_NAME, null, projection,
                        selection, selectionArgs, sortOrder);
            case NOTE_SINGLE:
                return mDatabase.queue(NoteTable.TABLE_NAME, uri.getLastPathSegment(), projection,
                        selection, selectionArgs, sortOrder);
            case NOTE_MULTI:
                return mDatabase.queue(NoteTable.TABLE_NAME, null, projection,
                        selection, selectionArgs, sortOrder);
            case NOTE_LABELS:
                return mDatabase.queue(NoteLabelTable.TABLE_NAME,
                        NoteLabelTable.COLUMN_NOTE_ID + " = " + uri.getLastPathSegment(), projection,
                        selection, selectionArgs, sortOrder);
            case LABEL_NOTES:
                return mDatabase.queue(NoteLabelTable.TABLE_NAME,
                        NoteLabelTable.COLUMN_LABEL_ID + " = " + uri.getLastPathSegment(),
                        projection, selection, selectionArgs, sortOrder);
            default:
                throw new RuntimeException("Uri Format not supported for this operation: " + uri + " type: " + uriType);
        }
    }

    @Nullable
    @Override
    public Uri insert(@NonNull Uri uri, @Nullable ContentValues values) {
        throw new RuntimeException("This URI Request is not supported");
    }

    @Override
    public int delete(@NonNull Uri uri, @Nullable String selection, @Nullable String[] selectionArgs) {
        throw new RuntimeException("This URI Request is not supported");
    }

    @Override
    public int update(@NonNull Uri uri, @Nullable ContentValues values, @Nullable String selection,
                      @Nullable String[] selectionArgs) {
        throw new RuntimeException("This URI Request is not supported");
    }

    @Nullable
    @Override
    public String getType(@NonNull Uri uri) {
        return null;
    }
}
