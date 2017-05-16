package com.acando.todohelper.internal;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.TextView;

import com.acando.todohelper.R;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.TimeZone;

public class DetailActivity extends AppCompatActivity {

    private ToDo mToDoItem;
    private EditText mTitle, mText;
    private boolean mHasUpdateItem;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        if(getSupportActionBar() != null) {
            getSupportActionBar().setHomeButtonEnabled(true);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        mTitle = (EditText) findViewById(R.id.title);
        mTitle.addTextChangedListener(new CustomTextWatcher());
        mText = (EditText) findViewById(R.id.text);
        mText.addTextChangedListener(new CustomTextWatcher());
        TextView lastModify = (TextView) findViewById(R.id.last_modify);

        mToDoItem = (ToDo) getIntent().getSerializableExtra("todo_object");
        if(mToDoItem != null) {
            mTitle.setText(mToDoItem.title);
            mText.setText(mToDoItem.text);

            Calendar cal = new GregorianCalendar();
            cal.setTimeZone(TimeZone.getDefault());
            cal.setTimeInMillis(mToDoItem.lastModifyDate);
            SimpleDateFormat formatter = new SimpleDateFormat("dd.MM.yyyy HH:mm");
            String formatted = formatter.format(cal.getTime());
            lastModify.setText("Last modify: " + formatted);
        } else {
            Calendar cal = Calendar.getInstance();
            cal.setTimeZone(TimeZone.getDefault());
            cal.setTimeInMillis(System.currentTimeMillis());
            SimpleDateFormat formatter = new SimpleDateFormat("dd.MM.yyyy HH:mm");
            String formatted = formatter.format(cal.getTime());
            lastModify.setText("Last modify: " + formatted);
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
    protected void onPause() {
        if(mHasUpdateItem) {
            Intent intent = new Intent(this, SyncService.class);
            intent.putExtra("title", mTitle.getText().toString());
            intent.putExtra("text", mText.getText().toString());

            if(mToDoItem != null) {
                intent.putExtra("id", mToDoItem.id);
            }
            startService(intent);
        }
        super.onPause();
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
