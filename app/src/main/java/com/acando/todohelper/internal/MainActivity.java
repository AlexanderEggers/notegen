package com.acando.todohelper.internal;

import android.content.ContentProviderClient;
import android.content.ContentValues;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.RemoteException;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.acando.todohelper.R;
import com.acando.todohelper.api.ToDoContentProvider;
import com.acando.todohelper.database.ToDoTable;

public class MainActivity extends AppCompatActivity {

    private ListAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        try {
            //System.out.println(insertToDo());
            Cursor c = queueToDo();
            c.moveToFirst();
            System.out.println(c.getCount());
            System.out.println(c.getInt(0));
        } catch (RemoteException e) {
            e.printStackTrace();
        }

        /*RecyclerView recyclerView = (RecyclerView) findViewById(R.id.list);
        recyclerView.setHasFixedSize(true);

        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        recyclerView.setLayoutManager(layoutManager);

        //Fetch data from database

        adapter = new ListAdapter(this, new ArrayList<ToDo>());
        recyclerView.setAdapter(adapter);*/
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
}
