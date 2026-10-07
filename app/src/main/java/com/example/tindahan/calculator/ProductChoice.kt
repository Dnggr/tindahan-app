package com.example.tindahan.calculator

import com.example.tindahan.core.Money
import com.example.tindahan.data.ProductEntity

/** What the product dropdown shows. toString() is the dropdown text and what the filter matches. */
data class ProductChoice(val product: ProductEntity) {
    override fun toString(): String = "${product.name} — ${Money.format(product.priceCentavos)}"
}
