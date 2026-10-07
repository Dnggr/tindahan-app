package com.example.tindahan.calculator

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.tindahan.R
import com.example.tindahan.core.Money
import com.example.tindahan.databinding.ItemCartLineBinding

class CartAdapter(
    private val onRemove: (CartLine) -> Unit,
) : ListAdapter<CartLine, CartAdapter.ViewHolder>(Diff) {

    class ViewHolder(val binding: ItemCartLineBinding) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder =
        ViewHolder(ItemCartLineBinding.inflate(LayoutInflater.from(parent.context), parent, false))

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val line = getItem(position)
        val ctx = holder.itemView.context
        with(holder.binding) {
            tvName.text = line.name
            tvDetail.text = ctx.getString(
                R.string.cart_line_detail, line.quantity, Money.format(line.unitPriceCentavos)
            )
            tvSubtotal.text = Money.format(line.subtotalCentavos)
            btnRemove.setOnClickListener { onRemove(line) }
        }
    }

    private object Diff : DiffUtil.ItemCallback<CartLine>() {
        override fun areItemsTheSame(a: CartLine, b: CartLine) = a.productId == b.productId
        override fun areContentsTheSame(a: CartLine, b: CartLine) = a == b
    }
}
