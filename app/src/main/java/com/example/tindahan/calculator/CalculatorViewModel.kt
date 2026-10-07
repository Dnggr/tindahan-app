package com.example.tindahan.calculator

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.tindahan.core.Money
import com.example.tindahan.data.ProductEntity
import com.example.tindahan.data.ProductRepository
import com.example.tindahan.data.SaleRepository
import com.example.tindahan.data.SaleResult
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

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

sealed class CalculatorEvent {
    object SaleSaved : CalculatorEvent()
    object InvalidCash : CalculatorEvent()
    data class SaleFailed(val message: String) : CalculatorEvent()
}

/**
 * Calculating never touches stock. Stock only changes through completeSale(), an explicit
 * user action handled entirely by SaleRepository.
 */
class CalculatorViewModel(
    repo: ProductRepository,
    private val saleRepo: SaleRepository,
) : ViewModel() {

    val products: StateFlow<List<ProductEntity>> = repo.products()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), emptyList())

    private val cart = MutableStateFlow<List<CartLine>>(emptyList())
    private val cashText = MutableStateFlow("")

    val uiState: StateFlow<CalculatorUiState> = combine(cart, cashText) { lines, cash ->
        CalculatorUiState(lines, PriceCalculator.total(lines), cash)
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), CalculatorUiState())

    private val _events = Channel<CalculatorEvent>(Channel.BUFFERED)
    val events: Flow<CalculatorEvent> = _events.receiveAsFlow()

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

    fun completeSale() {
        val lines = cart.value
        val cash = Money.parse(cashText.value)
        viewModelScope.launch {
            if (cashText.value.isNotBlank() && cash == null) {
                _events.send(CalculatorEvent.InvalidCash)
                return@launch
            }
            when (val result = saleRepo.completeSale(lines, cash)) {
                is SaleResult.Success -> {
                    clear()
                    _events.send(CalculatorEvent.SaleSaved)
                }
                is SaleResult.Failure -> _events.send(CalculatorEvent.SaleFailed(result.message))
            }
        }
    }

    class Factory(
        private val repo: ProductRepository,
        private val saleRepo: SaleRepository,
    ) : ViewModelProvider.Factory {
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: Class<T>): T =
            CalculatorViewModel(repo, saleRepo) as T
    }
}
