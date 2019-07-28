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

import static com.daniel.ethan.paymentmanager.Utils.isFilled;

public class BankAmountDialog extends DialogFragment {
    EditText bankAmount;
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        View view = LayoutInflater.from(getContext()).inflate(R.layout.dialog_change_bank_amount,null);
        bankAmount = view.findViewById(R.id.edit_bank_amount);

        return new AlertDialog.Builder(getContext())
                .setView(view)
                .setTitle("Set Bank Amount")
                .setPositiveButton("Set", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        if (isFilled(bankAmount)) {
                            Toast.makeText(getContext(), bankAmount.toString() + " created", Toast.LENGTH_SHORT).show();
                        }
                        Toast.makeText(getContext(), "user needs to fill out all fields", Toast.LENGTH_SHORT).show();
                    }
                })
                .setNegativeButton("Cancel", null)
                .show();
    }
}
