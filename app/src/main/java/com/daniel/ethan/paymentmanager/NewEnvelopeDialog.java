package com.daniel.ethan.paymentmanager;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v7.app.AppCompatDialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import static com.daniel.ethan.paymentmanager.Utils.isFilled;

@SuppressLint("ValidFragment")
public class NewEnvelopeDialog extends AppCompatDialogFragment {
    EditText envelopeName;
    EditText envelopeAmount;
    EditText envelopeAutoUpdate;
    NewEnvelopeDialogListener listener;

    public NewEnvelopeDialog(NewEnvelopeDialogListener listener) {
        this.listener = listener;
    }

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
                            String name = envelopeName.getText().toString();
                            Double amount = Double.parseDouble(envelopeAmount.getText().toString());
                            Double autoUpdate = Double.parseDouble(envelopeAutoUpdate.getText().toString());

                            listener.onCreateEnvelope(name, amount, autoUpdate);
                            Toast.makeText(getContext(), envelopeName.getText().toString() + " created", Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(getContext(), "user needs to fill out all fields", Toast.LENGTH_SHORT).show();
                        }
                    }
                })
                .setNegativeButton("Cancel", null)
                .show();
    }

    public interface NewEnvelopeDialogListener {
        void onCreateEnvelope(String name, Double amount, Double autoUpdate);
    }
}
