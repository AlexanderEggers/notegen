package com.acando.notegen;

import android.app.Activity;
import android.content.ContentProviderClient;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.acando.notegen.api.NoteContentProvider;
import com.acando.notegen.database.NoteTable;
import com.acando.notegen.database.UtilDatabase;
import com.acando.notegen.internal.Label;
import com.acando.notegen.internal.LabelDetailAdapter;
import com.acando.notegen.internal.Note;
import com.acando.notegen.widget.WidgetProvider;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.TimeZone;

public class DetailActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor> {

    private Note mNoteItem;
    private EditText mTitle, mText;
    private TextView mLastModify;
    private ImageView mImage;
    private boolean mHasUpdateItem;
    private LabelDetailAdapter adapter;

    @Override
    protected void onResume() {
        super.onResume();

        if(mNoteItem.id != -1) {
            ContentProviderClient cr = getContentResolver().acquireContentProviderClient(
                    NoteContentProvider.AUTHORITY);
            ArrayList<Note> updatedNote = UtilDatabase.getNotes(UtilDatabase.getNote(cr, mNoteItem.id));
            if(!updatedNote.isEmpty()) {
                mNoteItem = updatedNote.get(0);
                updateExistingNote();
                getSupportLoaderManager().destroyLoader(0);
                getSupportLoaderManager().initLoader(0, null, this);
                mHasUpdateItem = false;
            } else {
                mHasUpdateItem = false;
                finish();
            }
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        if (getSupportActionBar() != null) {
            getSupportActionBar().setHomeButtonEnabled(true);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        mTitle = (EditText) findViewById(R.id.title);
        mText = (EditText) findViewById(R.id.text);
        mImage = (ImageView) findViewById(R.id.image);
        mLastModify = (TextView) findViewById(R.id.last_modify);

        RecyclerView recyclerView = (RecyclerView) findViewById(R.id.label_list);
        recyclerView.setHasFixedSize(false);

        StaggeredGridLayoutManager layoutManager = new StaggeredGridLayoutManager(4, StaggeredGridLayoutManager.VERTICAL);
        recyclerView.setLayoutManager(layoutManager);

        adapter = new LabelDetailAdapter(this, new ArrayList<Label>());
        recyclerView.setAdapter(adapter);

        mNoteItem = new Note();
        mNoteItem.id = getIntent().getIntExtra("note_id", -1);

        if (mNoteItem != null) {
            updateExistingNote();
        } else {
            createNewNote();
        }

        mTitle.addTextChangedListener(new CustomTextWatcher());
        mText.addTextChangedListener(new CustomTextWatcher());
    }

    private void updateExistingNote() {
        mTitle.setText(mNoteItem.title);
        mText.setText(mNoteItem.text);

        if(mNoteItem.imageByte != null && mNoteItem.imageByte.length != 0) {
            mImage.setVisibility(View.VISIBLE);
            mImage.setImageBitmap(BitmapFactory.decodeByteArray(mNoteItem.imageByte, 0,
                    mNoteItem.imageByte.length));
        }

        Calendar cal = new GregorianCalendar();
        cal.setTimeZone(TimeZone.getDefault());
        cal.setTimeInMillis(mNoteItem.lastModifyDate);
        SimpleDateFormat formatter = new SimpleDateFormat("dd.MM.yyyy HH:mm");
        String formatted = formatter.format(cal.getTime());
        mLastModify.setText("Last modify: " + formatted);
    }

    private void createNewNote() {
        mNoteItem = new Note();
        mNoteItem.id = -1;
        mNoteItem.title = "";
        mNoteItem.text = "";

        Calendar cal = Calendar.getInstance();
        cal.setTimeZone(TimeZone.getDefault());
        cal.setTimeInMillis(System.currentTimeMillis());
        SimpleDateFormat formatter = new SimpleDateFormat("dd.MM.yyyy HH:mm");
        String formatted = formatter.format(cal.getTime());
        mLastModify.setText("Last modify: " + formatted);
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        if(mNoteItem.id != -1) {
            if(mNoteItem.isArchive == NoteTable.TRUE) {
                menu.findItem(R.id.archive).setTitle("Restore Note");
                menu.findItem(R.id.delete).setVisible(false);
            } else {
                menu.findItem(R.id.archive).setTitle("Archive Note");
                menu.findItem(R.id.delete).setVisible(true);
            }

            if(mNoteItem.isTrash == NoteTable.TRUE) {
                menu.findItem(R.id.delete).setTitle("Restore Note");
                menu.findItem(R.id.archive).setVisible(false);
            } else {
                menu.findItem(R.id.delete).setTitle("Delete Note");
                menu.findItem(R.id.archive).setVisible(true);
            }
        }
        return super.onPrepareOptionsMenu(menu);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.detail_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                return true;
            case R.id.attach_image:
                Intent intent = new Intent();
                intent.setType("image/*");
                intent.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(Intent.createChooser(intent, "Select Picture"), 0);
                return true;
            case R.id.delete:
                if(mNoteItem != null) {
                    UtilDatabase.deleteNote(this, mNoteItem.id);
                }
                mHasUpdateItem = false;
                onBackPressed();
                return true;
            case R.id.add_label:
                intent = new Intent(this, LabelListActivity.class);
                intent.putExtra("active_labels", adapter.getList());
                intent.putExtra("note_id", syncItem().id);
                startActivity(intent);
                return true;
            case R.id.share:
                intent = new Intent(Intent.ACTION_SEND);
                intent.setType("text/plain");
                intent.putExtra(Intent.EXTRA_EMAIL, "");
                intent.putExtra(Intent.EXTRA_CC, "");
                intent.putExtra(Intent.EXTRA_TITLE, mNoteItem.title);
                intent.putExtra(Intent.EXTRA_TEXT, mNoteItem.text);
                startActivity(Intent.createChooser(intent, "Share note via"));
                return true;
            case R.id.archive:
                mNoteItem.isArchive = mNoteItem.isArchive + 1 % 2;
                mHasUpdateItem = true;

                intent = new Intent(this, BinArchiveListActivity.class);
                intent.putExtra("isArchive", true);
                startActivity(intent);
                finish();
                return true;
            case R.id.bin:
                mNoteItem.isTrash = mNoteItem.isArchive + 1 % 2;
                mHasUpdateItem = true;

                intent = new Intent(this, BinArchiveListActivity.class);
                intent.putExtra("isArchive", false);
                startActivity(intent);
                finish();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 0) {
            if (resultCode == Activity.RESULT_OK) {
                if (data != null) {
                    try {
                        Bitmap bmp = MediaStore.Images.Media.getBitmap(getContentResolver(), data.getData());
                        mImage.setImageBitmap(bmp);
                        mImage.setVisibility(View.VISIBLE);
                        mNoteItem.convertImage(bmp);
                        mHasUpdateItem = true;
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            } else if (resultCode == Activity.RESULT_CANCELED) {
                Toast.makeText(this, "Cancelled", Toast.LENGTH_SHORT).show();
            }
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        syncItem();
    }

    public Note syncItem() {
        if (mHasUpdateItem) {
            mHasUpdateItem = false;

            mNoteItem.title = mTitle.getText().toString();
            mNoteItem.text = mText.getText().toString();

            if (mNoteItem.id == -1) {
                mNoteItem.id = UtilDatabase.insertNote(this, mNoteItem);
            } else {
                UtilDatabase.updateNote(this, mNoteItem);
            }
        }

        WidgetProvider.updateWidgets(this);

        return mNoteItem;
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        Uri uri = Uri.parse(NoteContentProvider.CONTENT_URI + NoteContentProvider.NOTE_LABELS_BASE
                + "/" + mNoteItem.id);
        return new CursorLoader(this, uri, null, null, null, null);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        ArrayList<Label> labels = UtilDatabase.getLabelsByID(this, data);
        adapter.refreshContent(labels);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {

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
            mHasUpdateItem = true;
        }
    }
}
