package com.example.tindahan

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.example.tindahan.databinding.ActivityMainBinding
import com.example.tindahan.inventory.InventoryActivity
import kotlinx.coroutines.launch

// Temporary home screen. Replaced by the dashboard in stage 4.
class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.btnInventory.setOnClickListener {
            startActivity(Intent(this, InventoryActivity::class.java))
        }

        val repo = (application as TindahanApp).productRepository
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                repo.productCount().collect {
                    binding.tvStatus.text = getString(R.string.products_count, it)
                }
            }
        }
    }
}
