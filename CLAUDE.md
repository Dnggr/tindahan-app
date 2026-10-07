# Tindahan App

## Goal
Offline Android inventory app for a small sari-sari store.

## Stack
Kotlin, Android, XML + ViewBinding, Room/SQLite, MVVM-ish (Activity -> ViewModel -> Repository -> DAO).

## Rules
1. Keep it simple. No Clean Architecture, UseCases, DI frameworks, or networking unless asked.
2. Do not add dependencies unless necessary.
3. Activities handle UI only. ViewModels handle UI state/actions. Repositories handle data ops. DAOs hold Room queries only.
4. Money is Long centavos. Never Double.
5. PriceCalculator is pure and the calculator screen never writes stock itself. Stock is reduced only by SaleRepository.completeSale (one DB transaction, via SaleDao.recordSale).
6. Validate input before database writes.
7. Prefer small files. Do not duplicate business logic.

## Debugging
1. Identify the exact failing layer. 2. Explain the root cause. 3. Smallest safe fix.
4. Don't rewrite unrelated files. 5. Check affected data flow. 6. Build/test after.
