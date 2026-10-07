package com.example.tindahan

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.tindahan.data.ProductRepository
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn

data class DashboardState(val productCount: Int = 0, val lowStockCount: Int = 0)

class DashboardViewModel(repo: ProductRepository) : ViewModel() {

    val state: StateFlow<DashboardState> =
        combine(repo.productCount(), repo.lowStockCount()) { products, low ->
            DashboardState(products, low)
        }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), DashboardState())

    class Factory(private val repo: ProductRepository) : ViewModelProvider.Factory {
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: Class<T>): T =
            DashboardViewModel(repo) as T
    }
}
