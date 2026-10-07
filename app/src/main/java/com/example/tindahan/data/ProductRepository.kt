package com.example.tindahan.data

import com.example.tindahan.core.ValidationResult
import com.example.tindahan.core.Validators
import kotlinx.coroutines.flow.Flow

class ProductRepository(private val dao: ProductDao) {

    fun products(query: String = ""): Flow<List<ProductEntity>> =
        if (query.isBlank()) dao.observeAll() else dao.search(query.trim())

    fun productCount(): Flow<Int> = dao.observeCount()
    fun lowStockCount(): Flow<Int> = dao.observeLowStockCount()

    suspend fun get(id: Long): ProductEntity? = dao.getById(id)

    /** Validates, then inserts. Returns the validation result; nothing is written on Invalid. */
    suspend fun add(
        name: String,
        priceCentavos: Long,
        stockQuantity: Int,
        unit: String,
        lowStockThreshold: Int,
        now: Long = System.currentTimeMillis(),
    ): ValidationResult {
        val check = Validators.product(name, priceCentavos, stockQuantity, lowStockThreshold)
        if (check is ValidationResult.Invalid) return check
        dao.insert(
            ProductEntity(
                name = name.trim(),
                priceCentavos = priceCentavos,
                stockQuantity = stockQuantity,
                unit = unit.trim().ifBlank { "pc" },
                lowStockThreshold = lowStockThreshold,
                createdAt = now,
                updatedAt = now,
            )
        )
        return ValidationResult.Valid
    }

    suspend fun update(
        product: ProductEntity,
        now: Long = System.currentTimeMillis(),
    ): ValidationResult {
        val check = Validators.product(
            product.name, product.priceCentavos, product.stockQuantity, product.lowStockThreshold
        )
        if (check is ValidationResult.Invalid) return check
        dao.update(
            product.copy(
                name = product.name.trim(),
                unit = product.unit.trim().ifBlank { "pc" },
                updatedAt = now,
            )
        )
        return ValidationResult.Valid
    }

    suspend fun delete(id: Long) = dao.deleteById(id)
}
