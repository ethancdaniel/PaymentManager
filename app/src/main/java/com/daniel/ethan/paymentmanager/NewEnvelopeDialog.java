package com.daniel.ethan.paymentmanager;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

public class NewEnvelopeDialog extends DialogFragment {
    EditText envelopeName;
    EditText envelopeAmount;
    EditText envelopeAutoUpdate;
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        View view = LayoutInflater.from(getContext()).inflate(R.layout.dialog_add_envelope,null);
        envelopeName = view.findViewById(R.id.dialog_add_envelope_name);
        envelopeAmount = view.findViewById(R.id.dialog_add_total_amount);
        envelopeAutoUpdate = view.findViewById(R.id.dialog_add_auto_update_amount);

        return new AlertDialog.Builder(getContext())
                .setView(view)
                .setTitle("Create New Envelope")
                .setPositiveButton("Create", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        if (isFilled(envelopeName, envelopeAmount, envelopeAutoUpdate)) {
                            Toast.makeText(getContext(), envelopeName.toString() + " created", Toast.LENGTH_SHORT).show();
                        }
                        Toast.makeText(getContext(), "user needs to fill out all fields", Toast.LENGTH_SHORT).show();
                    }
                })
                .setNegativeButton("Cancel", null)
                .show();
    }

    private boolean isFilled(EditText... editTextCollection) {
        for (EditText editText : editTextCollection) {
            if (editText.getText().toString().isEmpty()) {
                return false;
            }
        }
        return true;
    }
}
