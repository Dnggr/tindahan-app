# Debugging Guide
- Crash: Logcat exception -> lifecycle -> ViewBinding -> nulls -> DB migration.
- Product not showing: DAO query -> Repository -> ViewModel state -> Adapter -> UI refresh.
- Product not saving: validation -> entity values -> Repository.insert -> DAO.insert -> Room error.
- Wrong price: centavos conversion -> formula -> formatting -> quantity.
- Wrong stock: value loaded from DB -> update query -> duplicate updates.
- Wrong stock after a sale: SaleDao.deductStock query -> recordSale loop -> was the transaction rolled back? -> duplicate completeSale taps.
- Sale missing from history: SaleRepository.completeSale result -> SaleDao.insertSale/insertItems -> observeSales query.
- Crash on launch after an update: Room migration (version and Migration.migrate SQL must match the entities exactly).
Always fix the first incorrect value in the data flow.
