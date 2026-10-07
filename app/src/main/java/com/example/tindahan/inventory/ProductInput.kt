package com.example.tindahan.inventory

/** Parsed, not-yet-validated values from the product dialog. Rules live in core/Validators. */
data class ProductInput(
    val name: String,
    val priceCentavos: Long,
    val stockQuantity: Int,
    val unit: String,
    val lowStockThreshold: Int,
)
