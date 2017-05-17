package com.acando.notegen.internal;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.acando.notegen.NoteListActivity;
import com.acando.notegen.R;

import java.util.ArrayList;

public class LabelDetailAdapter extends RecyclerView.Adapter<LabelDetailAdapter.LabelViewHolder> {

    private Context mContext;
    private ArrayList<Label> mLabelEntries;

    public LabelDetailAdapter(Context context, ArrayList<Label> labelEntries) {
        mContext = context;
        mLabelEntries = labelEntries;
    }

    @Override
    public LabelDetailAdapter.LabelViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.detail_label_entry, null);
        return new LabelViewHolder(v);
    }

    @Override
    public void onBindViewHolder(LabelDetailAdapter.LabelViewHolder holder, int position) {
        holder.fillContent(mLabelEntries.get(position));
    }

    public void refreshContent(ArrayList<Label> labelEntries) {
        mLabelEntries = labelEntries;
        notifyDataSetChanged();
    }

    @Override
    public int getItemCount() {
        return mLabelEntries != null ? mLabelEntries.size() : 0;
    }

    protected class LabelViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        private TextView name;

        public LabelViewHolder(View view) {
            super(view);
            name = (TextView) view.findViewById(R.id.name);
            view.findViewById(R.id.label_layout).setOnClickListener(this);
        }

        public void fillContent(Label label) {
            name.setText(label.name);
        }

        @Override
        public void onClick(View v) {
            Intent intent = new Intent(mContext, NoteListActivity.class);
            intent.putExtra("label_object", mLabelEntries.get(getAdapterPosition()));
            mContext.startActivity(intent);
        }
    }

    public ArrayList<Label> getList() {
        return mLabelEntries;
    }
}
