# Data Rules
1. Database is the source of truth. No parallel product lists.
2. Products are identified by `id`, never by name.
3. Money is Long centavos (P12.50 = 1250).
4. Validate before writes: name not blank, price >= 0, stock >= 0, threshold >= 0.
5. Timestamps are epoch millis (Long).
6. Sales: stock is reduced only by SaleDao.recordSale, in one transaction with saving the sale. Insufficient stock rolls everything back.
7. Sale items store a snapshot of product name and unit price. product_id is NOT a foreign key; editing or deleting a product never changes history.
8. Changing the schema means bumping the Room version AND adding a Migration. Never use destructive migration: it would wipe the store's data.
