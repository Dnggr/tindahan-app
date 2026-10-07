# Architecture
User -> Activity -> ViewModel -> Repository -> DAO -> Room/SQLite

- UI: XML + ViewBinding
- Data: ProductEntity, ProductDao, ProductRepository, AppDatabase (package `data`)
- Core: Money (centavos <-> display), Validators (package `core`)
- Inventory (stage 2): InventoryActivity, InventoryViewModel, ProductAdapter, ProductDialog
- Calculator (stage 3): PriceCalculator (pure functions), CalculatorActivity

Rules: UI never touches Room directly. Repository is the only layer between ViewModel and DAO.
Calculator never modifies inventory.
