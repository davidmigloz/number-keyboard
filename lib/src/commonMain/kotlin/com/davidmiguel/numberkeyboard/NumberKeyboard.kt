package com.davidmiguel.numberkeyboard

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.davidmiguel.numberkeyboard.data.NumberKeyboardData
import com.davidmiguel.numberkeyboard.data.NumberKeyboardFormat
import com.davidmiguel.numberkeyboard.listener.NumberKeyboardClickedListener
import com.davidmiguel.numberkeyboard.listener.NumberKeyboardListener
import kotlin.math.pow

@Composable
fun NumberKeyboard(
    amount: String,
    maxAllowedAmount: Double = 10_000.0,
    maxAllowedDecimals: Int = 2,
    currencySymbol: String = "$",
    format: NumberKeyboardFormat = NumberKeyboardFormat.Default,
    roundUpToMax: Boolean = true,
    verticalArrangement: Arrangement.HorizontalOrVertical = Arrangement.spacedBy(8.dp),
    horizontalArrangement: Arrangement.HorizontalOrVertical = Arrangement.spacedBy(8.dp),
    button: @Composable (Int, NumberKeyboardClickedListener) -> Unit,
    leftAuxButton: @Composable ((NumberKeyboardClickedListener) -> Unit)? = null,
    rightAuxButton: @Composable ((NumberKeyboardClickedListener) -> Unit)? = null,
    decimalSeparator: Char = getDecimalSeparator(),
    groupingSeparator: Char = getGroupingSeparator(),
    listener: NumberKeyboardListener? = null
) {
    val firstRow: List<Int>
    val secondRow: List<Int>
    val thirdRow: List<Int>
    val fourthRow: Int

    when (format) {
        NumberKeyboardFormat.Normal -> {
            firstRow = listOf(1, 2, 3)
            secondRow = listOf(4, 5, 6)
            thirdRow = listOf(7, 8, 9)
            fourthRow = 0
        }
        NumberKeyboardFormat.Inverted -> {
            firstRow = listOf(7, 8, 9)
            secondRow = listOf(4, 5, 6)
            thirdRow = listOf(1, 2, 3)
            fourthRow = 0
        }
        NumberKeyboardFormat.Scrambled -> {
            val shuffled = remember { (0..9).shuffled() }
            firstRow = shuffled.subList(0, 3)
            secondRow = shuffled.subList(3, 6)
            thirdRow = shuffled.subList(6, 9)
            fourthRow = shuffled[9]
        }
        NumberKeyboardFormat.AlwaysScrambled -> {
            val shuffled = (0..9).shuffled()
            firstRow = shuffled.subList(0, 3)
            secondRow = shuffled.subList(3, 6)
            thirdRow = shuffled.subList(6, 9)
            fourthRow = shuffled[9]
        }
    }

    val clickedListener = object : NumberKeyboardClickedListener {
        override fun onNumberClicked(number: Int) {
            if (amount.isEmpty() && number == 0) return
            val appended = if (amount == "0") number.toString() else amount + number.toString()
            val standardised = appended.replace(',', '.').toDoubleOrNull() ?: 0.0

            if (getNumberOfDecimals(appended, decimalSeparator) > maxAllowedDecimals) return

            if (standardised in 0.0..maxAllowedAmount) {
                listener?.onUpdated(NumberKeyboardData(appended, decimalSeparator, groupingSeparator, currencySymbol))
            } else if (roundUpToMax) {
                val maxAmount = formatMaxAmount(maxAllowedAmount, maxAllowedDecimals, decimalSeparator)
                listener?.onUpdated(NumberKeyboardData(maxAmount, decimalSeparator, groupingSeparator, currencySymbol))
            }
        }

        override fun onLeftAuxButtonClicked() {
            if (!amount.contains(decimalSeparator)) {
                val updated = if (amount.isEmpty()) {
                    "0$decimalSeparator"
                } else {
                    "$amount$decimalSeparator"
                }
                listener?.onUpdated(NumberKeyboardData(updated, decimalSeparator, groupingSeparator, currencySymbol))
            }
        }

        override fun onRightAuxButtonClicked() {
            if (amount.isEmpty()) return

            val shouldTrimDecimals = maxAllowedDecimals == 0 &&
                    amount.contains(decimalSeparator) &&
                    amount.substringAfter(decimalSeparator).all { it == '0' }

            val cleanedAmount = if (shouldTrimDecimals) {
                amount.substringBefore(decimalSeparator)
            } else {
                amount
            }

            val updated = if (cleanedAmount.length <= 1) "" else cleanedAmount.dropLast(1)
            listener?.onUpdated(NumberKeyboardData(updated, decimalSeparator, groupingSeparator, currencySymbol))
        }
    }

    Column(verticalArrangement = verticalArrangement) {
        Row(Modifier.fillMaxWidth(), horizontalArrangement = horizontalArrangement) {
            firstRow.forEach { button(it, clickedListener) }
        }
        Row(Modifier.fillMaxWidth(), horizontalArrangement = horizontalArrangement) {
            secondRow.forEach { button(it, clickedListener) }
        }
        Row(Modifier.fillMaxWidth(), horizontalArrangement = horizontalArrangement) {
            thirdRow.forEach { button(it, clickedListener) }
        }
        Row(Modifier.fillMaxWidth(), horizontalArrangement = horizontalArrangement) {
            leftAuxButton?.invoke(clickedListener) ?: NumberKeyboardBlank()
            button(fourthRow, clickedListener)
            rightAuxButton?.invoke(clickedListener) ?: NumberKeyboardBlank()
        }
    }
}

private fun getNumberOfDecimals(amount: String, decimalSeparator: Char): Int {
    val index = amount.indexOf(decimalSeparator)
    return if (index >= 0) amount.length - index - 1 else 0
}

private fun formatMaxAmount(
    maxAllowedAmount: Double,
    maxAllowedDecimals: Int,
    decimalSeparator: Char
): String {
    val factor = 10.0.pow(maxAllowedDecimals)
    val rounded = kotlin.math.round(maxAllowedAmount * factor) / factor
    val parts = rounded.toString().split('.')

    val integerPart = parts[0]
    val decimalPart = parts.getOrNull(1)?.padEnd(maxAllowedDecimals, '0')
        ?: "0".repeat(maxAllowedDecimals)

    return "$integerPart$decimalSeparator$decimalPart"
}