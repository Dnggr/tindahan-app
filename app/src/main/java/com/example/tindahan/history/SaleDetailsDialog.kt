package com.example.tindahan.history

import android.content.Context
import com.example.tindahan.R
import com.example.tindahan.core.Money
import com.google.android.material.dialog.MaterialAlertDialogBuilder

object SaleDetailsDialog {

    fun show(context: Context, details: SaleDetails) {
        val sale = details.sale
        val text = buildString {
            details.items.forEach {
                appendLine(
                    context.getString(
                        R.string.sale_item_line, it.quantity, it.productName, Money.format(it.subtotalCentavos)
                    )
                )
            }
            appendLine()
            appendLine(context.getString(R.string.total_format, Money.format(sale.totalCentavos)))
            sale.cashCentavos?.let { cash ->
                appendLine(context.getString(R.string.cash_format, Money.format(cash)))
                append(context.getString(R.string.change_format, Money.format(cash - sale.totalCentavos)))
            }
        }.trimEnd()

        MaterialAlertDialogBuilder(context)
            .setTitle(context.getString(R.string.sale_number, sale.id))
            .setMessage(text)
            .setPositiveButton(R.string.close, null)
            .show()
    }
}
