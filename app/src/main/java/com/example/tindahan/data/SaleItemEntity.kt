package com.example.tindahan.data

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey
import com.example.tindahan.calculator.PriceCalculator

/**
 * One line of a completed sale. Name and price are snapshots, and product_id is deliberately NOT
 * a foreign key: deleting or editing a product must never change or delete sales history.
 */
@Entity(
    tableName = "sale_items",
    foreignKeys = [
        ForeignKey(
            entity = SaleEntity::class,
            parentColumns = ["id"],
            childColumns = ["sale_id"],
            onDelete = ForeignKey.CASCADE,
        )
    ],
    indices = [Index("sale_id")],
)
data class SaleItemEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    @ColumnInfo(name = "sale_id") val saleId: Long,
    @ColumnInfo(name = "product_id") val productId: Long,
    @ColumnInfo(name = "product_name") val productName: String,
    @ColumnInfo(name = "unit_price_centavos") val unitPriceCentavos: Long,
    val quantity: Int,
) {
    val subtotalCentavos: Long get() = PriceCalculator.subtotal(unitPriceCentavos, quantity)
}
