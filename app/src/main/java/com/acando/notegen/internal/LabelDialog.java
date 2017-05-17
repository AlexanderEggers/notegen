package com.acando.notegen.internal;

import android.app.Activity;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.acando.notegen.LabelListActivity;
import com.acando.notegen.R;
import com.acando.notegen.database.UtilDatabase;

public class LabelDialog extends DialogFragment {

    private static final String ARG_LABEL = "label";

    public static LabelDialog newInstance(Label label) {
        LabelDialog fragment = new LabelDialog();
        Bundle args = new Bundle();
        args.putSerializable(ARG_LABEL, label);
        fragment.setArguments(args);
        return fragment;
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        final AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = getActivity().getLayoutInflater();

        final Label label = (Label) getArguments().getSerializable(ARG_LABEL);

        final View v = inflater.inflate(R.layout.dialog_add_edit, null);
        v.findViewById(R.id.dialog_title).setVisibility(View.VISIBLE);
        ((TextView) v.findViewById(R.id.dialog_title)).setText(label == null ? "Add Label" : "Edit Label");
        ((TextView) v.findViewById(R.id.dialog_editText)).setText(label != null ? label.name : "");

        builder.setView(v)
                .setPositiveButton(label == null ? "Add" : "Edit", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int id) {
                                if(label != null) {
                                    UtilDatabase.updateLabel(getContext(), label, ((EditText)
                                            v.findViewById(R.id.dialog_editText)).getText().toString());
                                } else {
                                    UtilDatabase.insertLabel(getContext(), ((TextView)
                                            v.findViewById(R.id.dialog_editText)).getText().toString());
                                }
                            }
                        }
                ).setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

            }

        });

        if(label != null) {
            builder.setNeutralButton("Delete Label", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    UtilDatabase.deleteLabel(getContext(), label.id);
                }
            });
        }

        AlertDialog alertDialog = builder.create();
        alertDialog.setCanceledOnTouchOutside(true);

        if (alertDialog.getWindow() != null) {
            alertDialog.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE);
        }

        return alertDialog;
    }

    @Override
    public void onDismiss(DialogInterface dialog) {
        super.onDismiss(dialog);
        final Activity activity = getActivity();
        if (activity instanceof DialogInterface.OnDismissListener) {
            ((DialogInterface.OnDismissListener) activity).onDismiss(dialog);
        }
    }
}
