package com.example.tindahan.core

import java.util.Locale

/** Money is always Long centavos. Never Double. */
object Money {

    // Up to 9 digits of pesos (fits comfortably in a Long as centavos) and at most 2 decimals.
    private val AMOUNT = Regex("""\d{1,9}(\.\d{1,2})?""")

    /** 120000 -> "₱1,200.00" */
    fun format(centavos: Long): String {
        val sign = if (centavos < 0) "-" else ""
        val abs = Math.abs(centavos)
        return String.format(Locale.US, "%s₱%,d.%02d", sign, abs / 100, abs % 100)
    }

    /** 1250 -> "12.50" (plain text for input fields: no symbol, no grouping). */
    fun toInputText(centavos: Long): String =
        String.format(Locale.US, "%d.%02d", centavos / 100, centavos % 100)

    /** "12.50" -> 1250. Returns null unless it is a non-negative amount with at most 2 decimals. */
    fun parse(text: String): Long? {
        val cleaned = text.trim().removePrefix("₱").replace(",", "")
        if (!AMOUNT.matches(cleaned)) return null
        val parts = cleaned.split('.')
        val pesos = parts[0].toLong()
        val cents = when (parts.getOrNull(1)?.length) {
            null -> 0L
            1 -> parts[1].toLong() * 10
            else -> parts[1].toLong()
        }
        return pesos * 100 + cents
    }
}
