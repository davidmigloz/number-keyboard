package com.davidmiguel.sample;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    public void openKeyboardInteger(View view) {
        Intent intent = new Intent(this, KeyboardIntegerActivity.class);
        startActivity(intent);
    }

    public void openKeyboardDecimal(View view) {
        Intent intent = new Intent(this, KeyboardDecimalActivity.class);
        startActivity(intent);
    }

    public void openKeyboardFingerprint(View view) {
        Intent intent = new Intent(this, KeyboardFingerprintActivity.class);
        startActivity(intent);
    }

    public void openKeyboardCustom(View view) {
        Intent intent = new Intent(this, KeyboardCustomActivity.class);
        startActivity(intent);
    }

    public void openKeyboardPopup(View view) {
        Intent intent = new Intent(this, KeyboardPopupActivity.class);
        startActivity(intent);
    }

    public void openKeyboardEditTextPopup(View view) {
        Intent intent = new Intent(this, KeyboardEditTextPopupActivity.class);
        startActivity(intent);
    }
}
