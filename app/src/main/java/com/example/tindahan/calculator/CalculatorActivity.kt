package com.example.tindahan.calculator

import android.os.Bundle
import android.view.View
import android.widget.ArrayAdapter
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.widget.doAfterTextChanged
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.tindahan.R
import com.example.tindahan.TindahanApp
import com.example.tindahan.core.Money
import com.example.tindahan.data.ProductEntity
import com.example.tindahan.databinding.ActivityCalculatorBinding
import kotlinx.coroutines.launch

class CalculatorActivity : AppCompatActivity() {

    private lateinit var binding: ActivityCalculatorBinding

    private val viewModel: CalculatorViewModel by viewModels {
        CalculatorViewModel.Factory((application as TindahanApp).productRepository)
    }

    private val cartAdapter = CartAdapter { line -> viewModel.removeItem(line.productId) }

    /** The product currently picked in the dropdown (null if none, or the text was edited). */
    private var selected: ProductEntity? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCalculatorBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setSupportActionBar(binding.toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        binding.rvCart.layoutManager = LinearLayoutManager(this)
        binding.rvCart.adapter = cartAdapter
        binding.rvCart.addItemDecoration(DividerItemDecoration(this, DividerItemDecoration.VERTICAL))

        binding.actProduct.setOnItemClickListener { parent, _, position, _ ->
            val choice = parent.getItemAtPosition(position) as ProductChoice
            selected = choice.product
            binding.tilProduct.error = null
            showInfo(choice.product)
        }
        // Typing after picking invalidates the pick.
        binding.actProduct.doAfterTextChanged { text ->
            val current = selected
            if (current != null && text?.toString() != ProductChoice(current).toString()) {
                selected = null
                showInfo(null)
            }
        }

        binding.btnAdd.setOnClickListener { onAddClicked() }
        binding.etCash.doAfterTextChanged { viewModel.setCash(it?.toString().orEmpty()) }
        binding.btnClear.setOnClickListener {
            viewModel.clear()
            binding.etCash.setText("")
        }

        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                launch { viewModel.products.collect { onProducts(it) } }
                launch { viewModel.uiState.collect { render(it) } }
            }
        }
    }

    private fun onProducts(products: List<ProductEntity>) {
        binding.actProduct.setAdapter(
            ArrayAdapter(this, android.R.layout.simple_dropdown_item_1line, products.map { ProductChoice(it) })
        )
        // Drop the pick if that product no longer exists.
        val current = selected
        if (current != null && products.none { it.id == current.id }) {
            binding.actProduct.setText("", false)
        }
    }

    private fun showInfo(product: ProductEntity?) {
        binding.tvProductInfo.text = product?.let {
            getString(R.string.product_info, Money.format(it.priceCentavos), it.unit, it.stockQuantity)
        }.orEmpty()
    }

    private fun onAddClicked() {
        val product = selected
        if (product == null) {
            binding.tilProduct.error = getString(R.string.error_pick_product)
            return
        }
        val quantity = binding.etQuantity.text.toString().trim().toIntOrNull()
        if (quantity == null || quantity !in 1..PriceCalculator.MAX_QUANTITY) {
            binding.tilQuantity.error =
                getString(R.string.error_invalid_quantity, PriceCalculator.MAX_QUANTITY)
            return
        }
        binding.tilQuantity.error = null
        binding.tilProduct.error = null

        viewModel.addItem(product, quantity)

        // Ready for the next item.
        binding.actProduct.setText("", false)
        binding.etQuantity.setText("1")
    }

    private fun render(state: CalculatorUiState) {
        cartAdapter.submitList(state.lines)
        binding.tvEmptyCart.visibility = if (state.lines.isEmpty()) View.VISIBLE else View.GONE
        binding.tvTotal.text = getString(R.string.total_format, Money.format(state.totalCentavos))
        binding.tilCash.error = if (state.cashInvalid) getString(R.string.error_invalid_price) else null

        val change = state.changeCentavos
        binding.tvChange.text = when {
            change == null -> ""
            change >= 0 -> getString(R.string.change_format, Money.format(change))
            else -> getString(R.string.short_format, Money.format(-change))
        }
        binding.btnClear.isEnabled = state.lines.isNotEmpty() || state.cashText.isNotEmpty()
    }

    override fun onSupportNavigateUp(): Boolean {
        finish()
        return true
    }
}
