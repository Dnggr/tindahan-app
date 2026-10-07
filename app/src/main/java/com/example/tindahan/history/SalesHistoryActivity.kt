package com.example.tindahan.history

import android.os.Bundle
import android.view.View
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.tindahan.R
import com.example.tindahan.TindahanApp
import com.example.tindahan.core.Money
import com.example.tindahan.databinding.ActivitySalesHistoryBinding
import kotlinx.coroutines.launch

class SalesHistoryActivity : AppCompatActivity() {

    private lateinit var binding: ActivitySalesHistoryBinding

    private val viewModel: SalesHistoryViewModel by viewModels {
        SalesHistoryViewModel.Factory((application as TindahanApp).saleRepository)
    }

    private val adapter = SaleAdapter { sale -> viewModel.openSale(sale) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySalesHistoryBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setSupportActionBar(binding.toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        binding.rvSales.layoutManager = LinearLayoutManager(this)
        binding.rvSales.adapter = adapter
        binding.rvSales.addItemDecoration(DividerItemDecoration(this, DividerItemDecoration.VERTICAL))

        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                launch {
                    viewModel.sales.collect {
                        adapter.submitList(it.orEmpty())
                        binding.tvEmpty.visibility =
                            if (it != null && it.isEmpty()) View.VISIBLE else View.GONE
                    }
                }
                launch {
                    viewModel.todayTotalCentavos.collect {
                        binding.tvToday.text = getString(R.string.today_sales, Money.format(it))
                    }
                }
                launch { viewModel.details.collect { SaleDetailsDialog.show(this@SalesHistoryActivity, it) } }
            }
        }
    }

    override fun onSupportNavigateUp(): Boolean {
        finish()
        return true
    }
}
