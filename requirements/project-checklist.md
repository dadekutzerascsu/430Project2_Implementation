# Warehouse Project - Full Version (150 points)

## Foundation
- [x] Organize application classes in `src/`, test drivers in `tests/`, output in `bin/`.
- [x] Use one coordinator (`StoreSystem`).
- [x] Compile and run using `README.md`; assignment commands also work inside `src/`.
- [x] Keep input/output in the UI and business rules in the model.

## Features
- [x] Create clients and products with unique IDs.
- [x] Manage client wishlists; replace quantities for existing entries and
      support removing an entry (add/delete wishlist business process).
- [x] Review wishlist items at checkout; allow quantity changes and extra products.
- [x] Fill orders from stock; put shortages on each product's FIFO waitlist.
- [x] Clear processed wishlist entries to prevent duplicate orders.
- [x] Generate dated invoices for supplied items only; preserve unit prices.
- [x] Use `BigDecimal` for prices, balances, payments and transaction amounts.
- [x] Support account debits/credits and record dated transactions.
- [x] Apply charges automatically when orders or waitlisted items are fulfilled.
- [x] Record payments separately; allow overpayments to become client credit.
- [x] Receive shipments: fill waitlists first, invoice and charge each fulfillment,
      retain partially filled requests, then add leftovers to stock.
- [x] Display clients/balances, products/stock, wishlists and client transactions.
- [x] Display product waitlists, invoices, outstanding balances and quantities.
- [x] Validate existing IDs, quantities and money; reject fractions of a cent.
- [x] Preserve data and ID counters when saving/loading (recommended).

## Verification and Submission
- [ ] Run every operation and query in the final assignment through the menu.
- [ ] Check final P1-P5 stock: **92, 7, 25, 33, 39**.
- [ ] Check final balances: **C1 $55 credit; C2 $51 credit; C3 $48 owed;
      C4/C5 $0** (after separate $100 payments for C1 and C2).
- [ ] Check invoices: **C1 $40 + $5; C2 $49; C3 $45 + $3**.
- [ ] Test partial shipments, invalid input, empty lists and repeated checkout.
- [ ] Capture the required terminal test session and expected/actual results.
- [ ] Use a private group repo; invite `rsarnath` and `SajalCodeHub`.
- [ ] Submit the D2L report: Full version, contribution percentages, repo URL,
      test table and unlisted video link; every member speaks in the video.

Source: `Project1FinalImplementation.docx` and the PDFs in `PreviousAssignments`.
