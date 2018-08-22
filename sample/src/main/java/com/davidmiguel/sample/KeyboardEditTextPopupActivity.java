package com.davidmiguel.sample;

import android.content.Context;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.TextView;

import com.davidmiguel.numberkeyboard.NumberKeyboard;
import com.davidmiguel.numberkeyboard.NumberKeyboardPopup;

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.text.NumberFormat;
import java.text.ParseException;

/**
 * Created by Kevin Read <me@kevin-read.com> on 21.08.18 for number-keyboard.
 * Copyright (c) 2018 BörseGo AG. All rights reserved.
 */
public class KeyboardEditTextPopupActivity extends AppCompatActivity  {

    private static final double MAX_ALLOWED_AMOUNT = 9999.99;
    private static final int MAX_ALLOWED_DECIMALS = 2;

    private EditText amountEditText;
    private String amountText;
    private NumberKeyboardPopup popup;
    private String groupingSeparator;
    private NumberFormat numberFormat;

    private static final String TAG = KeyboardPopupActivity.class.getSimpleName();
    private char groupSeparatorChar;

    private TextWatcher amountFormatWatcher = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {

        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {

        }

        @Override
        public void afterTextChanged(Editable s) {
            final String inputAmount = s.toString();
            final String newAmount = updateAmount(inputAmount);
            if (!inputAmount.equals(newAmount)) {
                amountEditText.removeTextChangedListener(amountFormatWatcher);
                amountEditText.setText(newAmount);
                amountEditText.addTextChangedListener(amountFormatWatcher);
            }
        }
    };

    public KeyboardEditTextPopupActivity() {
        this.amountText = "";
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_keyboard_popup);
        setTitle("Keyboard popup with fake IME");
        amountEditText = findViewById(R.id.amount);

        numberFormat = NumberFormat.getInstance();
        if (numberFormat instanceof DecimalFormat) {
            DecimalFormatSymbols sym = ((DecimalFormat) numberFormat).getDecimalFormatSymbols();
            groupSeparatorChar = sym.getGroupingSeparator();
            groupingSeparator = String.valueOf(groupSeparatorChar);
        }

        numberFormat.setMaximumFractionDigits(MAX_ALLOWED_DECIMALS);

        popup = new NumberKeyboardPopup.Builder(findViewById(R.id.main_view)).setEditTextListener().setKeyboardLayout(R.layout.popup_keyboard).build(amountEditText);

        final NumberKeyboard keyboard = popup.getKeyboard();
        keyboard.setNumberKeyBackground(0);
        keyboard.setKeyPadding(0);

        amountEditText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!popup.isShowing()) {
                    popup.toggle();
                }
            }
        });

        amountEditText.addTextChangedListener(amountFormatWatcher);

        amountEditText.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                amountEditText.clearFocus();

                // Because there is no other Input that can take the focus, hide the keyboard explicitly
                final InputMethodManager inputMethodManager = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                if (inputMethodManager != null) {
                    inputMethodManager.hideSoftInputFromWindow(amountEditText.getWindowToken(), 0);
                }
                return true;
            }
        });

    }


    public int countCommas(final String haystack) {
        int count = 0;
        for (int i = 0; i < haystack.length(); i++) {
            if (haystack.charAt(i) == groupSeparatorChar) {
                count++;
            }
        }
        return count;
    }


    /**
     * Update new entered amount if it is valid.
     *
     * @param newAmountText   new text to parse and format
     */
    private String updateAmount(String newAmountText) {
        try {
            final int numOldCommas = countCommas(newAmountText);
            double newAmount = newAmountText.isEmpty() ? 0.0 : numberFormat.parse(newAmountText.replace(groupingSeparator, "")).doubleValue();
            newAmount = Math.min(newAmount, MAX_ALLOWED_AMOUNT);
            this.amountText = numberFormat.format(newAmount);
            return amountText.isEmpty() ? "0" : amountText;
        } catch (ParseException e) {
            Log.e(TAG, "Cannot parse amount '" + newAmountText + "'");
        }
        return newAmountText;
    }

}

