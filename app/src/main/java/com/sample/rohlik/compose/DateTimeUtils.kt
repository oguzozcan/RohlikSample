package com.sample.rohlik.compose

import android.util.Log
import androidx.core.content.PackageManagerCompat.LOG_TAG
import java.text.DateFormat
import java.text.DecimalFormat
import java.text.NumberFormat
import java.text.SimpleDateFormat
import java.util.*

val SHORT_MONTH_DAY_DISPLAY_LOCAL: DateFormat = SimpleDateFormat("MMM d")
val SHORT_MONTH_DAY_FULL_YEAR_DISPLAY_LOCAL = SimpleDateFormat("MMM d, yyyy")
val LONG_YEAR_MONTH_DAY_LOCAL = SimpleDateFormat("yyyy-MM-dd")

fun Date.getFormattedTime(): String {
    return if (isCurrentYear(this)) {
        SHORT_MONTH_DAY_DISPLAY_LOCAL.format(this)
    } else {
        SHORT_MONTH_DAY_FULL_YEAR_DISPLAY_LOCAL.format(this)
    }
}

fun getLongDate(date: Date?): String {
    if (date == null) {
        return ""
    }
    return LONG_YEAR_MONTH_DAY_LOCAL.format(date)
}

private fun isCurrentYear(date: Date): Boolean {
    val dateCalendar = Calendar.getInstance()
    dateCalendar.time = date
    return dateCalendar[Calendar.YEAR] == Calendar.getInstance()[Calendar.YEAR]
}

fun formatAmount(amount: Double?, locale: Locale, currencyCode: String?): String {
    if (amount == null) {
        return ""
    }
    var currency: Currency? = null
    var decDigits = 2 // A sane default
    var symbol = ""
    if (currencyCode != null) {
        try {
            currency = Currency.getInstance(currencyCode)
        } catch (iae: IllegalArgumentException) {
            Log.w("Formatter", "FormatAmount: invalid currency code: $currencyCode")
        }
        currency?.let {
            decDigits = it.defaultFractionDigits
            symbol = it.getSymbol(locale)
        }
    }

    DecimalFormat.getInstance(locale).apply {
        maximumFractionDigits = decDigits
        minimumFractionDigits = decDigits
        isGroupingUsed = true
        val sb = StringBuilder(format(amount)).apply {
            if (isSymbolASuffix(locale)) {
                append(' ').append(symbol)
            } else {
                if (symbol.length > 1) {
                    // For currency abbreviations, we want a space...
                    insert(0, ' ').insert(0, symbol)
                } else {
                    // For one character symbols, we do not.
                    insert(0, symbol)
                }
            }
        }
        return sb.toString()
    }
}

private fun isSymbolASuffix(locale: Locale): Boolean {
    NumberFormat.getCurrencyInstance(locale).run {
        if (this is DecimalFormat) {
            return toLocalizedPattern().indexOf('\u00a4') > 0
        }
    }
    return false
}