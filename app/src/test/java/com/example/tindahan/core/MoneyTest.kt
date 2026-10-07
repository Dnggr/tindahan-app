package com.example.tindahan.core

import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Test

class MoneyTest {
    @Test fun formatsCentavos() {
        assertEquals("₱12.50", Money.format(1250))
        assertEquals("₱0.05", Money.format(5))
        assertEquals("₱25.00", Money.format(2500))
        assertEquals("₱1,200.00", Money.format(120000))
    }

    @Test fun formatsInputText() {
        assertEquals("12.50", Money.toInputText(1250))
        assertEquals("0.05", Money.toInputText(5))
        assertEquals("1200.00", Money.toInputText(120000))
    }

    @Test fun parsesPesoText() {
        assertEquals(1250L, Money.parse("12.50"))
        assertEquals(1250L, Money.parse("12.5"))
        assertEquals(2500L, Money.parse("₱25"))
        assertEquals(120000L, Money.parse("1,200"))
        assertEquals(105L, Money.parse("1.05"))
        assertEquals(0L, Money.parse("0"))
    }

    @Test fun rejectsBadInput() {
        assertNull(Money.parse(""))
        assertNull(Money.parse("abc"))
        assertNull(Money.parse("-5"))
        assertNull(Money.parse("1.234"))
        assertNull(Money.parse(".5"))
        assertNull(Money.parse("12."))
        assertNull(Money.parse("9999999999"))
    }

    @Test fun parseAndInputTextRoundTrip() {
        for (c in listOf(0L, 5L, 99L, 100L, 1250L, 123456789L)) {
            assertEquals(c, Money.parse(Money.toInputText(c)))
        }
    }
}
