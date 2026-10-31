import java.math.BigDecimal;
import java.util.List;

/** Run with java -cp bin ShipmentTest; verifies automatic charges and shipment processing. */
public class ShipmentTest {
    public static void main(String[] args) {
        StoreSystem store = StoreSystem.instance();
        Client firstClient = store.addClient("First", "1 Main Street");
        Client secondClient = store.addClient("Second", "2 Main Street");
        Product product = store.addProduct("P1", 5, new BigDecimal("2.00"));
        Product untouched = store.addProduct("P2", 9, new BigDecimal("4.00"));

        // Checkout automatically charges the invoice total for supplied items.
        store.addToWishlist(firstClient.getClientID(), product.getProductID(), 7);
        store.fillOrder(firstClient.getClientID());
        equal(firstClient.getBalance(), "10.00");
        check(firstClient.getTransactions().size() == 1, "Order charge was not recorded");
        check(firstClient.getTransactions().get(0).getAmount()
                .equals(new BigDecimal("10.00")), "Order charge amount is wrong");

        // A fully waitlisted order creates no invoice and no charge.
        store.addToWishlist(secondClient.getClientID(), product.getProductID(), 4);
        store.fillOrder(secondClient.getClientID());
        check(store.getInvoices(secondClient.getClientID()).isEmpty(),
                "All-waitlisted order should not create an invoice");
        equal(secondClient.getBalance(), "0.00");

        // Waitlist is now FIFO: First waits for 2, Second waits for 4.

        // A shipment smaller than the head entry partially fills it and keeps it queued.
        List<ShipmentFulfillment> partial = store.receiveShipment(product.getProductID(), 1);
        check(partial.size() == 1, "Expected one partial fulfillment");
        check(partial.get(0).getClient() == firstClient, "Wrong client filled first");
        check(partial.get(0).getFilledQuantity() == 1, "Partial fill quantity is wrong");
        check(partial.get(0).getRemainingQuantity() == 1, "Partial fill remainder is wrong");
        equal(partial.get(0).getInvoice().getTotal(), "2.00");
        equal(firstClient.getBalance(), "12.00");
        check(product.getQuantityInStock() == 0, "Shipment bypassed the waitlist");
        List<WaitlistEntry> queue = store.getProductWaitlist(product.getProductID());
        check(queue.size() == 2, "Partially filled request left the waitlist");
        check(queue.get(0).getClient() == firstClient
                && queue.get(0).getRemainingQuantity() == 1,
                "Partially filled request lost its place in line");

        // A spanning shipment clears the head and partially fills the next entry.
        List<ShipmentFulfillment> spanning = store.receiveShipment(product.getProductID(), 3);
        check(spanning.size() == 2, "Expected two FIFO fulfillments");
        check(spanning.get(0).getClient() == firstClient
                && spanning.get(0).getFilledQuantity() == 1
                && spanning.get(0).getRemainingQuantity() == 0, "Head entry fill is wrong");
        check(spanning.get(1).getClient() == secondClient
                && spanning.get(1).getFilledQuantity() == 2
                && spanning.get(1).getRemainingQuantity() == 2, "Second entry fill is wrong");
        equal(firstClient.getBalance(), "14.00");
        equal(secondClient.getBalance(), "4.00");
        queue = store.getProductWaitlist(product.getProductID());
        check(queue.size() == 1, "Fully filled entry was not removed");
        check(queue.get(0).getClient() == secondClient
                && queue.get(0).getRemainingQuantity() == 2, "Remaining entry is wrong");
        check(product.getQuantityInStock() == 0, "Stock changed while the waitlist had entries");

        // A shipment larger than the waitlist clears it and restocks the leftovers.
        List<ShipmentFulfillment> clearing = store.receiveShipment(product.getProductID(), 10);
        check(clearing.size() == 1, "Expected one clearing fulfillment");
        equal(secondClient.getBalance(), "8.00");
        check(store.getProductWaitlist(product.getProductID()).isEmpty(),
                "Waitlist was not cleared");
        check(product.getQuantityInStock() == 8, "Leftover units were not added to stock");

        // Every fulfillment generated its own invoice.
        check(store.getInvoices(firstClient.getClientID()).size() == 3,
                "Expected order invoice plus two shipment invoices for First");
        check(store.getInvoices(secondClient.getClientID()).size() == 2,
                "Expected two shipment invoices for Second");

        // Ledger history records each automatic charge.
        check(firstClient.getTransactions().size() == 3, "First's charges were not all recorded");
        check(secondClient.getTransactions().size() == 2, "Second's charges were not all recorded");

        // A shipment with no waitlist goes straight to stock.
        List<ShipmentFulfillment> stockOnly = store.receiveShipment(untouched.getProductID(), 6);
        check(stockOnly.isEmpty(), "Unexpected fulfillment without a waitlist");
        check(untouched.getQuantityInStock() == 15, "Shipment was not added to stock");

        // Invalid shipments are rejected without changing anything.
        rejects(() -> store.receiveShipment(999, 5));
        rejects(() -> store.receiveShipment(product.getProductID(), 0));
        rejects(() -> store.receiveShipment(product.getProductID(), -3));
        check(product.getQuantityInStock() == 8, "Rejected shipment changed stock");
        equal(firstClient.getBalance(), "14.00");
        equal(secondClient.getBalance(), "8.00");

        System.out.println("Shipment tests passed: automatic order/shipment charges, FIFO fills, "
                + "partial requests retained, per-fill invoices, leftover restocking, validation.");
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
