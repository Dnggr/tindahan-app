package com.example.tindahan.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase

@Database(
    entities = [ProductEntity::class, SaleEntity::class, SaleItemEntity::class],
    version = 2,
    exportSchema = false,
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun productDao(): ProductDao
    abstract fun saleDao(): SaleDao

    companion object {
        /** v1 -> v2: adds sales history. Existing products are untouched. */
        val MIGRATION_1_2 = object : Migration(1, 2) {
            override fun migrate(db: SupportSQLiteDatabase) {
                db.execSQL(
                    "CREATE TABLE IF NOT EXISTS `sales` (" +
                        "`id` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, " +
                        "`total_centavos` INTEGER NOT NULL, " +
                        "`cash_centavos` INTEGER, " +
                        "`created_at` INTEGER NOT NULL)"
                )
                db.execSQL(
                    "CREATE TABLE IF NOT EXISTS `sale_items` (" +
                        "`id` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, " +
                        "`sale_id` INTEGER NOT NULL, " +
                        "`product_id` INTEGER NOT NULL, " +
                        "`product_name` TEXT NOT NULL, " +
                        "`unit_price_centavos` INTEGER NOT NULL, " +
                        "`quantity` INTEGER NOT NULL, " +
                        "FOREIGN KEY(`sale_id`) REFERENCES `sales`(`id`) " +
                        "ON UPDATE NO ACTION ON DELETE CASCADE )"
                )
                db.execSQL(
                    "CREATE INDEX IF NOT EXISTS `index_sale_items_sale_id` ON `sale_items` (`sale_id`)"
                )
            }
        }

        @Volatile private var instance: AppDatabase? = null

        fun get(context: Context): AppDatabase =
            instance ?: synchronized(this) {
                instance ?: Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "tindahan.db",
                ).addMigrations(MIGRATION_1_2).build().also { instance = it }
            }
    }
}
