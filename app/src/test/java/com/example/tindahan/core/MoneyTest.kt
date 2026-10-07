package com.example.tindahan.core

import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Test

class MoneyTest {
    @Test fun formatsCentavos() {
        assertEquals("₱12.50", Money.format(1250))
        assertEquals("₱0.05", Money.format(5))
        assertEquals("₱25.00", Money.format(2500))
    }

    @Test fun parsesPesoText() {
        assertEquals(1250L, Money.parse("12.50"))
        assertEquals(2500L, Money.parse("₱25"))
        assertEquals(120000L, Money.parse("1,200"))
        assertEquals(1250L, Money.parse("12.5"))
    }

    @Test fun rejectsBadInput() {
        assertNull(Money.parse(""))
        assertNull(Money.parse("abc"))
        assertNull(Money.parse("-5"))
        assertNull(Money.parse("1.234"))
    }
}
