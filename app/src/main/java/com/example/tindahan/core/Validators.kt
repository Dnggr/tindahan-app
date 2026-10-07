package com.example.tindahan.core

sealed class ValidationResult {
    object Valid : ValidationResult()
    data class Invalid(val message: String) : ValidationResult()
}

object Validators {
    fun product(
        name: String,
        priceCentavos: Long,
        stockQuantity: Int,
        lowStockThreshold: Int,
    ): ValidationResult = when {
        name.isBlank() -> ValidationResult.Invalid("Name is required")
        priceCentavos < 0 -> ValidationResult.Invalid("Price cannot be negative")
        stockQuantity < 0 -> ValidationResult.Invalid("Stock cannot be negative")
        lowStockThreshold < 0 -> ValidationResult.Invalid("Low-stock threshold cannot be negative")
        else -> ValidationResult.Valid
    }
}
