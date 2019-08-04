package com.daniel.ethan.paymentmanager;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import androidx.fragment.app.DialogFragment;

import static com.daniel.ethan.paymentmanager.Utils.isFilled;

@SuppressLint("ValidFragment")
public class EditEnvelopeDialog extends DialogFragment {

    EditText envelopeName;
    EditText envelopeAmount;
    EditText envelopeAutoUpdate;
    String name;
    String amount;
    String autoUpdate;
    EditEnvelopeDialogListener listener;

    public EditEnvelopeDialog(String name, String amount, String autoUpdate, EditEnvelopeDialogListener listener) {
        this.name = name;
        this.amount = amount;
        this.autoUpdate = autoUpdate;
        this.listener = listener;
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        View view = LayoutInflater.from(getContext()).inflate(R.layout.dialog_add_envelope,null);
        envelopeName = view.findViewById(R.id.dialog_add_envelope_name);
        envelopeAmount = view.findViewById(R.id.dialog_add_total_amount);
        envelopeAutoUpdate = view.findViewById(R.id.dialog_add_auto_update_amount);

        this.envelopeName.setText(name);
        this.envelopeAmount.setText(amount);
        this.envelopeAutoUpdate.setText(autoUpdate);

        return new AlertDialog.Builder(getContext())
                .setView(view)
                .setTitle("Edit Envelope")
                .setPositiveButton("Set", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        if (isFilled(envelopeName, envelopeAmount, envelopeAutoUpdate)) {
                            String name = envelopeName.getText().toString();
                            Double amount = Double.parseDouble(envelopeAmount.getText().toString());
                            Double autoUpdate = Double.parseDouble(envelopeAutoUpdate.getText().toString());

                            listener.onEditEnvelope(name, amount, autoUpdate);
                            Toast.makeText(getContext(), envelopeName.getText().toString() + " created", Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(getContext(), "user needs to fill out all fields", Toast.LENGTH_SHORT).show();
                        }
                    }
                })
                .setNegativeButton("Cancel", null)
                .show();
    }

    public interface EditEnvelopeDialogListener {
        void onEditEnvelope(String name, Double amount, Double autoUpdate);
    }
}
