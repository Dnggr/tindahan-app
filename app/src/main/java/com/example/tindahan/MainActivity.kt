package com.example.tindahan

import android.content.Intent
import android.os.Bundle
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.example.tindahan.calculator.CalculatorActivity
import com.example.tindahan.databinding.ActivityMainBinding
import com.example.tindahan.inventory.InventoryActivity
import com.google.android.material.color.MaterialColors
import kotlinx.coroutines.launch

/** Dashboard: product count, low-stock count, and entry points to the two screens. */
class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding

    private val viewModel: DashboardViewModel by viewModels {
        DashboardViewModel.Factory((application as TindahanApp).productRepository)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.btnInventory.setOnClickListener {
            startActivity(Intent(this, InventoryActivity::class.java))
        }
        binding.btnCalculator.setOnClickListener {
            startActivity(Intent(this, CalculatorActivity::class.java))
        }

        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.state.collect { render(it) }
            }
        }
    }

    private fun render(state: DashboardState) {
        binding.tvProductCount.text = state.productCount.toString()
        binding.tvLowStockCount.text = state.lowStockCount.toString()
        val attr = if (state.lowStockCount > 0) {
            com.google.android.material.R.attr.colorError
        } else {
            com.google.android.material.R.attr.colorOnSurface
        }
        binding.tvLowStockCount.setTextColor(MaterialColors.getColor(binding.tvLowStockCount, attr))
    }
}
