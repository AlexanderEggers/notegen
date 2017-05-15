package com.acando.todohelper.internal;

import android.content.ContentProviderClient;
import android.content.ContentValues;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.RemoteException;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;

import com.acando.todohelper.R;
import com.acando.todohelper.UtilNetwork;
import com.acando.todohelper.api.ToDoContentProvider;
import com.acando.todohelper.database.ToDoTable;

import java.io.ByteArrayOutputStream;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.add(R.id.content, new ToDoFragment(), "ToDoFragment");
        fragmentTransaction.commit();
    }

    private Uri insertToDo() throws RemoteException {
        ContentValues values = new ContentValues();
        values.put(ToDoTable.COLUMN_TITLE, "Test Title");
        values.put(ToDoTable.COLUMN_TEXT, "ich bin ein Test");
        values.put(ToDoTable.COLUMN_CREATION_DATE, System.currentTimeMillis());
        values.put(ToDoTable.COLUMN_LAST_MODIFY, System.currentTimeMillis());

        ContentValues values1 = new ContentValues();
        values1.put(ToDoTable.COLUMN_TITLE, "Test Title");
        values1.put(ToDoTable.COLUMN_TEXT, "ich bin ein Test");
        values1.put(ToDoTable.COLUMN_CREATION_DATE, System.currentTimeMillis());
        values1.put(ToDoTable.COLUMN_LAST_MODIFY, System.currentTimeMillis());

        Uri uri = Uri.parse(ToDoContentProvider.CONTENT_URI + ToDoContentProvider.TODO_BASE);
        ContentProviderClient yourCR = getContentResolver().acquireContentProviderClient(
                ToDoContentProvider.AUTHORITY);

        if(yourCR != null) {
            yourCR.insert(uri, values);
            Uri insertUri = yourCR.insert(uri, values1);

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                yourCR.close();
            } else {
                yourCR.release();
            }

            return insertUri;
        }
        return null;
    }

    private Cursor queueToDo() throws RemoteException {
        Uri uri = Uri.parse(ToDoContentProvider.CONTENT_URI + ToDoContentProvider.TODO_BASE + "/1");
        ContentProviderClient yourCR = getContentResolver().acquireContentProviderClient(
                        ToDoContentProvider.AUTHORITY);

        if(yourCR != null) {
            Cursor c = yourCR.query(uri, null, null, null, null);

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                yourCR.close();
            } else {
                yourCR.release();
            }

            return c;
        }
        return null;
    }

    private void updateToDo() throws RemoteException {
        Uri uri = Uri.parse(ToDoContentProvider.CONTENT_URI + ToDoContentProvider.TODO_BASE + "/1");
        ContentProviderClient yourCR = getContentResolver().acquireContentProviderClient(
                ToDoContentProvider.AUTHORITY);

        ContentValues values = new ContentValues();
        values.put(ToDoTable.COLUMN_TITLE, "New title");
        values.put(ToDoTable.COLUMN_TEXT, "Test Test");

        try {
            Bitmap bmp = UtilNetwork.getImage("https://intranet.acando.de/image/company_logo?img_id=4556&t=1494825189904");
            ByteArrayOutputStream stream = new ByteArrayOutputStream();
            bmp.compress(Bitmap.CompressFormat.JPEG, 100, stream);
            byte[] byteArray = stream.toByteArray();

            values.put(ToDoTable.COLUMN_IMAGE, byteArray);
        } catch (Exception e) {
            Log.e(MainActivity.class.getName(), e.getMessage());
        }

        values.put(ToDoTable.COLUMN_CREATION_DATE, System.currentTimeMillis());
        values.put(ToDoTable.COLUMN_LAST_MODIFY, System.currentTimeMillis());

        if(yourCR != null) {
            yourCR.update(uri, values, null, null);

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                yourCR.close();
            } else {
                yourCR.release();
            }
        }
    }
}
