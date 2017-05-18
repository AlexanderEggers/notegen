package com.acando.notegen.database;

import android.content.ContentProviderClient;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.os.RemoteException;

import com.acando.notegen.api.NoteContentProvider;
import com.acando.notegen.internal.Label;
import com.acando.notegen.internal.Note;

import java.util.ArrayList;

public class UtilDatabase {

    public static void addLabelToNote(Context context, int labelID, int noteID) {
        ContentProviderClient cr = context.getContentResolver().acquireContentProviderClient(
                NoteContentProvider.AUTHORITY);
        Uri uri = Uri.parse(NoteContentProvider.CONTENT_URI + NoteContentProvider.NOTE_LABELS_BASE + "/" + noteID);

        ContentValues values = new ContentValues();
        values.put(NoteLabelTable.COLUMN_NOTE_ID, noteID);
        values.put(NoteLabelTable.COLUMN_LABEL_ID, labelID);

        try {
            cr.insert(uri, values);
        } catch (RemoteException e) {
            e.printStackTrace();
        }

        cr.release();
    }

    public static void deleteLabelFromNote(Context context, int labelID, int noteID) {
        ContentProviderClient cr = context.getContentResolver().acquireContentProviderClient(
                NoteContentProvider.AUTHORITY);
        Uri uri = Uri.parse(NoteContentProvider.CONTENT_URI + NoteContentProvider.NOTE_LABELS_BASE + "/" + noteID);

        ContentValues values = new ContentValues();
        values.put(NoteLabelTable.COLUMN_NOTE_ID, noteID);
        values.put(NoteLabelTable.COLUMN_LABEL_ID, labelID);

        try {
            cr.delete(uri, NoteLabelTable.COLUMN_NOTE_ID + " = " + noteID + " AND "
                    + NoteLabelTable.COLUMN_LABEL_ID + " = " + labelID, null);
        } catch (RemoteException e) {
            e.printStackTrace();
        }

        cr.release();
    }

    public static void insertLabel(Context context, String name) {
        ContentProviderClient cr = context.getContentResolver().acquireContentProviderClient(
                NoteContentProvider.AUTHORITY);
        Uri uri = Uri.parse(NoteContentProvider.CONTENT_URI + NoteContentProvider.LABEL_BASE);

        ContentValues values = new ContentValues();
        values.put(LabelTable.COLUMN_INTERNAL_NAME, name.toLowerCase());
        values.put(LabelTable.COLUMN_NAME, name);

        try {
            cr.insert(uri, values);
        } catch (RemoteException e) {
            e.printStackTrace();
        }

        cr.release();
    }

    public static int insertNote(Context context, Note note) {
        ContentValues values = new ContentValues();
        values.put(NoteTable.COLUMN_TITLE, note.title);
        values.put(NoteTable.COLUMN_TEXT, note.text);
        values.put(NoteTable.COLUMN_ARCHIVE, false);
        values.put(NoteTable.COLUMN_BIN, false);
        values.put(NoteTable.COLUMN_LAST_MODIFY, System.currentTimeMillis());

        if(note.imageByte != null && note.imageByte.length != 0) {
            values.put(NoteTable.COLUMN_IMAGE, note.imageByte);
        }

        ContentProviderClient cr = context.getContentResolver().acquireContentProviderClient(
                NoteContentProvider.AUTHORITY);
        Uri uri = Uri.parse(NoteContentProvider.CONTENT_URI + NoteContentProvider.NOTE_BASE);

        int newID = -1;
        try {
            newID = Integer.parseInt(cr.insert(uri, values).getLastPathSegment());
        } catch (Exception e) {
            e.printStackTrace();
        }

        cr.release();
        return newID;
    }

    public static void updateLabel(Context context, Label label, String newName) {
        ContentProviderClient cr = context.getContentResolver().acquireContentProviderClient(
                NoteContentProvider.AUTHORITY);
        Uri uri = Uri.parse(NoteContentProvider.CONTENT_URI + NoteContentProvider.LABEL_BASE + "/" + label.id);

        ContentValues values = new ContentValues();
        values.put(LabelTable.COLUMN_INTERNAL_NAME, newName.toLowerCase());
        values.put(LabelTable.COLUMN_NAME, newName);

        try {
            cr.update(uri, values, null, null);
        } catch (RemoteException e) {
            e.printStackTrace();
        }

        cr.release();
    }

    public static void updateNote(Context context, Note note) {
        ContentProviderClient cr = context.getContentResolver().acquireContentProviderClient(
                NoteContentProvider.AUTHORITY);
        Uri uri = Uri.parse(NoteContentProvider.CONTENT_URI + NoteContentProvider.NOTE_BASE + "/" + note.id);

        ContentValues values = new ContentValues();
        values.put(NoteTable.COLUMN_TITLE, note.title);
        values.put(NoteTable.COLUMN_TEXT, note.text);
        values.put(NoteTable.COLUMN_ARCHIVE, note.isArchive);
        values.put(NoteTable.COLUMN_BIN, note.isTrash);
        values.put(NoteTable.COLUMN_LAST_MODIFY, System.currentTimeMillis());

        if(note.imageByte != null && note.imageByte.length != 0) {
            values.put(NoteTable.COLUMN_IMAGE, note.imageByte);
        }

        try {
            cr.update(uri, values, null, null);
        } catch (RemoteException e) {
            e.printStackTrace();
        }

        cr.release();
    }

    public static ArrayList<Note> getNotes(Cursor data) {
        ArrayList<Note> noteItems = new ArrayList<>();
        if(data.moveToFirst()) {
            for(int i = 0; i < data.getCount(); i++) {
                Note item = new Note();
                item.id = data.getInt(0);
                item.title = data.getString(1);
                item.text = data.getString(2);
                item.isArchive = data.getInt(3);
                item.isTrash = data.getInt(4);
                item.imageByte = data.getBlob(5);
                item.lastModifyDate = data.getLong(6);
                noteItems.add(item);
                data.moveToNext();
            }
        }
        return noteItems;
    }

    public static ArrayList<Note> getNotesByID(Context context, Cursor data) {
        ContentProviderClient cr = context.getContentResolver().acquireContentProviderClient(
                NoteContentProvider.AUTHORITY);
        ArrayList<Note> notes = new ArrayList<>();

        if(data.moveToFirst()) {
            for(int i = 0; i < data.getCount(); i++) {
                Cursor noteData = getNote(cr, data.getInt(NoteLabelTable.COLUMN_TODO_ID_INDEX));
                if(noteData != null && noteData.moveToFirst()) {
                    Note item = new Note();
                    item.id = noteData.getInt(0);
                    item.title = noteData.getString(1);
                    item.text = noteData.getString(2);
                    item.isArchive = noteData.getInt(3);
                    item.isTrash = noteData.getInt(4);
                    item.imageByte = noteData.getBlob(5);
                    item.lastModifyDate = noteData.getLong(6);
                    notes.add(item);
                    noteData.close();
                }
                data.moveToNext();
            }
        }

        cr.release();
        return notes;
    }

    public static Cursor getNote(ContentProviderClient cr, int id) {
        Uri uri = Uri.parse(NoteContentProvider.CONTENT_URI + NoteContentProvider.NOTE_BASE + "/" + id);
        Cursor cursor = null;

        try {
            cursor = cr.query(uri, null, null, null, null);
        } catch (RemoteException e) {
            e.printStackTrace();
        }
        return cursor;
    }

    public static ArrayList<Label> getLabels(Cursor data) {
        ArrayList<Label> labels = new ArrayList<>();
        if(data.moveToFirst()) {
            for(int i = 0; i < data.getCount(); i++) {
                Label label = new Label();
                label.id = data.getInt(0);
                label.internalName = data.getString(1);
                label.name = data.getString(2);
                labels.add(label);
                data.moveToNext();
            }
        }
        return labels;
    }

    public static ArrayList<Label> getLabelsByID(Context context, Cursor data) {
        ContentProviderClient cr = context.getContentResolver().acquireContentProviderClient(
                NoteContentProvider.AUTHORITY);
        ArrayList<Label> labels = new ArrayList<>();

        if(data.moveToFirst()) {
            for(int i = 0; i < data.getCount(); i++) {
                Cursor labelData = getLabel(cr, data.getInt(2));
                if(labelData != null && labelData.moveToFirst()) {
                    Label label = new Label();
                    label.id = labelData.getInt(0);
                    label.internalName = labelData.getString(1);
                    label.name = labelData.getString(2);
                    labels.add(label);
                    labelData.close();
                }
                data.moveToNext();
            }
        }

        cr.release();
        return labels;
    }

    private static Cursor getLabel(ContentProviderClient cr, int id) {
        Uri uri = Uri.parse(NoteContentProvider.CONTENT_URI + NoteContentProvider.LABEL_BASE + "/" + id);

        try {
            return cr.query(uri, null, null, null, null);
        } catch (RemoteException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static void deleteNote(Context context, int id) {
        ContentProviderClient cr = context.getContentResolver().acquireContentProviderClient(
                NoteContentProvider.AUTHORITY);
        Uri deleteNoteURI = Uri.parse(NoteContentProvider.CONTENT_URI
                + NoteContentProvider.NOTE_BASE + "/" + id);
        Uri deleteLabelsURI = Uri.parse(NoteContentProvider.CONTENT_URI
                + NoteContentProvider.NOTE_LABELS_BASE + "/" + id);

        try {
            cr.delete(deleteNoteURI, null, null);
            cr.delete(deleteLabelsURI, NoteLabelTable.COLUMN_NOTE_ID + " = " + id, null);
        } catch (RemoteException e) {
            e.printStackTrace();
        }
        cr.release();
    }

    public static void deleteLabel(Context context, int id) {
        ContentProviderClient cr = context.getContentResolver().acquireContentProviderClient(
                NoteContentProvider.AUTHORITY);
        Uri deleteLabelURI = Uri.parse(NoteContentProvider.CONTENT_URI
                + NoteContentProvider.LABEL_BASE + "/" + id);
        Uri deleteNotesConURI = Uri.parse(NoteContentProvider.CONTENT_URI
                + NoteContentProvider.LABEL_NOTES_BASE + "/" + id);

        try {
            cr.delete(deleteLabelURI, null, null);
            cr.delete(deleteNotesConURI, NoteLabelTable.COLUMN_LABEL_ID + " = " + id, null);
        } catch (RemoteException e) {
            e.printStackTrace();
        }
        cr.release();
    }
}
