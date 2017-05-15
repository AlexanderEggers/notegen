package com.acando.todohelper.internal;

import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.acando.todohelper.R;
import com.acando.todohelper.api.ToDoContentProvider;

import java.util.ArrayList;

public class ToDoFragment extends Fragment implements LoaderManager.LoaderCallbacks<Cursor> {

    private ListAdapter adapter;

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        Uri uri = Uri.parse(ToDoContentProvider.CONTENT_URI + ToDoContentProvider.TODO_BASE);
        return new CursorLoader(getActivity(), uri, null, null, null, null);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        data.moveToFirst();
        ArrayList<ToDo> toDoItems = new ArrayList<>();
        for(int i = 0; i < data.getCount(); i++) {
            ToDo item = new ToDo();
            item.id = data.getInt(0);
            item.title = data.getString(1);
            item.text = data.getString(2);
            //item.image = data.getBlob(3); TODO
            item.creationDate = data.getLong(4);
            item.lastModifyDate = data.getLong(5);
            toDoItems.add(item);
            data.moveToNext();
        }
        adapter.refreshContent(toDoItems);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {

    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getLoaderManager().initLoader(0, null, this);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.todo_content, container, false);

        RecyclerView recyclerView = (RecyclerView) v.findViewById(R.id.list);
        recyclerView.setHasFixedSize(true);

        LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity());
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        recyclerView.setLayoutManager(layoutManager);

        adapter = new ListAdapter(getActivity(), new ArrayList<ToDo>());
        recyclerView.setAdapter(adapter);

        return v;
    }
}
