package com.example.tindahan.data

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Transaction
import kotlinx.coroutines.flow.Flow

class InsufficientStockException(val productName: String) :
    Exception("Not enough stock for $productName")

@Dao
abstract class SaleDao {

    @Insert
    abstract suspend fun insertSale(sale: SaleEntity): Long

    @Insert
    abstract suspend fun insertItems(items: List<SaleItemEntity>)

    /**
     * Atomic check-and-deduct: only updates when enough stock exists.
     * Returns the number of rows changed (0 = not enough stock, or product no longer exists).
     */
    @Query(
        "UPDATE products SET stock_quantity = stock_quantity - :quantity, updated_at = :now " +
            "WHERE id = :productId AND stock_quantity >= :quantity"
    )
    abstract suspend fun deductStock(productId: Long, quantity: Int, now: Long): Int

    /**
     * Deducts stock for every item and saves the sale in ONE transaction. If any item has
     * insufficient stock this throws and Room rolls back everything, so inventory is never half-updated.
     */
    @Transaction
    open suspend fun recordSale(sale: SaleEntity, items: List<SaleItemEntity>, now: Long): Long {
        for (item in items) {
            if (deductStock(item.productId, item.quantity, now) == 0) {
                throw InsufficientStockException(item.productName)
            }
        }
        val saleId = insertSale(sale)
        insertItems(items.map { it.copy(saleId = saleId) })
        return saleId
    }

    @Query("SELECT * FROM sales ORDER BY created_at DESC, id DESC")
    abstract fun observeSales(): Flow<List<SaleEntity>>

    @Query("SELECT * FROM sale_items WHERE sale_id = :saleId ORDER BY id")
    abstract suspend fun getItems(saleId: Long): List<SaleItemEntity>

    @Query("SELECT COALESCE(SUM(total_centavos), 0) FROM sales WHERE created_at >= :since")
    abstract fun observeSalesTotalSince(since: Long): Flow<Long>
}
