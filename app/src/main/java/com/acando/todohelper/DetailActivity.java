package com.acando.todohelper;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
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

import com.acando.todohelper.internal.SyncService;
import com.acando.todohelper.internal.ToDo;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.TimeZone;

public class DetailActivity extends AppCompatActivity {

    private ToDo mToDoItem;
    private EditText mTitle, mText;
    private ImageView mImage;
    private FloatingActionButton mAddImageFab;
    private boolean mHasUpdateItem;

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
        mTitle.addTextChangedListener(new CustomTextWatcher());
        mText = (EditText) findViewById(R.id.text);
        mText.addTextChangedListener(new CustomTextWatcher());
        mImage = (ImageView) findViewById(R.id.image);
        mAddImageFab = (FloatingActionButton) findViewById(R.id.add_image);
        TextView lastModify = (TextView) findViewById(R.id.last_modify);

        mToDoItem = (ToDo) getIntent().getSerializableExtra("todo_object");
        if (mToDoItem != null) {
            mTitle.setText(mToDoItem.title);
            mText.setText(mToDoItem.text);

            if(mToDoItem.imageByte != null && mToDoItem.imageByte.length != 0) {
                mImage.setVisibility(View.VISIBLE);
                mAddImageFab.setVisibility(View.INVISIBLE);
                mImage.setImageBitmap(BitmapFactory.decodeByteArray(mToDoItem.imageByte, 0,
                        mToDoItem.imageByte.length));
            }

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
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    public void addImage(View view) {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "Select Picture"), 0);
    }

    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 0) {
            if (resultCode == Activity.RESULT_OK) {
                if (data != null) {
                    try {
                        Bitmap bmp = MediaStore.Images.Media.getBitmap(getContentResolver(), data.getData());
                        mImage.setImageBitmap(bmp);
                        ByteArrayOutputStream stream = new ByteArrayOutputStream();
                        bmp.compress(Bitmap.CompressFormat.JPEG, 100, stream);
                        mToDoItem.imageByte = stream.toByteArray();
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
        if (mHasUpdateItem) {
            Intent intent = new Intent(this, SyncService.class);
            intent.putExtra("title", mTitle.getText().toString());
            intent.putExtra("text", mText.getText().toString());

            if(mImage.getVisibility() == View.VISIBLE) {
                Drawable d = mImage.getDrawable();
                Bitmap bmp = ((BitmapDrawable)d).getBitmap();
                ByteArrayOutputStream stream = new ByteArrayOutputStream();
                bmp.compress(Bitmap.CompressFormat.JPEG, 100, stream);
                intent.putExtra("image", stream.toByteArray());
            }

            if (mToDoItem != null) {
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
