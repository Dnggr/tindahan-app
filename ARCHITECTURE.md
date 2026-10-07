# Architecture
User -> Activity -> ViewModel -> Repository -> DAO -> Room/SQLite

- UI: XML + ViewBinding
- Data: ProductEntity, ProductDao, ProductRepository, AppDatabase (package `data`)
- Core: Money (centavos <-> display), Validators (package `core`)
- Inventory (stage 2): InventoryActivity, InventoryViewModel, ProductAdapter, ProductDialog
- Dashboard: MainActivity, DashboardViewModel (counts only)
- Calculator (stage 3): PriceCalculator (pure functions), CalculatorActivity

Search: user text is escaped with SearchQuery.escapeLike before it reaches the DAO LIKE query.

Rules: UI never touches Room directly. Repository is the only layer between ViewModel and DAO.
Calculator never modifies inventory.
