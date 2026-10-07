# Debugging Guide
- Crash: Logcat exception -> lifecycle -> ViewBinding -> nulls -> DB migration.
- Product not showing: DAO query -> Repository -> ViewModel state -> Adapter -> UI refresh.
- Product not saving: validation -> entity values -> Repository.insert -> DAO.insert -> Room error.
- Wrong price: centavos conversion -> formula -> formatting -> quantity.
- Wrong stock: value loaded from DB -> update query -> duplicate updates.
Always fix the first incorrect value in the data flow.
