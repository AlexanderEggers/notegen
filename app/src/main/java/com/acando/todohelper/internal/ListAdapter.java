package com.acando.todohelper.internal;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.acando.todohelper.R;

import java.util.ArrayList;

public class ListAdapter extends RecyclerView.Adapter<ListAdapter.ToDoViewHolder> {

    private ArrayList<ToDoEntry> mToDoEntries;
    private Context mContext;

    public ListAdapter(Context context, ArrayList<ToDoEntry> toDoEntries) {
        mContext = context;
        mToDoEntries = toDoEntries;
    }

    @Override
    public ListAdapter.ToDoViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.todo_list_entry, null);
        return new ToDoViewHolder(v);
    }

    @Override
    public void onBindViewHolder(ListAdapter.ToDoViewHolder holder, int position) {
        holder.fillContent(mToDoEntries.get(position));
    }

    @Override
    public int getItemCount() {
        return mToDoEntries != null ? mToDoEntries.size() : 0;
    }

    protected class ToDoViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        private TextView title, text;
        private LinearLayout labelLayout;

        public ToDoViewHolder(View view) {
            super(view);
        }

        public void fillContent(ToDoEntry entry) {
            title.setText(entry.title);
            text.setText(entry.text);

            //Fetch labels from database according to the ids and add it's name to the linearlayout
        }

        @Override
        public void onClick(View v) {
            //open todo detail view
        }
    }
}
