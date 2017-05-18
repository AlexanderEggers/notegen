package com.acando.notegen.internal;

import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.acando.notegen.LabelListActivity;
import com.acando.notegen.R;
import com.acando.notegen.database.UtilDatabase;

import java.util.ArrayList;

public class LabelListAdapter extends RecyclerView.Adapter<LabelListAdapter.LabelViewHolder> {

    private LabelListActivity mContext;
    private ArrayList<Label> mLabelEntries, mActiveLabels;
    private int mNoteID;

    public LabelListAdapter(LabelListActivity context, ArrayList<Label> labelEntries, int noteID) {
        mContext = context;
        mLabelEntries = labelEntries;
        mNoteID = noteID;
    }

    @Override
    public LabelListAdapter.LabelViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_label_entry, null);
        return new LabelViewHolder(v);
    }

    @Override
    public void onBindViewHolder(LabelListAdapter.LabelViewHolder holder, int position) {
        boolean isActive = false;
        for(Label activeLabel : mActiveLabels) {
            if(activeLabel.id == mLabelEntries.get(position).id) {
                isActive = true;
                break;
            }
        }
        holder.fillContent(mLabelEntries.get(position), isActive);
    }

    public void refreshContent(ArrayList<Label> labelEntries) {
        mLabelEntries = labelEntries;
        notifyDataSetChanged();
    }

    public void setActiveLabels(ArrayList<Label> activeLabels) {
        mActiveLabels = activeLabels;
    }

    @Override
    public int getItemCount() {
        return mLabelEntries != null ? mLabelEntries.size() : 0;
    }

    protected class LabelViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener,
            View.OnLongClickListener {

        private TextView name;
        private View activeIndicator;
        private boolean isActive;

        public LabelViewHolder(View view) {
            super(view);
            name = (TextView) view.findViewById(R.id.name);

            if(mNoteID != -1) {
                activeIndicator = view.findViewById(R.id.active_indicator);
                view.findViewById(R.id.label_layout).setOnClickListener(this);
            } else {
                view.findViewById(R.id.active_indicator).setVisibility(View.GONE);
            }

            view.findViewById(R.id.label_layout).setOnLongClickListener(this);
        }

        public void fillContent(Label label, boolean isActive) {
            if(mNoteID != -1) {
                this.isActive = isActive;
                activeIndicator.setBackgroundColor(isActive ?
                        ContextCompat.getColor(mContext, R.color.label_active)
                        : ContextCompat.getColor(mContext, R.color.label_not_active));
            }
            name.setText(label.name);
        }

        @Override
        public void onClick(View v) {
            if(!isActive) {
                UtilDatabase.addLabelToNote(mContext, mLabelEntries.get(getAdapterPosition()).id, mNoteID);
            } else {
                UtilDatabase.deleteLabelFromNote(mContext, mLabelEntries.get(getAdapterPosition()).id, mNoteID);
            }
            isActive = !isActive;
            activeIndicator.setBackgroundColor(isActive ?
                    ContextCompat.getColor(mContext, R.color.label_active)
                    : ContextCompat.getColor(mContext, R.color.label_not_active));
        }

        @Override
        public boolean onLongClick(View v) {
            LabelDialog.newInstance(mLabelEntries.get(getAdapterPosition()))
                    .show(mContext.getSupportFragmentManager(), "LabelDialog");
            return true;
        }
    }
}
