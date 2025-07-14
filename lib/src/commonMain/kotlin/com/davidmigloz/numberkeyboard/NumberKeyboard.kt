package com.davidmigloz.numberkeyboard

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.davidmigloz.numberkeyboard.data.NumberKeyboardData
import com.davidmigloz.numberkeyboard.listener.NumberKeyboardClickedListener
import com.davidmigloz.numberkeyboard.listener.NumberKeyboardListener
import java.text.DecimalFormat
import java.text.NumberFormat

@Composable
fun NumberKeyboard(
    initialAmount: Double = 0.0,
    maxAllowedAmount: Double = 10_000.0,
    maxAllowedDecimals: Int = 2,
    currencySymbol: String = "$",
    isInverted: Boolean = false,
    roundUpToMax: Boolean = true,
    verticalArrangement: Arrangement.HorizontalOrVertical = Arrangement.spacedBy(8.dp),
    horizontalArrangement: Arrangement.HorizontalOrVertical = Arrangement.spacedBy(8.dp),
    button: @Composable (Int, NumberKeyboardClickedListener) -> Unit,
    leftAuxButton: @Composable ((NumberKeyboardClickedListener) -> Unit)? = null,
    rightAuxButton: @Composable ((NumberKeyboardClickedListener) -> Unit)? = null,
    decimalSeparator: Char = (NumberFormat.getNumberInstance() as DecimalFormat).decimalFormatSymbols.decimalSeparator,
    groupingSeparator: Char = (NumberFormat.getNumberInstance() as DecimalFormat).decimalFormatSymbols.groupingSeparator,
    listener: NumberKeyboardListener? = null
) {
    var amount by remember { mutableStateOf(if (initialAmount != 0.0) initialAmount.toInt().toString() else "") }

    val firstRow: List<Int>
    val secondRow: List<Int>
    val thirdRow: List<Int>
    val fourthRow: Int

    if (!isInverted) {
        firstRow = listOf(1, 2, 3)
        secondRow = listOf(4, 5, 6)
        thirdRow = listOf(7, 8, 9)
        fourthRow = 0
    } else {
        firstRow = listOf(7, 8, 9)
        secondRow = listOf(4, 5, 6)
        thirdRow = listOf(1, 2, 3)
        fourthRow = 0
    }

    val clickedListener = object : NumberKeyboardClickedListener {
        override fun onNumberClicked(number: Int) {
            if (amount.isEmpty() && number == 0) return
            val appendedAmount = amount + number.toString()
            val standardisedAmount = appendedAmount.replace(",", ".").toDoubleOrNull() ?: 0.0

            if (getNumberOfDecimals(appendedAmount, decimalSeparator) > maxAllowedDecimals) return
            if (standardisedAmount in 0.0..maxAllowedAmount) {
                amount += number
            } else {
                amount = if (roundUpToMax) maxAllowedAmount.toString().replace('.', decimalSeparator) else amount
            }
            listener?.onUpdated(NumberKeyboardData(amount, decimalSeparator, groupingSeparator, currencySymbol))
        }

        override fun onLeftAuxButtonClicked() {
            if (!amount.contains(decimalSeparator)) {
                amount = if (amount.isEmpty()) {
                    "0$decimalSeparator"
                } else {
                    "$amount$decimalSeparator"
                }
            }
            listener?.onUpdated(NumberKeyboardData(amount, decimalSeparator, groupingSeparator, currencySymbol))
        }

        override fun onRightAuxButtonClicked() {
            if (amount.isEmpty()) return
            val trimmedAmountText = amount.trimEnd(decimalSeparator)
            val newAmountText = if (trimmedAmountText.length <= 1) {
                ""
            } else {
                trimmedAmountText.substring(0, trimmedAmountText.length - 1)
            }
            amount = newAmountText
            listener?.onUpdated(NumberKeyboardData(amount, decimalSeparator, groupingSeparator, currencySymbol))
        }
    }

    Column(verticalArrangement = verticalArrangement) {
        Row(Modifier.fillMaxWidth(), horizontalArrangement = horizontalArrangement) {
            firstRow.forEach { button.invoke(it, clickedListener) }
        }
        Row(Modifier.fillMaxWidth(), horizontalArrangement = horizontalArrangement) {
            secondRow.forEach { button.invoke(it, clickedListener) }
        }
        Row(Modifier.fillMaxWidth(), horizontalArrangement = horizontalArrangement) {
            thirdRow.forEach { button.invoke(it, clickedListener) }
        }
        Row(Modifier.fillMaxWidth(), horizontalArrangement = horizontalArrangement) {
            leftAuxButton?.invoke(clickedListener) ?: NumberKeyboardBlank()
            button.invoke(fourthRow, clickedListener)
            rightAuxButton?.invoke(clickedListener) ?: NumberKeyboardBlank()
        }
    }
}

private fun getNumberOfDecimals(amount: String, decimalSeparator: Char): Int {
    val separatorIndex = amount.indexOf(decimalSeparator)
    return if (separatorIndex >= 0) {
        amount.length - separatorIndex - 1
    } else {
        0
    }
}