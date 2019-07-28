package com.daniel.ethan.paymentmanager;

import android.widget.EditText;

public class Utils {

    public static boolean isFilled(EditText... editTextCollection) {
        for (EditText editText : editTextCollection) {
            if (editText.getText().toString().isEmpty()) {
                return false;
            }
        }
        return true;
    }
}
