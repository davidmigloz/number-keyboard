package com.davidmiguel.numberkeyboard.sample

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
import androidx.compose.foundation.text.BasicText
import androidx.compose.foundation.text.TextAutoSize
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.Backspace
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SegmentedButton
import androidx.compose.material3.SegmentedButtonDefaults
import androidx.compose.material3.SingleChoiceSegmentedButtonRow
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.davidmiguel.numberkeyboard.NumberKeyboard
import com.davidmiguel.numberkeyboard.NumberKeyboardAuxButton
import com.davidmiguel.numberkeyboard.NumberKeyboardButton
import com.davidmiguel.numberkeyboard.data.NumberKeyboardData
import com.davidmiguel.numberkeyboard.data.NumberKeyboardFormat
import com.davidmiguel.numberkeyboard.getDecimalSeparator
import com.davidmiguel.numberkeyboard.getGroupingSeparator
import com.davidmiguel.numberkeyboard.listener.NumberKeyboardListener

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
                    "currencySymbol = $currencySymbol\n",
            style = MaterialTheme.typography.titleMedium,
        )

        var selectedIndex by remember { mutableIntStateOf(0) }
        val options = NumberKeyboardFormat.entries

        SingleChoiceSegmentedButtonRow {
            options.forEachIndexed { index, label ->
                SegmentedButton(
                    shape = SegmentedButtonDefaults.itemShape(
                        index = index,
                        count = options.size
                    ),
                    onClick = { selectedIndex = index },
                    selected = index == selectedIndex,
                    label = {
                        BasicText(
                            text = label.name,
                            maxLines = 1,
                            autoSize = TextAutoSize.StepBased(),
                        )
                    }
                )
            }
        }

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
            onAmountChanged = { amount = it },
            maxAllowedAmount = 8_888.888,
            maxAllowedDecimals = 3,
            currencySymbol = currencySymbol,
            format = NumberKeyboardFormat.entries[selectedIndex],
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