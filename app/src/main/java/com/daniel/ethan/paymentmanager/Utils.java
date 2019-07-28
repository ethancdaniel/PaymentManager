package com.daniel.ethan.paymentmanager;

import android.widget.EditText;

import java.text.NumberFormat;

public class Utils {

    public static boolean isFilled(EditText... editTextCollection) {
        for (EditText editText : editTextCollection) {
            if (editText.getText().toString().isEmpty()) {
                return false;
            }
        }
        return true;
    }

    public static String formatMoney(Double s) {
        NumberFormat format = NumberFormat.getCurrencyInstance();
        return format.format(s);
    }
}
