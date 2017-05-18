package com.acando.notegen;

import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;

import com.acando.notegen.api.NoteContentProvider;
import com.acando.notegen.database.NoteTable;
import com.acando.notegen.database.UtilDatabase;
import com.acando.notegen.internal.Note;
import com.acando.notegen.internal.NoteListAdapter;

import java.util.ArrayList;

public class BinArchiveListActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor> {

    private NoteListAdapter mAdapter;
    private int mLoaderID;
    private static final int ARCHIVE = 0, BIN = 1;

    @Override
    protected void onResume() {
        super.onResume();
        getSupportLoaderManager().destroyLoader(mLoaderID);
        getSupportLoaderManager().initLoader(mLoaderID, null, this);
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

        boolean isArchive = getIntent().getBooleanExtra("isArchive", false);
        if(isArchive) {
            setTitle("Archive");
            mLoaderID = ARCHIVE;
        } else {
            setTitle("Bin");
            mLoaderID = BIN;
        }

        if(getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setHomeButtonEnabled(true);
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        Uri uri;
        String selection;
        if (id == ARCHIVE) {
            uri = Uri.parse(NoteContentProvider.CONTENT_URI + NoteContentProvider.NOTE_BASE);
            selection = NoteTable.COLUMN_ARCHIVE + " = " + NoteTable.TRUE;
        } else {
            uri = Uri.parse(NoteContentProvider.CONTENT_URI + NoteContentProvider.NOTE_BASE);
            selection = NoteTable.COLUMN_BIN + " = " + NoteTable.TRUE;
        }
        return new CursorLoader(this, uri, null, selection, null, null);
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
