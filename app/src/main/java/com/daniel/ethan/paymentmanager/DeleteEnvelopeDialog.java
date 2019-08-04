package com.daniel.ethan.paymentmanager;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;

import androidx.fragment.app.DialogFragment;

public class DeleteEnvelopeDialog extends DialogFragment {
    private DeleteEnvelopeDialogListener listener;

    public DeleteEnvelopeDialog(DeleteEnvelopeDialogListener listener) {
        this.listener = listener;
    }
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {

        return new AlertDialog.Builder(getContext())
                .setTitle("Delete")
                .setMessage("Delete \"" + "\" envelope?")
                .setPositiveButton("Delete", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        listener.onDeletePressed();
                    }
                })
                .setNegativeButton("Cancel", null)
                .show();
    }

    public interface DeleteEnvelopeDialogListener {
        void onDeletePressed();
    }
}
