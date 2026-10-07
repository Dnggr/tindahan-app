# Data Rules
1. Database is the source of truth. No parallel product lists.
2. Products are identified by `id`, never by name.
3. Money is Long centavos (P12.50 = 1250).
4. Validate before writes: name not blank, price >= 0, stock >= 0, threshold >= 0.
5. Timestamps are epoch millis (Long).
