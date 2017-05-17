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

public class NoteContentProvider extends ContentProvider {

    private DatabaseHelper mDatabase;

    private static final int
            LABEL_SINGLE = 1, LABEL_MULTI = 2,
            NOTE_SINGLE = 3, NOTE_MULTI = 4,
            NOTE_LABELS = 5, LABEL_NOTES = 6;

    public static final String AUTHORITY = "com.acando.notegen.api.NoteContentProvider";

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
                throw new SQLException("Uri Format not supported for this operation: " + uri + " type: " + uriType);
        }
    }

    @Nullable
    @Override
    public Uri insert(@NonNull Uri uri, @Nullable ContentValues values) {
        int uriType = sURIMatcher.match(uri);
        long id;
        String basePath;

        switch (uriType) {
            case LABEL_SINGLE:
            case LABEL_MULTI:
                basePath = LABEL_BASE;
                id = mDatabase.insert(LabelTable.TABLE_NAME, values);
                break;
            case NOTE_SINGLE:
            case NOTE_MULTI:
                basePath = NOTE_BASE;
                id = mDatabase.insert(NoteTable.TABLE_NAME, values);
                break;
            case LABEL_NOTES:
                basePath = LABEL_NOTES_BASE;
                id = mDatabase.insert(NoteLabelTable.TABLE_NAME, values);
                break;
            case NOTE_LABELS:
                basePath = NOTE_LABELS_BASE;
                id = mDatabase.insert(NoteLabelTable.TABLE_NAME, values);
                break;
            default:
                throw new SQLException("Uri Format not supported for this operation: " + uri + " type: " + uriType);
        }

        getContext().getContentResolver().notifyChange(uri, null);
        return Uri.parse(NoteContentProvider.CONTENT_URI + basePath + "/" + id);
    }

    @Override
    public int delete(@NonNull Uri uri, @Nullable String selection, @Nullable String[] selectionArgs) {
        int uriType = sURIMatcher.match(uri);
        switch (uriType) {
            case LABEL_SINGLE:
                return mDatabase.delete(LabelTable.TABLE_NAME, uri.getLastPathSegment(), null);
            case NOTE_SINGLE:
                return mDatabase.delete(NoteTable.TABLE_NAME, uri.getLastPathSegment(), null);
            case NOTE_LABELS:
                return mDatabase.delete(NoteLabelTable.TABLE_NAME,
                        NoteLabelTable.COLUMN_NOTE_ID + " = " + uri.getLastPathSegment(), selection);
            case LABEL_NOTES:
                return mDatabase.delete(NoteLabelTable.TABLE_NAME,
                        NoteLabelTable.COLUMN_LABEL_ID + " = " + uri.getLastPathSegment(), selection);
            default:
                throw new SQLException("Uri Format not supported for this operation: " + uri + " type: " + uriType);
        }
    }

    @Override
    public int update(@NonNull Uri uri, @Nullable ContentValues values, @Nullable String selection,
                      @Nullable String[] selectionArgs) {
        int uriType = sURIMatcher.match(uri);
        switch (uriType) {
            case LABEL_SINGLE:
                return  mDatabase.update(LabelTable.TABLE_NAME, uri.getLastPathSegment(), values);
            case NOTE_SINGLE:
                return mDatabase.update(NoteTable.TABLE_NAME, uri.getLastPathSegment(), values);
            case NOTE_LABELS:
                return mDatabase.update(NoteLabelTable.TABLE_NAME,
                        NoteLabelTable.COLUMN_NOTE_ID + "= " + uri.getLastPathSegment(), values);
            case LABEL_NOTES:
                return mDatabase.update(NoteLabelTable.TABLE_NAME,
                        NoteLabelTable.COLUMN_LABEL_ID + "= " + uri.getLastPathSegment(), values);
            default:
                throw new SQLException("Uri Format not supported for this operation: " + uri + " type: " + uriType);
        }
    }

    @Nullable
    @Override
    public String getType(@NonNull Uri uri) {
        return null;
    }
}
