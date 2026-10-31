# Warehouse final project

Work on application classes in `src/` and test drivers in `tests/`.
The Stage 1 foundation was copied from
PreviousAssignments/Project1Stage1Group-main/WarehouseStage1.
The original Stage 1 files and LibraryFinal sample remain as references.

## Layout

```text
src/                  Application Java source
tests/                Java test drivers
bin/                  Generated .class files (ignored by Git)
requirements/         Final assignment document and project checklist
PreviousAssignments/  Earlier assignments and original Stage 1 source
WarehouseFSM/          Assignment-ready application source
LibraryFinal/         Instructor's reference implementation
.tools/               Portable JDK (local, ignored by Git)
.vscode/              VS Code configuration
```

## Compile and run

With a JDK on PATH, run these commands from the project root:

```powershell
javac -d bin src\*.java
java -cp bin UserInterface
```

The assignment-ready source files are also under `WarehouseFSM/`. To compile
and run from that folder as requested for the group repository:

```sh
cd WarehouseFSM
javac *.java
java UserInterface
```

At startup, answer `n` to start with a clean warehouse. The UI then begins in
`OpeningState`; use the role menus to perform the assignment sequence.

Compile and run the test drivers separately:

```powershell
javac -cp bin -d bin tests\*.java
java -cp bin ClientTest
java -cp bin ProductTest
java -cp bin WishlistItemTest
java -cp bin StoreSystemTest
java -cp bin PaymentTest
java -cp bin OrderFulfillmentTest
java -cp bin ShipmentTest
java -cp bin SaveLoadTest
java -cp bin DemoScriptTest
java -cp bin FsmWorkflowTest
```

For the assignment demonstration, compile and run from `WarehouseFSM/`, as
shown above. For development, the `src/` copy can also be compiled directly:

```powershell
cd src
javac *.java
java UserInterface
```

## Java and VS Code setup

A portable JDK used for verification is available locally in .tools/jdk
(ignored by Git). This workspace's .vscode/settings.json configures it for
Java projects and adds JAVA_HOME and the JDK bin folder to new VS Code terminals.
Install Microsoft's Extension Pack for Java from the Extensions view, reload
the window, and open a fresh terminal. Run java -version and javac -version
to confirm that both are available. No system-wide Java installation is needed.

The local VS Code settings are ignored by Git because the runtime entry has
an absolute path. If you move the project, update java.configuration.runtimes
in .vscode/settings.json to the new JDK location. The Windows terminal paths
use workspaceFolder and follow the project automatically.

For a PowerShell terminal outside VS Code, use this session-only setup first:
  $jdk = (Get-ChildItem .tools/jdk -Directory | Select-Object -First 1).FullName
  $env:Path = "$jdk\bin;$env:Path"

VS Code uses `src` and `tests` as source folders and `bin` as its output folder.
After a layout change, run Developer: Reload Window from the Command Palette
if VS Code still shows old source locations.

## Implemented features

The Project 2 interface is a text-only FSM in `src/`: `Context` owns the
transition matrix and role history; `OpeningState`, `ClientMenuState`,
`ClerkMenuState` and `ManagerMenuState` implement the four class-diagram states.
The client ID is kept in the context while a client menu is active. Becoming a
client/clerk pushes the prior role, and logout returns to it. The same source
files are also included in `WarehouseFSM/` for the assignment's GitHub folder.

The role menus support client details, product lists, transactions, wishlists,
orders and invoices; clerk client/payment operations; and manager product,
waitlist and shipment operations. Orders fulfill available stock, queue
shortages FIFO per product, create a dated invoice for supplied items at their
captured unit prices, charge the account automatically, and clear the
submitted wishlist. Receiving a shipment fills the product's waitlist FIFO
first: each fill generates its own invoice and automatic charge, partially
filled requests remain queued, and leftover units are added to stock.
Use numeric IDs (1, 2, 3); C1/P1 in the assignment are example names.
StoreSystem remains the single coordinator, keeping the Stage 1 structure.
Small integration changes validate model data, protect collection membership,
and handle invalid console input and end-of-input cleanly.

Client menus include wishlist removal and invoice history. Clerk menus include
payment recording, outstanding-balance reports and saving. Manager menus
include shipment receipt, waitlist display and saving.

Payments follow PreviousAssignments/ReceivePaymentFromAClient.pdf:
find the client, show the current balance, request a positive amount, credit
the account and show the updated balance. Invalid amounts are requested again;
an unknown client ends the operation without any changes. Payments are recorded
independently of ordering and shipping; the program does not transfer funds.
A negative account balance is displayed as an absolute credit amount.

All monetary APIs now use BigDecimal. Construct amounts from strings, e.g.
new BigDecimal("100.00"). Amounts must be positive and exactly representable
in whole cents; fractional cents are rejected rather than silently rounded.
Transactions store a date, description and signed amount: charges are positive,
payments are negative. Checkout records an "Order charge" and each shipment
fill records a "Shipment fulfillment charge" automatically; no manual debit
step is needed.

Every test driver verifies hardcoded expected results automatically and ends
with a PASS line; any mismatch prints FAIL and exits with an error, so no
manual output comparison is needed.

`OrderFulfillmentTest` verifies stock depletion, partial and zero-stock
shortages, per-product FIFO ordering, and repeated checkout. `PaymentTest`
verifies the previous assignment's $200 balance examples:

- $100 payment -> $100 owed.
- $200 payment -> $0 balance.
- $300 payment -> $100 credit.

It also checks prepayments, invalid requests, exact cent arithmetic, transaction
history, and preservation of other clients, inventory and wishlist data.
The test seeds charges directly; the UI applies charges automatically at
checkout and shipment processing.

`ShipmentTest` verifies automatic order and shipment charges, FIFO waitlist
fills, retained partial requests, per-fill invoices, leftover restocking and
input validation. `SaveLoadTest` verifies that saving and loading preserve
clients, products, wishlists, waitlists, invoices, transactions and both ID
counters (it writes and then deletes a WarehouseData file in the working
directory). `DemoScriptTest` runs the assignment's full test sequence and
checks the expected final results: stock 92/7/25/33/39; C1 $55 credit,
C2 $51 credit, C3 $48 owed, C4/C5 $0; invoices C1 $40+$5, C2 $49, C3 $45+$3.

Save/load: at startup the program asks whether to load previously saved data.
Clerk and manager menus can save the warehouse, including the next
client/product ID counters, to `WarehouseData` in the working directory
(ignored by Git).

The assignment's client/product/wishlist/order workflow is exercised through
the actual FSM menus and checked end-to-end:

```sh
javac -d bin src/*.java
javac -cp bin -d bin tests/*.java
java -cp bin FsmWorkflowTest
```

`DemoScriptTest` separately checks the extended order/payment/shipment scenario.

## Recording the required test session

The assignment requires a Zoom cloud recording demonstrating the menu workflow.
Run the program interactively while screen sharing, and show compilation and
the full sequence. This recording and the D2L completion report still need to
be completed by the group.

```sh
javac -d bin src/*.java
java -cp bin UserInterface
```

See [Project2requirements/Project2-completion-checklist.md](Project2requirements/Project2-completion-checklist.md)
for the Project 2 implementation and submission checklist, and
requirements/project-checklist.md for the separate 150-point project scope.

Compile only `src/` and `tests/`, not all folders recursively: the reference
folders contain duplicate class names, including `UserInterface`.
