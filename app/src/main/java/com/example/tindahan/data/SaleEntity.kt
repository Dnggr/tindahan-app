package com.example.tindahan.data

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "sales")
data class SaleEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    @ColumnInfo(name = "total_centavos") val totalCentavos: Long,
    /** null when no cash amount was entered. */
    @ColumnInfo(name = "cash_centavos") val cashCentavos: Long?,
    @ColumnInfo(name = "created_at") val createdAt: Long,
)
