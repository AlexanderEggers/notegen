package com.acando.todohelper.api;

import android.content.ContentProvider;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.SQLException;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.acando.todohelper.database.DatabaseHelper;
import com.acando.todohelper.database.LabelTable;
import com.acando.todohelper.database.ToDoLabelTable;
import com.acando.todohelper.database.ToDoTable;

public class ToDoContentProvider extends ContentProvider {

    // database
    private DatabaseHelper mDatabase;

    private static final int
            LABEL_SINGLE = 1, LABEL_MULTI = 2,
            TODO_SINGLE = 3, TODO_MULTI = 4,
            TODO_LABELS = 5, LABEL_TODOS = 6;

    public static final String AUTHORITY = "com.acando.todohelper.api.ToDoContentProvider";

    public static final String LABEL_BASE = "labels";
    public static final String TODO_BASE = "todos";
    public static final String TODO_LABEL_BASE = "todo-labels";

    public static final Uri CONTENT_URI = Uri.parse("content://" + AUTHORITY + "/");

    private static final UriMatcher sURIMatcher = new UriMatcher(UriMatcher.NO_MATCH);
    static {
        sURIMatcher.addURI(AUTHORITY, LABEL_BASE + "/?", LABEL_SINGLE);
        sURIMatcher.addURI(AUTHORITY, LABEL_BASE, LABEL_MULTI);

        sURIMatcher.addURI(AUTHORITY, TODO_BASE + "/#", TODO_SINGLE);
        sURIMatcher.addURI(AUTHORITY, TODO_BASE, TODO_MULTI);

        sURIMatcher.addURI(AUTHORITY, TODO_LABEL_BASE + "/todo/#", TODO_LABELS);
        sURIMatcher.addURI(AUTHORITY, TODO_LABEL_BASE + "/label/?", LABEL_TODOS);
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
            case TODO_SINGLE:
                return mDatabase.queue(ToDoTable.TABLE_NAME, uri.getLastPathSegment(), projection,
                        selection, selectionArgs, sortOrder);
            case TODO_MULTI:
                return mDatabase.queue(ToDoTable.TABLE_NAME, null, projection,
                        selection, selectionArgs, sortOrder);
            case TODO_LABELS:
                return mDatabase.queue(ToDoTable.TABLE_NAME,
                        ToDoLabelTable.COLUMN_TODO_ID + "= " + uri.getLastPathSegment(), projection,
                        selection, selectionArgs, sortOrder);
            case LABEL_TODOS:
                return mDatabase.queue(ToDoTable.TABLE_NAME,
                        ToDoLabelTable.COLUMN_LABEL_INTERNAL_NAME + "= '" + uri.getLastPathSegment() + "'",
                        projection, selection, selectionArgs, sortOrder);
            default:
                throw new SQLException("Uri Format not supported for this operation");
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
                basePath = LABEL_BASE;
                id = mDatabase.insert(LabelTable.TABLE_NAME, values);
                break;
            case LABEL_MULTI:
                basePath = LABEL_BASE;
                id = mDatabase.insert(LabelTable.TABLE_NAME, values);
                break;
            case TODO_SINGLE:
                basePath = TODO_BASE;
                id = mDatabase.insert(ToDoTable.TABLE_NAME, values);
                break;
            case TODO_MULTI:
                basePath = TODO_BASE;
                id = mDatabase.insert(ToDoTable.TABLE_NAME, values);
                break;
            case TODO_LABELS:
                basePath = TODO_LABEL_BASE;
                id = mDatabase.insert(ToDoLabelTable.TABLE_NAME, values);
                break;
            default:
                throw new SQLException("Uri Format not supported for this operation");
        }

        getContext().getContentResolver().notifyChange(uri, null);
        return Uri.parse(ToDoContentProvider.CONTENT_URI + basePath + "/" + id);
    }

    @Override
    public int delete(@NonNull Uri uri, @Nullable String selection, @Nullable String[] selectionArgs) {
        int uriType = sURIMatcher.match(uri), id;
        switch (uriType) {
            case LABEL_SINGLE:
                id = mDatabase.delete(LabelTable.TABLE_NAME, uri.getLastPathSegment());
                break;
            case TODO_SINGLE:
                id = mDatabase.delete(ToDoTable.TABLE_NAME, uri.getLastPathSegment());
                break;
            case TODO_LABELS:
                id = mDatabase.delete(ToDoLabelTable.TABLE_NAME,
                        ToDoLabelTable.COLUMN_TODO_ID + "= " + uri.getLastPathSegment());
                break;
            case LABEL_TODOS:
                id = mDatabase.delete(ToDoLabelTable.TABLE_NAME,
                        ToDoLabelTable.COLUMN_LABEL_INTERNAL_NAME + "= '" + uri.getLastPathSegment() + "'");
                break;
            default:
                throw new SQLException("Uri Format not supported for this operation");
        }
        return id;
    }

    @Override
    public int update(@NonNull Uri uri, @Nullable ContentValues values, @Nullable String selection, @Nullable String[] selectionArgs) {
        int uriType = sURIMatcher.match(uri), id;
        switch (uriType) {

        }


        return 0;
    }

    @Nullable
    @Override
    public String getType(@NonNull Uri uri) {
        return null;
    }
}
