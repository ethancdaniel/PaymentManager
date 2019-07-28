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
public class BankAmountDialog extends AppCompatDialogFragment {
    EditText bankAmount;
    BankAmountListener listener;

    public BankAmountDialog(BankAmountListener listener) {
        this.listener = listener;
    }

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
                            listener.applyBankAmount(Integer.parseInt(bankAmount.getText().toString()));
                            Toast.makeText(getContext(), bankAmount.getText().toString() + " is new bank amount", Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(getContext(), "user needs to fill out all fields", Toast.LENGTH_SHORT).show();
                        }
                    }
                })
                .setNegativeButton("Cancel", null)
                .show();
    }

    public interface BankAmountListener {
        void applyBankAmount(Integer amount);
    }
}
