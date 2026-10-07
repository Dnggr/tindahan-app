package com.example.tindahan.calculator

/** One line in the calculator cart. Identified by productId, never by name. */
data class CartLine(
    val productId: Long,
    val name: String,
    val unitPriceCentavos: Long,
    val quantity: Int,
) {
    val subtotalCentavos: Long get() = PriceCalculator.subtotal(unitPriceCentavos, quantity)
}

/**
 * Pure functions only: no database, no Android. Money is Long centavos.
 * Uses *Exact math so an overflow fails loudly instead of silently wrapping.
 */
object PriceCalculator {
    const val MAX_QUANTITY = 9999

    fun subtotal(unitPriceCentavos: Long, quantity: Int): Long =
        Math.multiplyExact(unitPriceCentavos, quantity.toLong())

    fun total(lines: List<CartLine>): Long =
        lines.fold(0L) { acc, line -> Math.addExact(acc, line.subtotalCentavos) }

    /** cash - total. Negative means the customer is short by that amount. */
    fun change(totalCentavos: Long, cashCentavos: Long): Long = cashCentavos - totalCentavos

    /** Adds [item]; if the same product is already in the cart, quantities are merged. */
    fun addItem(lines: List<CartLine>, item: CartLine): List<CartLine> {
        val index = lines.indexOfFirst { it.productId == item.productId }
        if (index < 0) return lines + item
        val merged = lines[index].copy(quantity = Math.addExact(lines[index].quantity, item.quantity))
        return lines.toMutableList().also { it[index] = merged }
    }

    fun removeItem(lines: List<CartLine>, productId: Long): List<CartLine> =
        lines.filterNot { it.productId == productId }
}
