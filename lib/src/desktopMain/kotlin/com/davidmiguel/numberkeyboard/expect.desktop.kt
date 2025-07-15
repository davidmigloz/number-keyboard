package com.davidmiguel.numberkeyboard

import java.text.DecimalFormat
import java.text.NumberFormat
import java.util.Locale

actual fun getDecimalSeparator(): Char {
    val format = NumberFormat.getNumberInstance() as DecimalFormat
    return format.decimalFormatSymbols.decimalSeparator
}

actual fun getGroupingSeparator(): Char {
    val format = NumberFormat.getNumberInstance() as DecimalFormat
    return format.decimalFormatSymbols.groupingSeparator
}

actual fun isCurrencySymbolInFront(): Boolean {
    val locale = Locale.getDefault()
    val transformedLocale = Locale.Builder()
        .setLanguage(locale.language)
        .setRegion(locale.country)
        .build()

    val format = NumberFormat.getCurrencyInstance(transformedLocale) as DecimalFormat
    return format.positivePrefix.isNotBlank()
}