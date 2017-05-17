package com.acando.notegen;

import android.content.DialogInterface;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import com.acando.notegen.api.NoteContentProvider;
import com.acando.notegen.database.UtilDatabase;
import com.acando.notegen.internal.Label;
import com.acando.notegen.internal.LabelDialog;
import com.acando.notegen.internal.LabelListAdapter;

import java.util.ArrayList;

public class LabelListActivity extends AppCompatActivity implements
        LoaderManager.LoaderCallbacks<Cursor>, DialogInterface.OnDismissListener {

    private LabelListAdapter mAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_list);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        if(getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setHomeButtonEnabled(true);
        }

        RecyclerView recyclerView = (RecyclerView) findViewById(R.id.list);
        recyclerView.setHasFixedSize(true);

        LinearLayoutManager layoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        recyclerView.setLayoutManager(layoutManager);

        mAdapter = new LabelListAdapter(this, new ArrayList<Label>(), getIntent().getIntExtra("note_id", -1));
        recyclerView.setAdapter(mAdapter);

        ArrayList<Label> activeLabels = (ArrayList<Label>) getIntent().getSerializableExtra("active_labels");
        if(activeLabels != null) {
            mAdapter.setActiveLabels(activeLabels);
        } else {
            mAdapter.setActiveLabels(new ArrayList<Label>());
        }

        getSupportLoaderManager().initLoader(0, null, this);
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
                LabelDialog.newInstance(null).show(getSupportFragmentManager(), "LabelDialog");
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        Uri uri = Uri.parse(NoteContentProvider.CONTENT_URI + NoteContentProvider.LABEL_BASE);
        return new CursorLoader(this, uri, null, null, null, null);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        ArrayList<Label> labels = UtilDatabase.getLabels(data);
        if (labels.isEmpty()) {
            findViewById(R.id.list).setVisibility(View.INVISIBLE);
            findViewById(R.id.no_content_view).setVisibility(View.VISIBLE);
        } else {
            findViewById(R.id.list).setVisibility(View.VISIBLE);
            findViewById(R.id.no_content_view).setVisibility(View.INVISIBLE);
            mAdapter.refreshContent(labels);
        }
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {

    }

    @Override
    public void onDismiss(DialogInterface dialog) {
        getSupportLoaderManager().destroyLoader(0);
        getSupportLoaderManager().initLoader(0, null, this);
    }
}
