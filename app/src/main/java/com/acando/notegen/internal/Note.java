package com.acando.notegen.internal;

import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;

import java.io.ByteArrayOutputStream;
import java.io.Serializable;

public class Note implements Serializable {
    public int id, isArchive, isTrash;
    public String title, text;
    public long lastModifyDate;
    public byte[] imageByte;

    public byte[] convertImage(Drawable drawable) {
        Bitmap bmp = ((BitmapDrawable) drawable).getBitmap();
        return convertImage(bmp);
    }

    public byte[] convertImage(Bitmap bmp) {
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        bmp.compress(Bitmap.CompressFormat.JPEG, 100, stream);
        imageByte = stream.toByteArray();
        return imageByte;
    }
}
