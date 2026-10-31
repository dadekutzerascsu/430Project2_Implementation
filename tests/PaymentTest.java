import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

/** Run with java -cp bin PaymentTest; throws AssertionError if a payment rule fails. */
public class PaymentTest {
    public static void main(String[] args) {
        StoreSystem store = StoreSystem.instance();

        // The three examples from ReceivePaymentFromAClient.pdf.
        checkPayment(store, "Partial", "100.00", "100.00");
        checkPayment(store, "Full", "200.00", "0.00");
        checkPayment(store, "Overpayment", "300.00", "-100.00");

        Client client = store.addClient("Prepayment", "400 Main Street");
        Client unaffected = store.addClient("Other client", "500 Main Street");
        Product product = store.addProduct("P1", 10, new BigDecimal("0.10"));
        store.addToWishlist(client.getClientID(), product.getProductID(), 5);

        equal(store.receivePayment(client.getClientID(), new BigDecimal("100")), "-100.00");
        check(client.getBalanceDetails().equals("Credit: $100.00"), "Credit display");
        equal(unaffected.getBalance(), "0.00");
        check(unaffected.getTransactions().isEmpty(), "Another client's history changed");
        check(product.getQuantityInStock() == 10, "Payment changed stock");
        check(client.findWishlistItem(product.getProductID()).getQuantity() == 5,
                "Payment changed wishlist");

        for (BigDecimal invalid : new BigDecimal[] {
                null, BigDecimal.ZERO, new BigDecimal("-1"), new BigDecimal("0.001") }) {
            rejects(() -> store.receivePayment(client.getClientID(), invalid));
            equal(client.getBalance(), "-100.00");
            check(client.getTransactions().size() == 1, "Rejected payment changed history");
        }
        rejects(() -> store.receivePayment(999, new BigDecimal("10")));
        rejects(() -> client.debit(new BigDecimal("-10")));
        rejects(() -> client.credit(new BigDecimal("1.005")));
        equal(client.getBalance(), "-100.00");
        check(client.getTransactions().size() == 1, "Rejected account update changed history");

        List<Transaction> snapshot = store.getTransactions(client.getClientID());
        try {
            snapshot.clear();
            throw new AssertionError("Transaction history can be removed through a query");
        } catch (UnsupportedOperationException expected) { }
        store.receivePayment(client.getClientID(), new BigDecimal("0.10"));
        check(snapshot.size() == 1, "Transaction query was not a snapshot");

        Client cents = new Client(100, "Exact cents", "Address");
        cents.debit(new BigDecimal("0.30"));
        cents.credit(new BigDecimal("0.10"));
        cents.credit(new BigDecimal("0.20"));
        equal(cents.getBalance(), "0.00");
        check(cents.getBalanceDetails().equals("Balance: $0.00"), "Zero display");
        equal(product.getSalePrice(), "0.10");
        rejects(() -> new Product(101, "Fractional cent", 0, new BigDecimal("1.005")));
        rejects(() -> new Product(101, "Missing price", 0, null));

        BigDecimal ledgerTotal = new BigDecimal("0.00");
        for (Transaction transaction : client.getTransactions()) {
            ledgerTotal = ledgerTotal.add(transaction.getAmount());
        }
        check(ledgerTotal.equals(client.getBalance()), "Ledger and balance differ");
        System.out.println("Payment tests passed: partial, full, overpayment, prepayment, "
                + "validation, exact cents, history, and unrelated data.");
    }

    private static void checkPayment(StoreSystem store, String name,
                                     String payment, String expectedBalance) {
        Client client = store.addClient(name, "100 Main Street");
        client.debit(new BigDecimal("200.00"));
        LocalDate before = LocalDate.now();
        BigDecimal result = store.receivePayment(client.getClientID(), new BigDecimal(payment));
        equal(result, expectedBalance);
        equal(client.getBalance(), expectedBalance);
        List<Transaction> transactions = store.getTransactions(client.getClientID());
        check(transactions.size() == 2, "Missing charge/payment history");
        Transaction receipt = transactions.get(1);
        check(receipt.getAmount().equals(new BigDecimal(payment).negate()), "Payment amount");
        check(receipt.getDescription().equals("Payment received"), "Payment description");
        check(!receipt.getDate().isBefore(before)
                && !receipt.getDate().isAfter(LocalDate.now()), "Payment date");
        check(client.getDetails().contains(client.getBalanceDetails()), "Client query balance");
    }

    private static void equal(BigDecimal actual, String expected) {
        check(actual.equals(new BigDecimal(expected)), "Expected " + expected + ", got " + actual);
    }

    private static void check(boolean condition, String message) {
        if (!condition) throw new AssertionError(message);
    }

    private static void rejects(Runnable action) {
        try {
            action.run();
            throw new AssertionError("Invalid operation was accepted");
        } catch (IllegalArgumentException expected) { }
    }
}
