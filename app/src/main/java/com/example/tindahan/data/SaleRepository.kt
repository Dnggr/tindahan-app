package com.example.tindahan.data

import com.example.tindahan.calculator.CartLine
import com.example.tindahan.calculator.PriceCalculator
import kotlinx.coroutines.flow.Flow

sealed class SaleResult {
    data class Success(val saleId: Long) : SaleResult()
    data class Failure(val message: String) : SaleResult()
}

class SaleRepository(private val dao: SaleDao) {

    fun sales(): Flow<List<SaleEntity>> = dao.observeSales()
    fun salesTotalSince(since: Long): Flow<Long> = dao.observeSalesTotalSince(since)
    suspend fun items(saleId: Long): List<SaleItemEntity> = dao.getItems(saleId)

    /** The only place stock is reduced by a sale. Cash is optional; if given it must cover the total. */
    suspend fun completeSale(
        lines: List<CartLine>,
        cashCentavos: Long?,
        now: Long = System.currentTimeMillis(),
    ): SaleResult {
        if (lines.isEmpty()) return SaleResult.Failure("Cart is empty")
        val total = PriceCalculator.total(lines)
        if (cashCentavos != null && cashCentavos < total) {
            return SaleResult.Failure("Cash is less than the total")
        }
        val sale = SaleEntity(totalCentavos = total, cashCentavos = cashCentavos, createdAt = now)
        val items = lines.map {
            SaleItemEntity(
                saleId = 0, // filled in by the DAO once the sale row exists
                productId = it.productId,
                productName = it.name,
                unitPriceCentavos = it.unitPriceCentavos,
                quantity = it.quantity,
            )
        }
        return try {
            SaleResult.Success(dao.recordSale(sale, items, now))
        } catch (e: InsufficientStockException) {
            SaleResult.Failure("Not enough stock for ${e.productName}")
        }
    }
}
