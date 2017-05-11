package com.acando.todohelper.database;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.database.sqlite.SQLiteQueryBuilder;

public class DatabaseOpenHelper extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "todo.db";
    private static final String TABLE_NAME = "todos";
    private static final String SQL_CREATE = "CREATE TABLE " + TABLE_NAME +
            " (_id INTEGER PRIMARY KEY, date INTEGER, title TEXT , text TEXT , label TEXT )";
    private static final String SQL_DROP = "DROP TABLE IS EXISTS " + TABLE_NAME ;

    public DatabaseOpenHelper(Context context) {
        super(context, DATABASE_NAME, null, 1);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(SQL_CREATE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL(SQL_DROP);
        onCreate(db);
    }

    public Cursor getToDoEntries(String id, String[] projection, String selection, String[] selectionArgs, String sortOrder) {
        SQLiteQueryBuilder sqliteQueryBuilder = new SQLiteQueryBuilder();
        sqliteQueryBuilder.setTables(TABLE_NAME);

        if(id != null) {
            sqliteQueryBuilder.appendWhere("_id" + " = " + id);
        }

        return sqliteQueryBuilder.query(getReadableDatabase(),
                projection,
                selection,
                selectionArgs,
                null,
                null,
                sortOrder);
    }
}
