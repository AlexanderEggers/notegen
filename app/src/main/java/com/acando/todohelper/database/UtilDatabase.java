package com.acando.todohelper.database;

import android.database.Cursor;
import android.os.RemoteException;

import com.acando.todohelper.internal.Label;
import com.acando.todohelper.internal.ToDo;

import java.util.ArrayList;

public class UtilDatabase {

    public static ArrayList<ToDo> getToDos(Cursor data) {
        data.moveToFirst();
        ArrayList<ToDo> toDoItems = new ArrayList<>();
        for(int i = 0; i < data.getCount(); i++) {
            ToDo item = new ToDo();
            item.id = data.getInt(0);
            item.title = data.getString(1);
            item.text = data.getString(2);
            //item.image = data.getBlob(3); TODO
            item.lastModifyDate = data.getLong(4);
            toDoItems.add(item);
            data.moveToNext();
        }
        return toDoItems;
    }

    public static ArrayList<Label> getLabels(Cursor data) {
        data.moveToFirst();
        ArrayList<Label> labels = new ArrayList<>();
        for(int i = 0; i < data.getCount(); i++) {
            Label label = new Label();
            label.internalName = data.getString(0);
            label.name = data.getString(1);
            labels.add(label);
            data.moveToNext();
        }
        return labels;
    }
}
