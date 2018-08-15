package com.davidmiguel.sample;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.EditText;

import com.davidmiguel.numberkeyboard.NumberKeyboardListener;
import com.davidmiguel.numberkeyboard.NumberKeyboardPopup;

/**
 * Created by Kevin Read <me@kevin-read.com> on 14.08.18 for number-keyboard.
 * Copyright (c) 2018 BörseGo AG. All rights reserved.
 */
public class KeyboardPopupActivity extends AppCompatActivity implements NumberKeyboardListener {

    private static final double MAX_ALLOWED_AMOUNT = 9999.99;
    private static final int MAX_ALLOWED_DECIMALS = 2;

    private EditText amountEditText;
    private String amountText;
    private double amount;
    private NumberKeyboardPopup popup;

    public KeyboardPopupActivity() {
        this.amountText = "";
        this.amount = 0.0;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_keyboard_popup);
        setTitle("Keyboard decimal");
        amountEditText = findViewById(R.id.amount);

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
        if (amountText.isEmpty() && number == 0) {
            return;
        }
        updateAmount(amountText + number);
    }

    @Override
    public void onLeftAuxButtonClicked() {
        // Nothing to do
    }

    @Override
    public void onModifierButtonClicked(int number) {
        switch (number) {
            case 0:
                // Comma button
                if (!hasComma(amountText)) {
                    amountText = amountText.isEmpty() ? "0," : amountText + ",";
                    showAmount(amountText);
                }
                break;
            case 2:
                // Delete button
                if (amountText.isEmpty()) {
                    return;
                }
                String newAmountText;
                if (amountText.length() <= 1) {
                    newAmountText = "";
                } else {
                    newAmountText = amountText.substring(0, amountText.length() - 1);
                    if (newAmountText.charAt(newAmountText.length() - 1) == ',') {
                        newAmountText = newAmountText.substring(0, newAmountText.length() - 1);
                    }
                    if ("0".equals(newAmountText)) {
                        newAmountText = "";
                    }
                }
                updateAmount(newAmountText);
                break;
        }
    }

    @Override
    public void onRightAuxButtonClicked() {
        // Nothing to do
    }

    /**
     * Update new entered amount if it is valid.
     */
    private void updateAmount(String newAmountText) {
        double newAmount = newAmountText.isEmpty() ? 0.0 : Double.parseDouble(newAmountText.replaceAll(",", "."));
        if (newAmount >= 0.0 && newAmount <= MAX_ALLOWED_AMOUNT
                && getNumDecimals(newAmountText) <= MAX_ALLOWED_DECIMALS) {
            amountText = newAmountText;
            amount = newAmount;
            showAmount(amountText);
        }
    }

    /**
     * Add . every thousand.
     */
    private String addThousandSeparator(String amount) {
        String integer;
        String decimal;
        if (amount.indexOf(',') >= 0) {
            integer = amount.substring(0, amount.indexOf(','));
            decimal = amount.substring(amount.indexOf(','), amount.length());
        } else {
            integer = amount;
            decimal = "";
        }
        if (integer.length() > 3) {
            StringBuilder tmp = new StringBuilder(integer);
            for (int i = integer.length() - 3; i > 0; i = i - 3) {
                tmp.insert(i, ".");
            }
            integer = tmp.toString();
        }
        return integer + decimal;
    }

    /**
     * Shows amount in UI.
     */
    private void showAmount(String amount) {
        amountEditText.setText("€" + (amount.isEmpty() ? "0" : addThousandSeparator(amount)));
    }

    /**
     * Checks whether the string has a comma.
     */
    private boolean hasComma(String text) {
        for (int i = 0; i < text.length(); i++) {
            if (text.charAt(i) == ',') {
                return true;
            }
        }
        return false;
    }

    /**
     * Calculate the number of decimals of the string.
     */
    private int getNumDecimals(String num) {
        if (!hasComma(num)) {
            return 0;
        }
        return num.substring(num.indexOf(',') + 1, num.length()).length();
    }
}
