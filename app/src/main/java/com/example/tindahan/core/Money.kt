package com.example.tindahan.core

import java.math.BigDecimal
import java.math.RoundingMode

/** Money is always Long centavos. Never Double. */
object Money {

    /** 1250 -> "₱12.50" */
    fun format(centavos: Long): String {
        val sign = if (centavos < 0) "-" else ""
        val abs = Math.abs(centavos)
        return "$sign₱%d.%02d".format(abs / 100, abs % 100)
    }

    /** "12.50" -> 1250. Returns null if not a valid non-negative amount with at most 2 decimals. */
    fun parse(text: String): Long? {
        val cleaned = text.trim().removePrefix("₱").replace(",", "")
        if (cleaned.isEmpty()) return null
        val value = cleaned.toBigDecimalOrNull() ?: return null
        if (value.signum() < 0 || value.scale() > 2 && value.stripTrailingZeros().scale() > 2) return null
        return value.setScale(2, RoundingMode.UNNECESSARY).movePointRight(2).longValueExact()
    }
}
