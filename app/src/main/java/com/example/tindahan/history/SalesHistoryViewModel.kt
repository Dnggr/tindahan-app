package com.example.tindahan.history

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.tindahan.core.Dates
import com.example.tindahan.data.SaleEntity
import com.example.tindahan.data.SaleItemEntity
import com.example.tindahan.data.SaleRepository
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

data class SaleDetails(val sale: SaleEntity, val items: List<SaleItemEntity>)

class SalesHistoryViewModel(private val repo: SaleRepository) : ViewModel() {

    /** null = first load not finished yet. */
    val sales: StateFlow<List<SaleEntity>?> = repo.sales()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), null)

    val todayTotalCentavos: StateFlow<Long> =
        repo.salesTotalSince(Dates.startOfDay(System.currentTimeMillis()))
            .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), 0L)

    private val _details = Channel<SaleDetails>(Channel.BUFFERED)
    val details: Flow<SaleDetails> = _details.receiveAsFlow()

    fun openSale(sale: SaleEntity) {
        viewModelScope.launch { _details.send(SaleDetails(sale, repo.items(sale.id))) }
    }

    class Factory(private val repo: SaleRepository) : ViewModelProvider.Factory {
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: Class<T>): T =
            SalesHistoryViewModel(repo) as T
    }
}
