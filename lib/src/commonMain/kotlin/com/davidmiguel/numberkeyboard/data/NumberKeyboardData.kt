@file:Suppress("unused", "MemberVisibilityCanBePrivate")

package com.davidmiguel.numberkeyboard.data

import com.davidmiguel.numberkeyboard.isCurrencySymbolInFront

class NumberKeyboardData(
    amount: String,
    private val decimalSeparator: Char,
    private val groupingSeparator: Char,
    private val currencySymbol: String
) {
    val rawAmount: String = amount.ifEmpty { "0" }

    // Integer
    val byte: Byte
        get() = rawAmount.normaliseNumber().toInt().toByte()

    val short: Short
        get() = rawAmount.normaliseNumber().toInt().toShort()

    val int: Int
        get() = rawAmount.normaliseNumber().toInt()

    val long: Long
        get() = rawAmount.normaliseNumber().toLong()

    // Floating-point
    val float: Float
        get() = rawAmount.normaliseNumber().toFloat()

    val double: Double
        get() = rawAmount.normaliseNumber()

    val currency: String
        get() = formatCurrency(rawAmount, decimalSeparator, groupingSeparator, currencySymbol)
}

private fun String.normaliseNumber(): Double = replace(",", ".").toDoubleOrNull() ?: 0.0

private fun formatCurrency(
    amount: String,
    decimalSeparator: Char,
    groupingSeparator: Char,
    currencySymbol: String
): String {
    val formattedAmount = formatAmount(amount, decimalSeparator, groupingSeparator)
    return if (isCurrencySymbolInFront()) {
        "$currencySymbol $formattedAmount"
    } else {
        "$formattedAmount $currencySymbol"
    }
}

private fun formatAmount(
    amount: String,
    decimalSeparator: Char,
    groupingSeparator: Char
): String {
    val parts = amount.split(decimalSeparator)
    val integerPart = parts[0]
    val decimalPart = if (parts.size > 1) decimalSeparator + parts[1] else ""

    val formattedIntegerPart = StringBuilder()
    var index = 0

    for (i in integerPart.length - 1 downTo 0) {
        formattedIntegerPart.insert(0, integerPart[i])
        index++
        if (index % 3 == 0 && i > 0) {
            formattedIntegerPart.insert(0, groupingSeparator)
        }
    }

    return formattedIntegerPart.toString() + decimalPart
}