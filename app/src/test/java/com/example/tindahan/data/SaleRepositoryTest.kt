package com.example.tindahan.data

import com.example.tindahan.calculator.CartLine
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

/**
 * Fake that does NOT override recordSale, so the real deduct-then-insert logic runs against
 * in-memory data. (Real rollback on failure is Room's job and needs an instrumented test.)
 */
private class FakeSaleDao(val stock: MutableMap<Long, Int>) : SaleDao() {
    val sales = mutableListOf<SaleEntity>()
    val items = mutableListOf<SaleItemEntity>()

    override suspend fun insertSale(sale: SaleEntity): Long {
        val id = sales.size + 1L
        sales.add(sale.copy(id = id))
        return id
    }

    override suspend fun insertItems(items: List<SaleItemEntity>) {
        this.items.addAll(items)
    }

    override suspend fun deductStock(productId: Long, quantity: Int, now: Long): Int {
        val have = stock[productId] ?: return 0
        if (have < quantity) return 0
        stock[productId] = have - quantity
        return 1
    }

    override fun observeSales(): Flow<List<SaleEntity>> = flowOf(sales.toList())
    override suspend fun getItems(saleId: Long): List<SaleItemEntity> = items.filter { it.saleId == saleId }
    override fun observeSalesTotalSince(since: Long): Flow<Long> =
        flowOf(sales.filter { it.createdAt >= since }.sumOf { it.totalCentavos })
}

class SaleRepositoryTest {

    private fun line(id: Long, name: String, price: Long, qty: Int) = CartLine(id, name, price, qty)

    @Test fun emptyCartFailsWithoutTouchingTheDatabase() = runBlocking {
        val dao = FakeSaleDao(mutableMapOf())
        val result = SaleRepository(dao).completeSale(emptyList(), null)
        assertTrue(result is SaleResult.Failure)
        assertEquals(0, dao.sales.size)
    }

    @Test fun cashLessThanTotalFailsAndKeepsStock() = runBlocking {
        val dao = FakeSaleDao(mutableMapOf(1L to 10))
        val result = SaleRepository(dao).completeSale(listOf(line(1, "Coke", 2500, 2)), cashCentavos = 4000)
        assertTrue(result is SaleResult.Failure)
        assertEquals(10, dao.stock[1L])
        assertEquals(0, dao.sales.size)
    }

    @Test fun successDeductsStockAndSavesSnapshots() = runBlocking {
        val dao = FakeSaleDao(mutableMapOf(1L to 10, 2L to 5))
        val lines = listOf(line(1, "Coke", 2500, 4), line(2, "Noodles", 1500, 1))
        val result = SaleRepository(dao).completeSale(lines, cashCentavos = 20000, now = 777L)

        assertEquals(SaleResult.Success(1L), result)
        assertEquals(6, dao.stock[1L])
        assertEquals(4, dao.stock[2L])

        val sale = dao.sales.single()
        assertEquals(11500L, sale.totalCentavos) // 4 x 25.00 + 15.00
        assertEquals(20000L, sale.cashCentavos)
        assertEquals(777L, sale.createdAt)

        assertEquals(2, dao.items.size)
        assertTrue(dao.items.all { it.saleId == 1L })
        assertEquals("Coke", dao.items[0].productName)
        assertEquals(2500L, dao.items[0].unitPriceCentavos)
    }

    @Test fun cashIsOptional() = runBlocking {
        val dao = FakeSaleDao(mutableMapOf(1L to 3))
        val result = SaleRepository(dao).completeSale(listOf(line(1, "Coke", 2500, 1)), null)
        assertTrue(result is SaleResult.Success)
        assertEquals(null, dao.sales.single().cashCentavos)
    }

    @Test fun insufficientStockFailsAndNoSaleIsSaved() = runBlocking {
        val dao = FakeSaleDao(mutableMapOf(1L to 10, 2L to 1))
        val lines = listOf(line(1, "Coke", 2500, 2), line(2, "Noodles", 1500, 5))
        val result = SaleRepository(dao).completeSale(lines, null)

        assertEquals(SaleResult.Failure("Not enough stock for Noodles"), result)
        assertEquals(0, dao.sales.size)
        assertEquals(0, dao.items.size)
        // Note: in the real database the first line's deduction is rolled back by Room's transaction.
    }

    @Test fun deletedProductCountsAsInsufficientStock() = runBlocking {
        val dao = FakeSaleDao(mutableMapOf())
        val result = SaleRepository(dao).completeSale(listOf(line(9, "Ghost", 100, 1)), null)
        assertTrue(result is SaleResult.Failure)
    }
}
