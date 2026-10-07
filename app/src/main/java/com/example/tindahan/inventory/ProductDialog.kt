package com.example.tindahan.inventory

import android.content.Context
import android.view.LayoutInflater
import androidx.appcompat.app.AlertDialog
import com.example.tindahan.R
import com.example.tindahan.core.Money
import com.example.tindahan.data.ProductEntity
import com.example.tindahan.databinding.DialogProductBinding
import com.google.android.material.dialog.MaterialAlertDialogBuilder

/** One dialog for both Add (existing == null) and Edit. It only collects and parses input. */
object ProductDialog {

    fun show(
        context: Context,
        existing: ProductEntity?,
        onSave: (ProductInput) -> Unit,
        onDelete: (ProductEntity) -> Unit,
    ) {
        val b = DialogProductBinding.inflate(LayoutInflater.from(context))
        if (existing != null) {
            b.etName.setText(existing.name)
            b.etPrice.setText(Money.toInputText(existing.priceCentavos))
            b.etStock.setText(existing.stockQuantity.toString())
            b.etUnit.setText(existing.unit)
            b.etThreshold.setText(existing.lowStockThreshold.toString())
        } else {
            b.etUnit.setText(context.getString(R.string.default_unit))
            b.etThreshold.setText(context.getString(R.string.default_threshold))
        }

        val builder = MaterialAlertDialogBuilder(context)
            .setTitle(if (existing == null) R.string.dialog_add_title else R.string.dialog_edit_title)
            .setView(b.root)
            .setNegativeButton(R.string.cancel, null)
            .setPositiveButton(R.string.save, null) // overridden below so errors keep the dialog open
        if (existing != null) {
            builder.setNeutralButton(R.string.delete) { _, _ -> confirmDelete(context, existing, onDelete) }
        }

        val dialog = builder.create()
        dialog.show()
        dialog.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener {
            val input = read(context, b)
            if (input != null) {
                onSave(input)
                dialog.dismiss()
            }
        }
    }

    /** Returns parsed input, or null after marking the bad fields. */
    private fun read(context: Context, b: DialogProductBinding): ProductInput? {
        val required = context.getString(R.string.error_required)

        val name = b.etName.text.toString().trim()
        val price = Money.parse(b.etPrice.text.toString())
        val stockText = b.etStock.text.toString().trim()
        val stock = stockText.toIntOrNull()
        val thresholdText = b.etThreshold.text.toString().trim()
        val threshold = thresholdText.toIntOrNull()

        b.tilName.error = if (name.isEmpty()) required else null
        b.tilPrice.error = when {
            b.etPrice.text.isNullOrBlank() -> required
            price == null -> context.getString(R.string.error_invalid_price)
            else -> null
        }
        b.tilStock.error = when {
            stockText.isEmpty() -> required
            stock == null || stock < 0 -> context.getString(R.string.error_invalid_number)
            else -> null
        }
        b.tilThreshold.error = when {
            thresholdText.isEmpty() -> required
            threshold == null || threshold < 0 -> context.getString(R.string.error_invalid_number)
            else -> null
        }

        if (name.isEmpty() || price == null || stock == null || stock < 0 ||
            threshold == null || threshold < 0
        ) return null

        val unit = b.etUnit.text.toString().trim().ifBlank { context.getString(R.string.default_unit) }
        return ProductInput(name, price, stock, unit, threshold)
    }

    private fun confirmDelete(context: Context, product: ProductEntity, onDelete: (ProductEntity) -> Unit) {
        MaterialAlertDialogBuilder(context)
            .setTitle(R.string.delete_title)
            .setMessage(context.getString(R.string.delete_message, product.name))
            .setNegativeButton(R.string.cancel, null)
            .setPositiveButton(R.string.delete) { _, _ -> onDelete(product) }
            .show()
    }
}
