package com.daniel.ethan.paymentmanager;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import androidx.fragment.app.DialogFragment;

import com.google.android.material.textfield.TextInputEditText;

import static com.daniel.ethan.paymentmanager.Utils.isFilled;

public class PayEnvelopeDialog extends DialogFragment {
    TextInputEditText payEditText;
    PayEnvelopeDialogListener listener;
    String envelopeName;
    String formattedAmount;

    public PayEnvelopeDialog(String envelopeName, String formattedAmount, PayEnvelopeDialogListener listener) {
        this.listener = listener;
        this.envelopeName = envelopeName;
        this.formattedAmount = formattedAmount;
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        View view = LayoutInflater.from(getContext()).inflate(R.layout.dialog_pay_envelope,null);
        payEditText = view.findViewById(R.id.edit_pay_envelope);
        payEditText.setHint(formattedAmount);

        return new AlertDialog.Builder(getContext())
                .setView(view)
                .setTitle("Pay \"" + envelopeName + "\" bill")
                .setPositiveButton("Pay", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        if (isFilled(payEditText)) {
                            listener.onPayPressed(Double.parseDouble(payEditText.getText().toString()));
                            Toast.makeText(getContext(), payEditText.getText().toString() + " paid", Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(getContext(), "user needs to fill out all fields", Toast.LENGTH_SHORT).show();
                        }
                    }
                })
                .setNegativeButton("Cancel", null)
                .show();
    }

    public interface PayEnvelopeDialogListener {
        void onPayPressed(double amount);
    }
}
