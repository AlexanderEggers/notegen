package com.acando.todohelper.internal;

import android.app.IntentService;
import android.content.ContentProviderClient;
import android.content.ContentValues;
import android.content.Intent;
import android.net.Uri;
import android.support.annotation.Nullable;

import com.acando.todohelper.api.ToDoContentProvider;
import com.acando.todohelper.database.ToDoTable;

public class SyncService extends IntentService {

    public SyncService() {
        super("SyncService");
    }

    @Override
    protected void onHandleIntent(@Nullable Intent intent) {
        String title = intent.getStringExtra("title");
        String text = intent.getStringExtra("text");
        byte[] image = intent.getByteArrayExtra("image");
        int id = intent.getIntExtra("id", -1);

        ContentValues values = new ContentValues();
        values.put(ToDoTable.COLUMN_TITLE, title);
        values.put(ToDoTable.COLUMN_TEXT, text);
        values.put(ToDoTable.COLUMN_LAST_MODIFY, System.currentTimeMillis());

        if(image != null && image.length != 0) {
            values.put(ToDoTable.COLUMN_IMAGE, image);
        }

        ContentProviderClient yourCR = getContentResolver().acquireContentProviderClient(
                ToDoContentProvider.AUTHORITY);

        try {
            if(id != -1) {
                Uri uri = Uri.parse(ToDoContentProvider.CONTENT_URI + ToDoContentProvider.TODO_BASE + "/" + id);
                yourCR.update(uri, values, null, null);
            } else {
                Uri uri = Uri.parse(ToDoContentProvider.CONTENT_URI + ToDoContentProvider.TODO_BASE);
                yourCR.insert(uri, values);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        yourCR.release();

        System.out.println("FINISHED");
    }
}
