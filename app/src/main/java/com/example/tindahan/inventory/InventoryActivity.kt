package com.example.tindahan.inventory

import android.os.Bundle
import android.view.View
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.widget.doAfterTextChanged
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.tindahan.TindahanApp
import com.example.tindahan.data.ProductEntity
import com.example.tindahan.databinding.ActivityInventoryBinding
import com.google.android.material.snackbar.Snackbar
import kotlinx.coroutines.launch

class InventoryActivity : AppCompatActivity() {

    private lateinit var binding: ActivityInventoryBinding

    private val viewModel: InventoryViewModel by viewModels {
        InventoryViewModel.Factory((application as TindahanApp).productRepository)
    }

    private val adapter = ProductAdapter { product -> showDialog(product) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityInventoryBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setSupportActionBar(binding.toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        binding.rvProducts.layoutManager = LinearLayoutManager(this)
        binding.rvProducts.adapter = adapter
        binding.rvProducts.addItemDecoration(DividerItemDecoration(this, DividerItemDecoration.VERTICAL))

        binding.etSearch.doAfterTextChanged { viewModel.setQuery(it?.toString().orEmpty()) }
        binding.fabAdd.setOnClickListener { showDialog(null) }

        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                launch { viewModel.products.collect { render(it) } }
                launch {
                    viewModel.messages.collect {
                        Snackbar.make(binding.root, it, Snackbar.LENGTH_SHORT).show()
                    }
                }
            }
        }
    }

    private fun render(products: List<ProductEntity>?) {
        adapter.submitList(products.orEmpty())
        binding.tvEmpty.visibility = if (products != null && products.isEmpty()) View.VISIBLE else View.GONE
    }

    private fun showDialog(existing: ProductEntity?) {
        ProductDialog.show(
            context = this,
            existing = existing,
            onSave = { input -> viewModel.save(existing, input) },
            onDelete = { product -> viewModel.delete(product) },
        )
    }

    override fun onSupportNavigateUp(): Boolean {
        finish()
        return true
    }
}
