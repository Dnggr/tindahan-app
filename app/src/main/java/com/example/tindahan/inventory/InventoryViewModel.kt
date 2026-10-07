package com.example.tindahan.inventory

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.tindahan.core.ValidationResult
import com.example.tindahan.data.ProductEntity
import com.example.tindahan.data.ProductRepository
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class InventoryViewModel(private val repo: ProductRepository) : ViewModel() {

    private val query = MutableStateFlow("")

    /** null = first load not finished yet; empty list = loaded, nothing to show. */
    @OptIn(ExperimentalCoroutinesApi::class)
    val products: StateFlow<List<ProductEntity>?> = query
        .flatMapLatest { repo.products(it) }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), null)

    private val _messages = Channel<String>(Channel.BUFFERED)
    /** One-shot messages for the UI (e.g. validation errors). */
    val messages: Flow<String> = _messages.receiveAsFlow()

    fun setQuery(text: String) {
        query.value = text
    }

    /** existing == null -> add; otherwise update that product. */
    fun save(existing: ProductEntity?, input: ProductInput) {
        viewModelScope.launch {
            val result = if (existing == null) {
                repo.add(
                    name = input.name,
                    priceCentavos = input.priceCentavos,
                    stockQuantity = input.stockQuantity,
                    unit = input.unit,
                    lowStockThreshold = input.lowStockThreshold,
                )
            } else {
                repo.update(
                    existing.copy(
                        name = input.name,
                        priceCentavos = input.priceCentavos,
                        stockQuantity = input.stockQuantity,
                        unit = input.unit,
                        lowStockThreshold = input.lowStockThreshold,
                    )
                )
            }
            if (result is ValidationResult.Invalid) _messages.send(result.message)
        }
    }

    fun delete(product: ProductEntity) {
        viewModelScope.launch { repo.delete(product.id) }
    }

    class Factory(private val repo: ProductRepository) : ViewModelProvider.Factory {
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: Class<T>): T =
            InventoryViewModel(repo) as T
    }
}
