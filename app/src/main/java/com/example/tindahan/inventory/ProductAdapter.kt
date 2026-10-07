package com.example.tindahan.inventory

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.tindahan.R
import com.example.tindahan.core.Money
import com.example.tindahan.data.ProductEntity
import com.example.tindahan.databinding.ItemProductBinding

class ProductAdapter(
    private val onClick: (ProductEntity) -> Unit,
) : ListAdapter<ProductEntity, ProductAdapter.ViewHolder>(Diff) {

    class ViewHolder(val binding: ItemProductBinding) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder =
        ViewHolder(ItemProductBinding.inflate(LayoutInflater.from(parent.context), parent, false))

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val product = getItem(position)
        val ctx = holder.itemView.context
        with(holder.binding) {
            tvName.text = product.name
            tvPrice.text = ctx.getString(
                R.string.price_per_unit, Money.format(product.priceCentavos), product.unit
            )
            tvStock.text = ctx.getString(R.string.stock_format, product.stockQuantity)
            tvLowStock.visibility = if (product.isLowStock) android.view.View.VISIBLE else android.view.View.GONE
            root.setOnClickListener { onClick(product) }
        }
    }

    private object Diff : DiffUtil.ItemCallback<ProductEntity>() {
        override fun areItemsTheSame(a: ProductEntity, b: ProductEntity) = a.id == b.id
        override fun areContentsTheSame(a: ProductEntity, b: ProductEntity) = a == b
    }
}
