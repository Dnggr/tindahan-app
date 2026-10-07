package com.example.tindahan.data

import com.example.tindahan.core.ValidationResult
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

private class FakeDao : ProductDao {
    val items = mutableListOf<ProductEntity>()
    var nextId = 1L
    var updateCalls = 0

    override fun observeAll(): Flow<List<ProductEntity>> = flowOf(items.toList())
    override fun search(query: String): Flow<List<ProductEntity>> =
        flowOf(items.filter { it.name.contains(query, ignoreCase = true) })
    override suspend fun getById(id: Long) = items.find { it.id == id }
    override fun observeCount(): Flow<Int> = flowOf(items.size)
    override fun observeLowStockCount(): Flow<Int> = flowOf(items.count { it.isLowStock })
    override suspend fun insert(product: ProductEntity): Long {
        val id = nextId++
        items.add(product.copy(id = id))
        return id
    }
    override suspend fun update(product: ProductEntity) {
        updateCalls++
        val i = items.indexOfFirst { it.id == product.id }
        if (i >= 0) items[i] = product
    }
    override suspend fun deleteById(id: Long) {
        items.removeAll { it.id == id }
    }
}

class ProductRepositoryTest {
    private val dao = FakeDao()
    private val repo = ProductRepository(dao)

    @Test fun addRejectsBlankNameAndWritesNothing() = runBlocking {
        val result = repo.add("  ", 2500, 10, "pc", 5)
        assertTrue(result is ValidationResult.Invalid)
        assertEquals(0, dao.items.size)
    }

    @Test fun addTrimsNameDefaultsUnitAndStampsTimes() = runBlocking {
        val result = repo.add("  Coke 290ml ", 2500, 12, "  ", 5, now = 1000L)
        assertEquals(ValidationResult.Valid, result)
        val saved = dao.items.single()
        assertEquals("Coke 290ml", saved.name)
        assertEquals("pc", saved.unit)
        assertEquals(1000L, saved.createdAt)
        assertEquals(1000L, saved.updatedAt)
    }

    @Test fun updateRejectsNegativeStockAndDoesNotCallDao() = runBlocking {
        repo.add("Coke", 2500, 12, "pc", 5, now = 1L)
        val bad = dao.items.single().copy(stockQuantity = -1)
        assertTrue(repo.update(bad, now = 2L) is ValidationResult.Invalid)
        assertEquals(0, dao.updateCalls)
        assertEquals(12, dao.items.single().stockQuantity)
    }

    @Test fun updateKeepsCreatedAtAndBumpsUpdatedAt() = runBlocking {
        repo.add("Coke", 2500, 12, "pc", 5, now = 1L)
        val edited = dao.items.single().copy(priceCentavos = 2800)
        assertEquals(ValidationResult.Valid, repo.update(edited, now = 9L))
        val saved = dao.items.single()
        assertEquals(2800L, saved.priceCentavos)
        assertEquals(1L, saved.createdAt)
        assertEquals(9L, saved.updatedAt)
    }

    @Test fun lowStockIsAtOrBelowThreshold() {
        fun p(stock: Int) = ProductEntity(1, "x", 100, stock, "pc", 5, 0, 0)
        assertTrue(p(5).isLowStock)
        assertTrue(p(0).isLowStock)
        assertEquals(false, p(6).isLowStock)
    }
}
