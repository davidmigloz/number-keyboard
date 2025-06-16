package com.davidmiguel.sample

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.consumeWindowInsets
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeGesturesPadding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.Backspace
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
import java.text.DecimalFormat
import java.text.NumberFormat

@Composable
fun DecimalScreen(innerPadding: PaddingValues) {
    val currencySymbol = "$"
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
            text = "Decimal",
            style = MaterialTheme.typography.titleLarge,
        )

        Spacer(modifier = Modifier.height(16.dp))

        var text by remember { mutableStateOf("$currencySymbol 0") }

        Text(
            text = text,
            style = MaterialTheme.typography.displayLarge,
        )

        Spacer(modifier = Modifier.weight(1F))

        val buttonModifier = Modifier
            .weight(1F)
            .height(48.dp)
        val buttonTextStyle = MaterialTheme.typography.titleMedium
        NumberKeyboard(
            button = { number, clickedListener ->
                NumberKeyboardButton(
                    modifier = buttonModifier,
                    textStyle = buttonTextStyle,
                    number = number,
                    listener = clickedListener
                )
            },
            leftAuxButton = { clickedListener ->
                NumberKeyboardAuxButton(
                    modifier = buttonModifier,
                    textStyle = buttonTextStyle,
                    value = (NumberFormat.getNumberInstance() as DecimalFormat).decimalFormatSymbols.decimalSeparator.toString(),
                    clicked = { clickedListener.onLeftAuxButtonClicked() }
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
                    text = data.currency
                }
            }
        )

        Spacer(modifier = Modifier.height(16.dp))
    }
}