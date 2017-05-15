package com.acando.todohelper.internal;

import android.content.Context;
import android.support.design.widget.Snackbar;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.acando.todohelper.R;

import java.util.ArrayList;

public class ListAdapter extends RecyclerView.Adapter<ListAdapter.ToDoViewHolder> {

    private ArrayList<ToDo> mToDoEntries;
    private Context mContext;

    public ListAdapter(Context context, ArrayList<ToDo> toDoEntries) {
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

    public void refreshContent(ArrayList<ToDo> toDoEntries) {
        mToDoEntries = toDoEntries;
        notifyDataSetChanged();
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
            title = (TextView) view.findViewById(R.id.title);
            text = (TextView) view.findViewById(R.id.text);
            view.findViewById(R.id.todo_entry_layout).setOnClickListener(this);
        }

        public void fillContent(ToDo entry) {
            title.setText(entry.title);
            text.setText(entry.text);
        }

        @Override
        public void onClick(View v) {
            Snackbar.make(v, "Test successful", Snackbar.LENGTH_SHORT).show();
        }
    }
}
