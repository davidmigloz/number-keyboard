package com.davidmigloz.numberkeyboard

import platform.Foundation.*

actual fun getDecimalSeparator(): Char {
    return NSLocale.currentLocale.decimalSeparator?.firstOrNull() ?: '.'
}

actual fun getGroupingSeparator(): Char {
    return NSLocale.currentLocale.groupingSeparator?.firstOrNull() ?: ','
}

actual fun isCurrencySymbolInFront(): Boolean {
    val languageCode = NSLocale.currentLocale.languageCode ?: "en"
    val countryCode = NSLocale.currentLocale.countryCode ?: "US"

    val localeId = "$languageCode-$countryCode"
    val formatter = NSNumberFormatter().apply {
        numberStyle = NSNumberFormatterCurrencyStyle
        locale = NSLocale(localeIdentifier = localeId)
    }

    val symbol = formatter.currencySymbol ?: return false
    val formatted = formatter.stringFromNumber(NSNumber.numberWithDouble(1.0)) ?: return false

    return formatted.trim().startsWith(symbol)
}
