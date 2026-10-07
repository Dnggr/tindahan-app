package com.example.tindahan.history

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.tindahan.R
import com.example.tindahan.core.Money
import com.example.tindahan.data.SaleEntity
import com.example.tindahan.databinding.ItemSaleBinding
import java.text.DateFormat
import java.util.Date

class SaleAdapter(
    private val onClick: (SaleEntity) -> Unit,
) : ListAdapter<SaleEntity, SaleAdapter.ViewHolder>(Diff) {

    class ViewHolder(val binding: ItemSaleBinding) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder =
        ViewHolder(ItemSaleBinding.inflate(LayoutInflater.from(parent.context), parent, false))

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val sale = getItem(position)
        val ctx = holder.itemView.context
        with(holder.binding) {
            tvTitle.text = ctx.getString(R.string.sale_number, sale.id)
            tvDate.text = DateFormat.getDateTimeInstance(DateFormat.MEDIUM, DateFormat.SHORT)
                .format(Date(sale.createdAt))
            tvTotal.text = Money.format(sale.totalCentavos)
            root.setOnClickListener { onClick(sale) }
        }
    }

    private object Diff : DiffUtil.ItemCallback<SaleEntity>() {
        override fun areItemsTheSame(a: SaleEntity, b: SaleEntity) = a.id == b.id
        override fun areContentsTheSame(a: SaleEntity, b: SaleEntity) = a == b
    }
}
