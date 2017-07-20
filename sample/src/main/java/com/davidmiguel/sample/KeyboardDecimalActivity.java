package com.davidmiguel.sample;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.TextView;

import com.davidmiguel.numberkeyboard.NumberKeyboard;
import com.davidmiguel.numberkeyboard.NumberKeyboardListener;

import java.text.NumberFormat;
import java.text.ParseException;
import java.util.Locale;

public class KeyboardDecimalActivity extends AppCompatActivity implements NumberKeyboardListener {

    private static final int MAX_ALLOWED_AMOUNT = 99999;
    private static final int MAX_ALLOWED_DECIMALS = 2;

    private NumberFormat nf;
    private TextView amountEditText;
    private String amountText;
    private double amount;

    public KeyboardDecimalActivity() {
        nf = NumberFormat.getInstance(Locale.FRANCE);
        nf.setMinimumFractionDigits(0);
        nf.setMaximumFractionDigits(2);
        amountText = "";
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_keyboard_decimal);
        setTitle("Keyboard decimal");
        amountEditText = (TextView) findViewById(R.id.amount);
        NumberKeyboard numberKeyboard = (NumberKeyboard) findViewById(R.id.numberKeyboard);
        numberKeyboard.setListener(this);
    }

    @Override
    public void onNumberClicked(int number) {
        if (!amountText.isEmpty() && amountText.charAt(amountText.length() - 1) == ',' && number == 0) {
            return;
        }
        String newAmountText = amountText + number;
        double newAmount = getAmount(newAmountText);
        updateAmount(newAmount, newAmountText);
    }

    @Override
    public void onLeftAuxButtonClicked() {
        if (!hasComma(amountText)) {
            amountText = amountText + ",";
            amountEditText.setText(nf.format(amount) + ",");
        }
    }

    @Override
    public void onRightAuxButtonClicked() {
        String newAmountText;
        double newAmount;
        if (!hasComma(amountText) && amount < 10) {
            newAmountText = "";
            newAmount = 0.0;
        } else {
            newAmountText = amountText.substring(0, amountText.length() - 1);
            if (newAmountText.charAt(newAmountText.length() - 1) == ',') {
                newAmountText = newAmountText.substring(0, newAmountText.length() - 1);
            }
            newAmount = getAmount(newAmountText);
        }
        updateAmount(newAmount, newAmountText);
    }

    private double getAmount(String amountText) {
        double tmp;
        try {
            tmp = nf.parse(amountText).doubleValue();
        } catch (ParseException e) {
            tmp = 0.0;
        }
        return tmp;
    }

    private void updateAmount(double newAmount, String newAmountText) {
        if (newAmount >= 0 && newAmount <= MAX_ALLOWED_AMOUNT
                && getNumDecimals(newAmountText) <= MAX_ALLOWED_DECIMALS) {
            amountText = newAmountText;
            amount = newAmount;
            amountEditText.setText(nf.format(amount));
        }
    }

    private boolean hasComma(String text) {
        for (int i = 0; i < text.length(); i++) {
            if (text.charAt(i) == ',') {
                return true;
            }
        }
        return false;
    }

    private int getNumDecimals(String num) {
        if (!hasComma(num)) {
            return 0;
        }
        return num.substring(num.indexOf(',') + 1, num.length()).length();
    }
}
