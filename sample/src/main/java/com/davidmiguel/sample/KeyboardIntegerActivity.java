package com.davidmiguel.sample;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.TextView;

import com.davidmiguel.numberkeyboard.NumberKeyboard;
import com.davidmiguel.numberkeyboard.NumberKeyboardListener;

import java.text.NumberFormat;

public class KeyboardIntegerActivity extends AppCompatActivity implements NumberKeyboardListener {

    private static final int MAX_ALLOWED_AMOUNT = 99999;

    private TextView amountEditText;
    private int amount;
    private NumberFormat nf = NumberFormat.getInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_keyboard_integer);
        setTitle("Keyboard integer");
        amountEditText = findViewById(R.id.amount);
        NumberKeyboard numberKeyboard = findViewById(R.id.numberKeyboard);
        numberKeyboard.setListener(this);
    }

    @Override
    public void onNumberClicked(int number) {
        int newAmount = (int) (amount * 10.0 + number);
        if (newAmount <= MAX_ALLOWED_AMOUNT) {
            amount = newAmount;
            showAmount();
        }
    }

    @Override
    public void onLeftAuxButtonClicked() {
        // Nothing to do
    }

    @Override
    public void onRightAuxButtonClicked() {
        amount = (int) (amount / 10.0);
        showAmount();
    }

    private void showAmount() {
        amountEditText.setText(nf.format(amount));
    }
}
