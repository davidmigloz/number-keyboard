package com.davidmiguel.numberkeyboard.sample

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.consumeWindowInsets
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeGesturesPadding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.Backspace
import androidx.compose.material.icons.rounded.Fingerprint
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.davidmiguel.numberkeyboard.NumberKeyboard
import com.davidmiguel.numberkeyboard.NumberKeyboardAuxButton
import com.davidmiguel.numberkeyboard.NumberKeyboardButton
import com.davidmiguel.numberkeyboard.data.NumberKeyboardData
import com.davidmiguel.numberkeyboard.listener.NumberKeyboardListener

@Composable
fun BiometricScreen(innerPadding: PaddingValues) {
    Column(
        Modifier
            .padding(innerPadding)
            .consumeWindowInsets(innerPadding)
            .safeGesturesPadding()
    ) {
        Text(
            text = "Number Keyboard",
            style = MaterialTheme.typography.headlineLarge,
        )

        Text(
            text = "Fingerprint",
            style = MaterialTheme.typography.titleLarge,
        )

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text = "maxAllowedAmount = 9_999\n" +
                    "maxAllowedDecimals = 0\n" +
                    "roundUpToMax = false",
            style = MaterialTheme.typography.titleMedium,
        )

        Spacer(modifier = Modifier.height(16.dp))

        var text by remember { mutableStateOf("0") }

        Text(
            text = text,
            style = MaterialTheme.typography.displayLarge,
        )

        Spacer(modifier = Modifier.weight(1F))

        val buttonModifier = Modifier
            .weight(1F)
            .height(48.dp)
        val buttonTextStyle = MaterialTheme.typography.titleMedium
        var amount by remember { mutableStateOf("") }
        NumberKeyboard(
            amount = amount,
            onAmountChanged = { amount = it },
            maxAllowedAmount = 9_999.00,
            maxAllowedDecimals = 0,
            roundUpToMax = false,
            button = { number, clickedListener ->
                NumberKeyboardButton(
                    modifier = buttonModifier,
                    textStyle = buttonTextStyle,
                    number = number,
                    listener = clickedListener
                )
            },
            leftAuxButton = { _ ->
                NumberKeyboardAuxButton(
                    modifier = buttonModifier,
                    textStyle = buttonTextStyle,
                    imageVector = Icons.Rounded.Fingerprint,
                    clicked = {
//                        Toast.makeText(context, "Biometrics Triggered", Toast.LENGTH_SHORT).show()
                    }
                )
            },
            rightAuxButton = { clickedListener ->
                NumberKeyboardAuxButton(
                    modifier = buttonModifier,
                    textStyle = buttonTextStyle,
                    imageVector = Icons.AutoMirrored.Rounded.Backspace,
                    clicked = { clickedListener.onRightAuxButtonClicked() }
                )
            },
            listener = object : NumberKeyboardListener {
                override fun onUpdated(data: NumberKeyboardData) {
                    text = data.int.toString()
                }
            }
        )

        Spacer(modifier = Modifier.height(16.dp))
    }
}