package com.davidmiguel.sample;

import android.content.Context;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;

import com.davidmiguel.numberkeyboard.NumberKeyboardListener;
import com.davidmiguel.numberkeyboard.NumberKeyboardPopup;

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.text.NumberFormat;
import java.text.ParseException;

/**
 * Created by Kevin Read <me@kevin-read.com> on 14.08.18 for number-keyboard.
 * Copyright (c) 2018 BörseGo AG. All rights reserved.
 */
public class KeyboardPopupActivity extends AppCompatActivity implements NumberKeyboardListener {

    private static final double MAX_ALLOWED_AMOUNT = 9999.99;
    private static final int MAX_ALLOWED_DECIMALS = 2;

    private EditText amountEditText;
    private String amountText;
    private NumberKeyboardPopup popup;
    private String groupingSeparator;
    private NumberFormat numberFormat;

    private static final String TAG = KeyboardPopupActivity.class.getSimpleName();
    private char groupSeparatorChar;

    public KeyboardPopupActivity() {
        this.amountText = "";
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_keyboard_popup);
        setTitle("Keyboard decimal popup");
        amountEditText = findViewById(R.id.amount);

        numberFormat = NumberFormat.getInstance();
        if (numberFormat instanceof DecimalFormat) {
            DecimalFormatSymbols sym = ((DecimalFormat) numberFormat).getDecimalFormatSymbols();
            groupSeparatorChar = sym.getGroupingSeparator();
            groupingSeparator = String.valueOf(groupSeparatorChar);
        }

        numberFormat.setMaximumFractionDigits(MAX_ALLOWED_DECIMALS);

        popup = new NumberKeyboardPopup.Builder(findViewById(R.id.main_view)).setNumberKeyboardListener(this).setKeyboardLayout(R.layout.popup_keyboard).build(amountEditText);

        amountEditText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!popup.isShowing()) {
                    popup.toggle();
                }
            }
        });
    }

    @Override
    public void onNumberClicked(int number) {
        if ((amountText.isEmpty() || amountText.equals("-")) && number == 0) {
            return;
        }
        final int selectionStart = amountEditText.getSelectionStart();
        final int selectionEnd = amountEditText.getSelectionEnd();
        final StringBuilder sb = new StringBuilder();
        if (selectionStart > 0) {
            sb.append(amountText.substring(0, selectionStart));
        }
        sb.append(number);
        if (selectionEnd < amountText.length() - 1) {
            sb.append(selectionEnd);
        }
        amountText = sb.toString();
        updateAmount(amountText, selectionStart + 1);
    }

    @Override
    public void onLeftAuxButtonClicked() {
        // Nothing to do
    }

    @Override
    public void onModifierButtonClicked(int number) {
        switch (number) {
            case 0:
                // Minus button
                int currentSelection = amountEditText.getSelectionStart();
                if (!amountText.startsWith("-")) {
                    amountText = "-" + amountText;
                    currentSelection++;
                } else {
                    amountText = amountText.substring(1);
                    currentSelection--;
                }
                showAmount(amountText);
                amountEditText.setSelection(currentSelection);
                break;
            case 1:
                // Comma button
                if (!amountText.contains(",")) {
                    currentSelection = amountEditText.getSelectionStart();
                    // If we are currently at the last position, set cursor after the comma
                    if (currentSelection == amountEditText.length()) {
                        currentSelection++;
                    }
                    amountText = amountText.isEmpty() ? "0," : amountText + ",";
                    showAmount(amountText);
                    amountEditText.setSelection(currentSelection);
                }
                break;
            case 2:
                // Delete button
                if (amountText.isEmpty()) {
                    return;
                }
                String newAmountText;

                final int selectionStart = amountEditText.getSelectionStart();
                final int selectionEnd = amountEditText.getSelectionEnd();

                if (amountText.length() <= 1) {
                    newAmountText = "";
                } else {
                    // Check if we have a selection
                    // Strip complete selection
                    final StringBuilder sb = new StringBuilder();
                    if (selectionStart == selectionEnd) {
                        if (selectionStart > 1) {
                            sb.append(amountText.substring(0, selectionStart - 1));
                        }
                        if (selectionStart < amountText.length()) {
                            sb.append(amountText.substring(selectionStart));
                        }
                    } else {
                        if (selectionStart > 0) {
                            sb.append(amountText.substring(0, selectionStart));
                        }
                        if (selectionEnd < amountText.length() - 1) {
                            sb.append(selectionEnd);
                        }
                    }
                    newAmountText = sb.toString();
                    if (!newAmountText.isEmpty()) {
                        if (newAmountText.charAt(newAmountText.length() - 1) == ',') {
                            newAmountText = newAmountText.substring(0, newAmountText.length() - 1);
                        }
                        if ("0".equals(newAmountText)) {
                            newAmountText = "";
                        }
                    }
                }
                updateAmount(newAmountText, selectionEnd > 0 ? selectionEnd - 1 : 0);
                break;
            case 3:
                // Enter button, close keyboard
                final InputMethodManager inputMethodManager = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                if (inputMethodManager != null) {
                    inputMethodManager.hideSoftInputFromWindow(amountEditText.getWindowToken(), 0);
                }

                break;
        }
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

    @Override
    public void onRightAuxButtonClicked() {
        // Nothing to do
    }

    /**
     * Update new entered amount if it is valid.
     *
     * @param newAmountText   new text to parse and format
     * @param newSelectionIdx position that should be the new selection if possible
     */
    private void updateAmount(String newAmountText, int newSelectionIdx) {
        try {
            final int numOldCommas = countCommas(newAmountText);
            double newAmount = newAmountText.isEmpty() ? 0.0 : numberFormat.parse(newAmountText.replace(groupingSeparator, "")).doubleValue();
            newAmount = Math.min(newAmount, MAX_ALLOWED_AMOUNT);
            this.amountText = numberFormat.format(newAmount);
            showAmount(this.amountText);

            final int newLength = amountText.length();
            newSelectionIdx -= numOldCommas - countCommas(amountText);
            amountEditText.setSelection(newSelectionIdx <= newLength ? newSelectionIdx : newLength);
        } catch (ParseException e) {
            Log.e(TAG, "Cannot parse amount '" + newAmountText + "'");
        }
    }

    /**
     * Shows amount in UI.
     */
    private void showAmount(String amount) {
        amountEditText.setText(amount.isEmpty() ? "0" : amount);
    }
}
