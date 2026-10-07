package com.example.tindahan.data

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "products")
data class ProductEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val name: String,
    @ColumnInfo(name = "price_centavos") val priceCentavos: Long,
    @ColumnInfo(name = "stock_quantity") val stockQuantity: Int,
    val unit: String,
    @ColumnInfo(name = "low_stock_threshold") val lowStockThreshold: Int,
    @ColumnInfo(name = "created_at") val createdAt: Long,
    @ColumnInfo(name = "updated_at") val updatedAt: Long,
) {
    val isLowStock: Boolean get() = stockQuantity <= lowStockThreshold
}
