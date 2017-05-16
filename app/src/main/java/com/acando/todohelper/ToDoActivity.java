package com.acando.todohelper;

import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v7.widget.RecyclerView;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.support.v7.widget.Toolbar;
import android.view.View;

import com.acando.todohelper.api.ToDoContentProvider;
import com.acando.todohelper.database.UtilDatabase;
import com.acando.todohelper.internal.ToDo;
import com.acando.todohelper.internal.ToDoListAdapter;

import java.util.ArrayList;

public class ToDoActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor> {

    private ToDoListAdapter adapter;

    @Override
    protected void onResume() {
        super.onResume();

        getSupportLoaderManager().destroyLoader(0);
        getSupportLoaderManager().initLoader(0, null, ToDoActivity.this);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_todo);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        RecyclerView recyclerView = (RecyclerView) findViewById(R.id.list);
        recyclerView.setHasFixedSize(false);

        StaggeredGridLayoutManager layoutManager = new StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL);
        recyclerView.setLayoutManager(layoutManager);

        adapter = new ToDoListAdapter(this, new ArrayList<ToDo>());
        recyclerView.setAdapter(adapter);

        findViewById(R.id.fab).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(ToDoActivity.this, DetailActivity.class);
                startActivity(i);
            }
        });
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        Uri uri = Uri.parse(ToDoContentProvider.CONTENT_URI + ToDoContentProvider.TODO_BASE);
        return new CursorLoader(this, uri, null, null, null, null);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        ArrayList<ToDo> todos = UtilDatabase.getToDos(data);
        if(todos.isEmpty()) {
            findViewById(R.id.list).setVisibility(View.INVISIBLE);
            findViewById(R.id.no_content_view).setVisibility(View.VISIBLE);
        } else {
            findViewById(R.id.list).setVisibility(View.VISIBLE);
            findViewById(R.id.no_content_view).setVisibility(View.INVISIBLE);
            adapter.refreshContent(todos);
        }
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {

    }
}
