package com.acando.todohelper.internal;

import android.content.Context;
import android.content.Intent;
import android.support.design.widget.Snackbar;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.acando.todohelper.R;

import java.util.ArrayList;

public class ToDoListAdapter extends RecyclerView.Adapter<ToDoListAdapter.ToDoViewHolder> {

    private ArrayList<ToDo> mToDoEntries;
    private Context mContext;

    public ToDoListAdapter(Context context, ArrayList<ToDo> toDoEntries) {
        mContext = context;
        mToDoEntries = toDoEntries;
    }

    @Override
    public ToDoListAdapter.ToDoViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_todo_entry, null);
        return new ToDoViewHolder(v);
    }

    @Override
    public void onBindViewHolder(ToDoListAdapter.ToDoViewHolder holder, int position) {
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
            Intent intent = new Intent(mContext, DetailActivity.class);
            intent.putExtra("todo_object", mToDoEntries.get(getAdapterPosition()));
            mContext.startActivity(intent);
        }
    }
}
