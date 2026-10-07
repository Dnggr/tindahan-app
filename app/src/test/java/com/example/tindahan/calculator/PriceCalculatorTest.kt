package com.example.tindahan.calculator

import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class PriceCalculatorTest {

    private fun line(id: Long, price: Long, qty: Int) = CartLine(id, "p$id", price, qty)

    @Test fun subtotalIsPriceTimesQuantity() {
        assertEquals(10000L, PriceCalculator.subtotal(2500, 4)) // P25 x 4 = P100
    }

    @Test fun totalSumsSubtotals() {
        val lines = listOf(line(1, 2500, 4), line(2, 1250, 4)) // P100 + P50
        assertEquals(15000L, PriceCalculator.total(lines))
    }

    @Test fun emptyCartTotalIsZero() {
        assertEquals(0L, PriceCalculator.total(emptyList()))
    }

    @Test fun changeIsCashMinusTotal() {
        assertEquals(5000L, PriceCalculator.change(15000, 20000)) // P200 - P150 = P50
        assertEquals(0L, PriceCalculator.change(15000, 15000))
    }

    @Test fun changeIsNegativeWhenCashIsShort() {
        assertEquals(-2500L, PriceCalculator.change(15000, 12500))
    }

    @Test fun addingSameProductMergesQuantity() {
        val lines = PriceCalculator.addItem(listOf(line(1, 2500, 2)), line(1, 2500, 3))
        assertEquals(1, lines.size)
        assertEquals(5, lines.single().quantity)
    }

    @Test fun addingDifferentProductsKeepsOrder() {
        var lines = PriceCalculator.addItem(emptyList(), line(1, 100, 1))
        lines = PriceCalculator.addItem(lines, line(2, 200, 1))
        assertEquals(listOf(1L, 2L), lines.map { it.productId })
    }

    @Test fun sameNameDifferentIdAreSeparateLines() {
        val a = CartLine(1, "Coke", 2500, 1)
        val b = CartLine(2, "Coke", 2500, 1)
        assertEquals(2, PriceCalculator.addItem(listOf(a), b).size)
    }

    @Test fun removeItemRemovesOnlyThatProduct() {
        val lines = listOf(line(1, 100, 1), line(2, 200, 1))
        assertEquals(listOf(2L), PriceCalculator.removeItem(lines, 1).map { it.productId })
    }

    @Test fun inputIsNotMutated() {
        val original = listOf(line(1, 100, 1))
        PriceCalculator.addItem(original, line(1, 100, 1))
        assertEquals(1, original.single().quantity)
    }

    @Test fun overflowFailsLoudly() {
        var failed = false
        try {
            PriceCalculator.subtotal(Long.MAX_VALUE, 2)
        } catch (e: ArithmeticException) {
            failed = true
        }
        assertTrue(failed)
    }
}
