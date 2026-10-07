package com.example.tindahan.calculator

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.tindahan.core.Money
import com.example.tindahan.data.ProductEntity
import com.example.tindahan.data.ProductRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update

data class CalculatorUiState(
    val lines: List<CartLine> = emptyList(),
    val totalCentavos: Long = 0,
    val cashText: String = "",
) {
    val cashCentavos: Long? get() = Money.parse(cashText)
    val cashInvalid: Boolean get() = cashText.isNotBlank() && cashCentavos == null

    /** null until a valid cash amount is entered. Negative = short. */
    val changeCentavos: Long?
        get() = cashCentavos?.let { PriceCalculator.change(totalCentavos, it) }
}

/** Read-only with respect to inventory: it only observes products, it never writes them. */
class CalculatorViewModel(repo: ProductRepository) : ViewModel() {

    val products: StateFlow<List<ProductEntity>> = repo.products()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), emptyList())

    private val cart = MutableStateFlow<List<CartLine>>(emptyList())
    private val cashText = MutableStateFlow("")

    val uiState: StateFlow<CalculatorUiState> = combine(cart, cashText) { lines, cash ->
        CalculatorUiState(lines, PriceCalculator.total(lines), cash)
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), CalculatorUiState())

    fun addItem(product: ProductEntity, quantity: Int) {
        val line = CartLine(product.id, product.name, product.priceCentavos, quantity)
        cart.update { PriceCalculator.addItem(it, line) }
    }

    fun removeItem(productId: Long) {
        cart.update { PriceCalculator.removeItem(it, productId) }
    }

    fun setCash(text: String) {
        cashText.value = text
    }

    fun clear() {
        cart.value = emptyList()
        cashText.value = ""
    }

    class Factory(private val repo: ProductRepository) : ViewModelProvider.Factory {
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: Class<T>): T =
            CalculatorViewModel(repo) as T
    }
}
