package com.acando.notegen.internal;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.acando.notegen.DetailActivity;
import com.acando.notegen.R;

import java.util.ArrayList;

public class NoteListAdapter extends RecyclerView.Adapter<NoteListAdapter.ToDoViewHolder> {

    private ArrayList<Note> mNoteEntries;
    private Context mContext;

    public NoteListAdapter(Context context, ArrayList<Note> noteEntries) {
        mContext = context;
        mNoteEntries = noteEntries;
    }

    @Override
    public NoteListAdapter.ToDoViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_note_entry, null);
        return new ToDoViewHolder(v);
    }

    @Override
    public void onBindViewHolder(NoteListAdapter.ToDoViewHolder holder, int position) {
        holder.fillContent(mNoteEntries.get(position));
    }

    public void refreshContent(ArrayList<Note> noteEntries) {
        mNoteEntries = noteEntries;
        notifyDataSetChanged();
    }

    @Override
    public int getItemCount() {
        return mNoteEntries != null ? mNoteEntries.size() : 0;
    }

    protected class ToDoViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        private TextView title, text;

        public ToDoViewHolder(View view) {
            super(view);
            title = (TextView) view.findViewById(R.id.title);
            text = (TextView) view.findViewById(R.id.text);
            view.findViewById(R.id.todo_entry_layout).setOnClickListener(this);
        }

        public void fillContent(Note entry) {
            if(entry.title != null && !entry.title.equals("")) {
                title.setText(entry.title);
                title.setVisibility(View.VISIBLE);
            } else {
                title.setVisibility(View.GONE);
            }

            if(entry.text != null && !entry.text.equals("")) {
                text.setText(entry.text);
                text.setVisibility(View.VISIBLE);
            } else {
                text.setVisibility(View.GONE);
            }
        }

        @Override
        public void onClick(View v) {
            Intent intent = new Intent(mContext, DetailActivity.class);
            intent.putExtra("todo_object", mNoteEntries.get(getAdapterPosition()));
            mContext.startActivity(intent);
        }
    }
}
