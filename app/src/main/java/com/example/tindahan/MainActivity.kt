package com.example.tindahan

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.example.tindahan.databinding.ActivityMainBinding
import kotlinx.coroutines.launch

// Placeholder: proves the data layer is wired. Replaced by the dashboard in stage 4.
class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val repo = (application as TindahanApp).productRepository
        lifecycleScope.launch {
            repo.products().collect { binding.tvStatus.text = "Products: ${it.size}" }
        }
    }
}
