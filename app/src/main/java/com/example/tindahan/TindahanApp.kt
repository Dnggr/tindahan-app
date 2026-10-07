package com.example.tindahan

import android.app.Application
import com.example.tindahan.data.AppDatabase
import com.example.tindahan.data.ProductRepository

class TindahanApp : Application() {
    val database by lazy { AppDatabase.get(this) }
    val productRepository by lazy { ProductRepository(database.productDao()) }
}
