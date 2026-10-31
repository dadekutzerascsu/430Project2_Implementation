import java.math.BigDecimal;
import java.util.List;

/**
 * Run with java -cp bin DemoScriptTest; performs the final assignment's test
 * sequence and checks every expected result from the project checklist.
 */
public class DemoScriptTest {
    public static void main(String[] args) {
        StoreSystem store = StoreSystem.instance();

        // Create five clients C1-C5 and five products P1-P5.
        for (int i = 1; i <= 5; i++) {
            store.addClient("C" + i, i + "00 Main Street");
        }
        int[] startingStock = {10, 20, 30, 40, 50};
        for (int i = 1; i <= 5; i++) {
            store.addProduct("P" + i, startingStock[i - 1], new BigDecimal(i + ".00"));
        }

        // Wishlists: C1 5 each of P1/P3/P5; C2 7 each of P1/P2/P4; C3 6 each of P1/P2/P5.
        store.addToWishlist(1, 1, 5);
        store.addToWishlist(1, 3, 5);
        store.addToWishlist(1, 5, 5);
        store.addToWishlist(2, 1, 7);
        store.addToWishlist(2, 2, 7);
        store.addToWishlist(2, 4, 7);
        store.addToWishlist(3, 1, 6);
        store.addToWishlist(3, 2, 6);
        store.addToWishlist(3, 5, 6);

        // Order for C2: fully supplied and automatically charged $49.
        store.fillOrder(2);
        equal(store.findClient(2).getBalance(), "49.00");

        // Order for C3: 3 of P1 supplied, 3 waitlisted; charged $45.
        store.fillOrder(3);
        equal(store.findClient(3).getBalance(), "45.00");
        check(store.getWishlistItems(2).isEmpty(), "C2 wishlist was not cleared");
        check(store.getWishlistItems(3).isEmpty(), "C3 wishlist was not cleared");
        List<WaitlistEntry> p1Waitlist = store.getProductWaitlist(1);
        check(p1Waitlist.size() == 1
                && p1Waitlist.get(0).getClient().getClientID() == 3
                && p1Waitlist.get(0).getRemainingQuantity() == 3,
                "P1 waitlist should hold 3 units for C3");
        check(store.getProductWaitlist(2).isEmpty(), "P2 waitlist should be empty");

        // Order for C1: P1 fully waitlisted; charged $40 for P3/P5.
        store.fillOrder(1);
        equal(store.findClient(1).getBalance(), "40.00");
        check(store.getWishlistItems(1).isEmpty(), "C1 wishlist was not cleared");

        // Record $100 payments for C1 and C2.
        store.receivePayment(1, new BigDecimal("100.00"));
        store.receivePayment(2, new BigDecimal("100.00"));
        equal(store.findClient(1).getBalance(), "-60.00");
        equal(store.findClient(2).getBalance(), "-51.00");

        // Receive a shipment of 100 P1: fills C3 (3) then C1 (5) FIFO, rest to stock.
        List<ShipmentFulfillment> fills = store.receiveShipment(1, 100);
        check(fills.size() == 2, "Expected two waitlist fulfillments");
        check(fills.get(0).getClient().getClientID() == 3
                && fills.get(0).getFilledQuantity() == 3, "C3 was not filled first");
        check(fills.get(1).getClient().getClientID() == 1
                && fills.get(1).getFilledQuantity() == 5, "C1 was not filled second");
        check(store.getProductWaitlist(1).isEmpty(), "P1 waitlist was not cleared");

        // Final stock: P1-P5 = 92, 7, 25, 33, 39.
        int[] expectedStock = {92, 7, 25, 33, 39};
        for (int i = 1; i <= 5; i++) {
            check(store.findProduct(i).getQuantityInStock() == expectedStock[i - 1],
                    "P" + i + " final stock is wrong");
        }

        // Final balances: C1 $55 credit, C2 $51 credit, C3 $48 owed, C4/C5 $0.
        equal(store.findClient(1).getBalance(), "-55.00");
        check(store.findClient(1).getBalanceDetails().equals("Credit: $55.00"),
                "C1 credit display is wrong");
        equal(store.findClient(2).getBalance(), "-51.00");
        equal(store.findClient(3).getBalance(), "48.00");
        equal(store.findClient(4).getBalance(), "0.00");
        equal(store.findClient(5).getBalance(), "0.00");
        List<Client> owing = store.getClientsWithOutstandingBalance();
        check(owing.size() == 1 && owing.get(0).getClientID() == 3,
                "Only C3 should owe money");

        // Invoices: C1 $40 + $5; C2 $49; C3 $45 + $3.
        invoiceTotals(store, 1, "40.00", "5.00");
        invoiceTotals(store, 2, "49.00");
        invoiceTotals(store, 3, "45.00", "3.00");

        System.out.println("Demo script test passed: final stock 92/7/25/33/39; balances "
                + "C1 $55 credit, C2 $51 credit, C3 $48 owed, C4/C5 $0; "
                + "invoices C1 $40+$5, C2 $49, C3 $45+$3.");
    }

    private static void invoiceTotals(StoreSystem store, int clientID, String... expected) {
        List<Invoice> invoices = store.getInvoices(clientID);
        check(invoices.size() == expected.length,
                "C" + clientID + " should have " + expected.length + " invoices");
        for (int i = 0; i < expected.length; i++) {
            equal(invoices.get(i).getTotal(), expected[i]);
        }
    }

    private static void equal(BigDecimal actual, String expected) {
        check(actual.equals(new BigDecimal(expected)), "Expected " + expected + ", got " + actual);
    }

    private static void check(boolean condition, String message) {
        if (!condition) throw new AssertionError(message);
    }
}
