import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

/** Run with java -cp bin OrderFulfillmentTest. */
public class OrderFulfillmentTest {
    public static void main(String[] args) {
        StoreSystem store = StoreSystem.instance();
        Client firstClient = store.addClient("First", "1 Main Street");
        Client secondClient = store.addClient("Second", "2 Main Street");
        Product stockedProduct = store.addProduct("P1", 5, new BigDecimal("2.00"));
        Product outOfStockProduct = store.addProduct("P2", 0, new BigDecimal("3.00"));

        store.addToWishlist(firstClient.getClientID(), stockedProduct.getProductID(), 7);
        store.addToWishlist(firstClient.getClientID(), outOfStockProduct.getProductID(), 4);
        LocalDate beforeCheckout = LocalDate.now();
        List<OrderFulfillment> firstOrder = store.fillOrder(firstClient.getClientID());
        check(firstOrder.size() == 2, "Expected two fulfilled order lines");
        check(firstOrder.get(0).getSuppliedQuantity() == 5, "Available stock not supplied");
        check(firstOrder.get(0).getWaitlistedQuantity() == 2, "Shortage not waitlisted");
        check(firstOrder.get(1).getSuppliedQuantity() == 0, "Out-of-stock item was supplied");
        check(firstOrder.get(1).getWaitlistedQuantity() == 4, "Full shortage not waitlisted");
        check(stockedProduct.getQuantityInStock() == 0, "Stock was not reduced");
        check(firstClient.getWishlistItems().isEmpty(), "Submitted wishlist was not cleared");
        List<Invoice> invoices = store.getInvoices(firstClient.getClientID());
        check(invoices.size() == 1, "Expected one invoice for supplied items");
        Invoice invoice = invoices.get(0);
        check(!invoice.getDate().isBefore(beforeCheckout)
                && !invoice.getDate().isAfter(LocalDate.now()),
            "Invoice date does not match checkout");
        check(invoice.getLineItems().size() == 1,
            "Invoice should exclude the fully waitlisted product");
        Invoice.LineItem line = invoice.getLineItems().get(0);
        check(line.getProductID() == stockedProduct.getProductID(), "Wrong invoiced product");
        check(line.getQuantity() == 5, "Invoice quantity should include supplied units only");
        check(line.getUnitPrice().equals(new BigDecimal("2.00")), "Invoice unit price changed");
        check(line.getLineTotal().equals(new BigDecimal("10.00")), "Invoice line total is wrong");
        check(invoice.getTotal().equals(new BigDecimal("10.00")), "Invoice total is wrong");

        store.addToWishlist(secondClient.getClientID(), stockedProduct.getProductID(), 3);
        List<OrderFulfillment> secondOrder = store.fillOrder(secondClient.getClientID());
        check(secondOrder.get(0).getWaitlistedQuantity() == 3, "Second shortage not queued");
        check(store.getInvoices(secondClient.getClientID()).isEmpty(),
            "An all-waitlisted order should not create an invoice");

        List<WaitlistEntry> queue = store.getProductWaitlist(stockedProduct.getProductID());
        check(queue.size() == 2, "Expected two FIFO waitlist entries");
        check(queue.get(0).getClient().getClientID() == firstClient.getClientID(),
                "First request is not at the head of the waitlist");
        check(queue.get(0).getRemainingQuantity() == 2, "First shortage quantity is wrong");
        check(queue.get(1).getClient().getClientID() == secondClient.getClientID(),
                "Second request is not behind the first");
        check(queue.get(1).getRemainingQuantity() == 3, "Second shortage quantity is wrong");

        check(store.fillOrder(firstClient.getClientID()).isEmpty(),
                "Repeated checkout unexpectedly submitted the old order");
        check(queue.size() == 2, "Repeated checkout changed the waitlist");
        check(store.getProductWaitlist(999) == null, "Unknown product should have no waitlist");
        System.out.println("Order fulfillment and invoice tests passed: stock, shortages, FIFO, "
            + "supplied-only invoices, and repeat checkout.");
    }

    private static void check(boolean condition, String message) {
        if (!condition) {
            throw new AssertionError(message);
        }
    }
}