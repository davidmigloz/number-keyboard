package com.davidmigloz.numberkeyboard.sample

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.consumeWindowInsets
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeGesturesPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
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
import com.davidmigloz.numberkeyboard.NumberKeyboard
import com.davidmigloz.numberkeyboard.NumberKeyboardAuxButton
import com.davidmigloz.numberkeyboard.NumberKeyboardButton
import com.davidmigloz.numberkeyboard.data.NumberKeyboardData
import com.davidmigloz.numberkeyboard.getDecimalSeparator
import com.davidmigloz.numberkeyboard.getGroupingSeparator
import com.davidmigloz.numberkeyboard.listener.NumberKeyboardListener

@Composable
fun CustomScreen(innerPadding: PaddingValues) {
    val currencySymbol = "€"
    Column(
        Modifier
            .padding(innerPadding)
            .consumeWindowInsets(innerPadding)
            .safeGesturesPadding()
            .verticalScroll(rememberScrollState())
    ) {
        Text(
            text = "Number Keyboard",
            style = MaterialTheme.typography.headlineLarge,
        )

        Text(
            text = "Custom",
            style = MaterialTheme.typography.titleLarge,
        )

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text = "maxAllowedAmount = 8_888.888\n" +
                    "maxAllowedDecimals = 3\n" +
                    "currencySymbol = $currencySymbol\n" +
                    "isInverted = true",
            style = MaterialTheme.typography.titleMedium,
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
            .aspectRatio(1F)
        val buttonTextStyle = MaterialTheme.typography.titleMedium
        var amount by remember { mutableStateOf("") }
        NumberKeyboard(
            amount = amount,
            onAmountChange = { amount = it },
            maxAllowedAmount = 8_888.888,
            maxAllowedDecimals = 3,
            currencySymbol = currencySymbol,
            isInverted = true,
            button = { number, clickedListener ->
                NumberKeyboardButton(
                    modifier = buttonModifier,
                    textStyle = buttonTextStyle,
                    shape = CircleShape,
                    number = number,
                    listener = clickedListener
                )
            },
            leftAuxButton = { clickedListener ->
                NumberKeyboardAuxButton(
                    modifier = buttonModifier,
                    textStyle = buttonTextStyle,
                    shape = CircleShape,
                    value = getDecimalSeparator().toString(),
                    clicked = { clickedListener.onLeftAuxButtonClicked() }
                )
            },
            rightAuxButton = { clickedListener ->
                NumberKeyboardAuxButton(
                    modifier = buttonModifier,
                    textStyle = buttonTextStyle,
                    shape = CircleShape,
                    imageVector = Icons.AutoMirrored.Rounded.Backspace,
                    clicked = { clickedListener.onRightAuxButtonClicked() }
                )
            },
            decimalSeparator = getDecimalSeparator(),
            groupingSeparator = getGroupingSeparator(),
            listener = object : NumberKeyboardListener {
                override fun onUpdated(data: NumberKeyboardData) {
                    text = data.currency
                }
            }
        )

        Spacer(modifier = Modifier.height(16.dp))
    }
}