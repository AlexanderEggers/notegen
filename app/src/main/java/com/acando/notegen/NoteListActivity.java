package com.acando.notegen;

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
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import com.acando.notegen.api.NoteContentProvider;
import com.acando.notegen.database.UtilDatabase;
import com.acando.notegen.internal.Label;
import com.acando.notegen.internal.Note;
import com.acando.notegen.internal.NoteListAdapter;

import java.util.ArrayList;

public class NoteListActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor> {

    private NoteListAdapter mAdapter;
    private int mLoaderID;
    private Label mLabel;

    @Override
    protected void onResume() {
        super.onResume();
        getSupportLoaderManager().destroyLoader(mLoaderID);
        getSupportLoaderManager().initLoader(mLoaderID, null, NoteListActivity.this);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_list);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        RecyclerView recyclerView = (RecyclerView) findViewById(R.id.list);
        recyclerView.setHasFixedSize(false);

        StaggeredGridLayoutManager layoutManager = new StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL);
        recyclerView.setLayoutManager(layoutManager);

        mAdapter = new NoteListAdapter(this, new ArrayList<Note>());
        recyclerView.setAdapter(mAdapter);

        Label label = (Label) getIntent().getSerializableExtra("label_object");
        if(label != null) {
            mLabel = label;
            setTitle(label.name);
            mLoaderID = 1;

            if(getSupportActionBar() != null) {
                getSupportActionBar().setDisplayHomeAsUpEnabled(true);
                getSupportActionBar().setHomeButtonEnabled(true);
            }
        } else {
            mLoaderID = 0;
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                return true;
            case R.id.add_item:
                Intent i = new Intent(NoteListActivity.this, DetailActivity.class);
                startActivity(i);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        Uri uri;
        if (id == 0) {
            uri = Uri.parse(NoteContentProvider.CONTENT_URI + NoteContentProvider.NOTE_BASE);

        } else {
            uri = Uri.parse(NoteContentProvider.CONTENT_URI + NoteContentProvider.LABEL_NOTES_BASE
                    + "/" + mLabel.id);
        }
        return new CursorLoader(this, uri, null, null, null, null);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        int i = loader.getId();
        ArrayList<Note> notes;

        if (i == 0) {
            notes = UtilDatabase.getNotes(data);
        } else {
            notes = UtilDatabase.getNotesByID(this, data);
        }

        if (notes.isEmpty()) {
            findViewById(R.id.list).setVisibility(View.INVISIBLE);
            findViewById(R.id.no_content_view).setVisibility(View.VISIBLE);
        } else {
            findViewById(R.id.list).setVisibility(View.VISIBLE);
            findViewById(R.id.no_content_view).setVisibility(View.INVISIBLE);
            mAdapter.refreshContent(notes);
        }
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {

    }
}
