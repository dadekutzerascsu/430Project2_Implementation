import java.io.File;
import java.math.BigDecimal;
import java.util.List;

/** Run with java -cp bin SaveLoadTest; verifies saving and loading preserve data and ID counters. */
public class SaveLoadTest {
    public static void main(String[] args) {
        File saveFile = new File("WarehouseData");
        try {
            StoreSystem store = StoreSystem.instance();
            Client firstClient = store.addClient("First", "1 Main Street");
            Client secondClient = store.addClient("Second", "2 Main Street");
            Product product = store.addProduct("P1", 5, new BigDecimal("2.00"));

            store.addToWishlist(secondClient.getClientID(), product.getProductID(), 2);
            store.addToWishlist(firstClient.getClientID(), product.getProductID(), 7);
            store.fillOrder(firstClient.getClientID());
            store.receivePayment(firstClient.getClientID(), new BigDecimal("4.00"));

            check(StoreSystem.save(), "Save reported failure");
            check(saveFile.isFile(), "Save file was not created");

            // Changes made after the save must disappear when the saved state is loaded.
            store.addClient("Extra", "9 Main Street");
            store.addProduct("Extra product", 1, new BigDecimal("1.00"));
            store.receivePayment(firstClient.getClientID(), new BigDecimal("6.00"));

            StoreSystem restored = StoreSystem.retrieve();
            check(restored != null, "Retrieve returned no warehouse");
            check(restored == StoreSystem.instance(), "Retrieve did not replace the instance");
            check(restored.getAllClients().size() == 2, "Client list was not restored");
            check(restored.getAllProducts().size() == 1, "Product list was not restored");

            Client restoredFirst = restored.findClient(firstClient.getClientID());
            equal(restoredFirst.getBalance(), "6.00");
            check(restoredFirst.getTransactions().size() == 2, "Transactions were not restored");
            List<Invoice> invoices = restored.getInvoices(firstClient.getClientID());
            check(invoices.size() == 1, "Invoices were not restored");
            check(invoices.get(0).getTotal().equals(new BigDecimal("10.00")),
                    "Invoice total changed");

            Product restoredProduct = restored.findProduct(product.getProductID());
            check(restoredProduct.getQuantityInStock() == 0, "Stock was not restored");
            List<WaitlistEntry> waitlist =
                    restored.getProductWaitlist(restoredProduct.getProductID());
            check(waitlist.size() == 1, "Waitlist was not restored");
            check(waitlist.get(0).getRemainingQuantity() == 2, "Waitlisted quantity changed");
            check(waitlist.get(0).getClient() == restoredFirst,
                    "Waitlist entry lost its link to the restored client");

            List<WishlistItem> wishlist =
                    restored.getWishlistItems(secondClient.getClientID());
            check(wishlist.size() == 1, "Wishlist was not restored");
            check(wishlist.get(0).getProduct() == restoredProduct,
                    "Wishlist item lost its link to the restored product");

            // ID counters continue from the saved state.
            check(restored.addClient("Third", "3 Main Street").getClientID() == 3,
                    "Client ID counter was not preserved");
            check(restored.addProduct("P2", 1, new BigDecimal("1.00")).getProductID() == 2,
                    "Product ID counter was not preserved");

            // Restored data still supports shipment processing end to end.
            restored.receiveShipment(restoredProduct.getProductID(), 2);
            equal(restoredFirst.getBalance(), "10.00");
            check(restored.getProductWaitlist(restoredProduct.getProductID()).isEmpty(),
                    "Restored waitlist was not fulfilled");
            check(restoredProduct.getQuantityInStock() == 0,
                    "Shipment for the waitlist changed restored stock");

            System.out.println("Save/load tests passed: data, relationships "
                    + "and ID counters are preserved.");
        } finally {
            saveFile.delete();
        }
    }

    private static void equal(BigDecimal actual, String expected) {
        check(actual.equals(new BigDecimal(expected)), "Expected " + expected + ", got " + actual);
    }

    private static void check(boolean condition, String message) {
        if (!condition) throw new AssertionError(message);
    }
}
