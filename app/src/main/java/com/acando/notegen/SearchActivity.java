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
import android.text.Editable;
import android.text.TextWatcher;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;

import com.acando.notegen.api.NoteContentProvider;
import com.acando.notegen.database.NoteTable;
import com.acando.notegen.database.UtilDatabase;
import com.acando.notegen.internal.Note;
import com.acando.notegen.internal.NoteListAdapter;

import java.util.ArrayList;

public class SearchActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor> {

    private NoteListAdapter mAdapter;
    private EditText mEditText;

    @Override
    protected void onResume() {
        super.onResume();

        if(!mEditText.getText().toString().isEmpty()) {
            getSupportLoaderManager().destroyLoader(0);
            getSupportLoaderManager().initLoader(0, null, SearchActivity.this);
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        RecyclerView recyclerView = (RecyclerView) findViewById(R.id.list);
        recyclerView.setHasFixedSize(false);

        StaggeredGridLayoutManager layoutManager = new StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL);
        recyclerView.setLayoutManager(layoutManager);

        mAdapter = new NoteListAdapter(this, new ArrayList<Note>());
        recyclerView.setAdapter(mAdapter);

        mEditText = (EditText) findViewById(R.id.search_field);
        mEditText.requestFocus();
        mEditText.addTextChangedListener(new SearchActivity.CustomTextWatcher());

        if(getSupportActionBar() != null) {
            getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_arrow_back_black_24dp);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setHomeButtonEnabled(true);
        }
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        Uri uri = Uri.parse(NoteContentProvider.CONTENT_URI + NoteContentProvider.NOTE_BASE);
        String searchWord = mEditText.getText().toString();
        String selection = "(" + NoteTable.COLUMN_TITLE + " LIKE ('%" + searchWord + "%') OR "
                + NoteTable.COLUMN_TEXT + " LIKE ('%" + searchWord + "%')) AND (" +
                NoteTable.COLUMN_ARCHIVE + " = " + NoteTable.FALSE + " AND " +
                NoteTable.COLUMN_BIN + " = " + NoteTable.FALSE + ")";
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

    private class CustomTextWatcher implements TextWatcher {

        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {

        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {

        }

        @Override
        public void afterTextChanged(Editable s) {
            getSupportLoaderManager().destroyLoader(0);
            getSupportLoaderManager().initLoader(0, null, SearchActivity.this);
        }
    }
}
