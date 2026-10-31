import java.util.*;
import java.math.BigDecimal;

/** Run with java -cp bin StoreSystemTest; checks hardcoded expected results automatically. */
public class StoreSystemTest {

    public static void main(String[] args) {

        StoreSystem store = StoreSystem.instance();

        System.out.println("StoreSystem Test");
        System.out.println("----------------");

        // Create five clients C1 through C5 with generated IDs.
        for (int i = 1; i <= 5; i++) {
            Client client = store.addClient("C" + i, i + "00 Main Street");
            check(client.getClientID() == i, "C" + i + " should receive ID " + i);
        }
        check(store.getAllClients().size() == 5, "Expected five clients");
        check(store.findClient(3).getName().equals("C3"), "Lookup by ID returned the wrong client");
        check(store.findClient(99) == null, "Unknown client ID should return null");

        System.out.println();
        System.out.println("All Clients");
        Iterator<Client> clients = store.getClients();
        while (clients.hasNext()) {
            Client client = clients.next();
            System.out.println(client);
            check(client.getDetails().endsWith("Balance: $0.00"),
                    "New clients should start with a $0.00 balance");
        }

        // Create five products P1 through P5 with generated IDs.
        int[] stock = {10, 20, 30, 40, 50};
        for (int i = 1; i <= 5; i++) {
            Product product = store.addProduct("P" + i, stock[i - 1], new BigDecimal(i + ".00"));
            check(product.getProductID() == i, "P" + i + " should receive ID " + i);
        }
        check(store.getAllProducts().size() == 5, "Expected five products");
        check(store.findProduct(99) == null, "Unknown product ID should return null");

        System.out.println();
        System.out.println("All Products");
        Iterator<Product> products = store.getProducts();
        while (products.hasNext()) {
            System.out.println(products.next());
        }
        check(store.findProduct(2).getDetails().equals(
                "Product ID: 2, Name: P2, Quantity In Stock: 20, Sale Price: $2.00"),
                "P2 details do not match the expected hardcoded text");

        // C1: 5 each of P1, P3 and P5.
        store.addToWishlist(1, 1, 5);
        store.addToWishlist(1, 3, 5);
        store.addToWishlist(1, 5, 5);
        System.out.println();
        System.out.println("C1 Wishlist");
        printWishlist(store.getWishlistItems(1));
        check(store.getWishlistItems(1).size() == 3, "C1 wishlist should hold three products");

        // C2: 7 each of P1, P2 and P4.
        store.addToWishlist(2, 1, 7);
        store.addToWishlist(2, 2, 7);
        store.addToWishlist(2, 4, 7);
        System.out.println();
        System.out.println("C2 Wishlist");
        printWishlist(store.getWishlistItems(2));

        // C3: 6 each of P1, P2 and P5.
        store.addToWishlist(3, 1, 6);
        store.addToWishlist(3, 2, 6);
        store.addToWishlist(3, 5, 6);
        System.out.println();
        System.out.println("C3 Wishlist");
        printWishlist(store.getWishlistItems(3));

        // Adding an existing product replaces its quantity instead of duplicating it.
        store.addToWishlist(2, 2, 9);
        check(store.getWishlistItems(2).size() == 3, "Replacing a quantity added a duplicate");
        check(store.findClient(2).findWishlistItem(2).getQuantity() == 9,
                "C2's P2 quantity should be replaced with 9");

        // Remove a product from a wishlist (add/delete business process).
        check(store.removeFromWishlist(2, 4), "Removing P4 from C2 should succeed");
        check(store.getWishlistItems(2).size() == 2, "C2 wishlist should shrink to two products");
        check(!store.removeFromWishlist(2, 4), "Removing an unlisted product should fail");
        check(!store.removeFromWishlist(99, 1), "Removing for an unknown client should fail");
        System.out.println();
        System.out.println("Updated C2 Wishlist");
        printWishlist(store.getWishlistItems(2));

        // Invalid wishlist requests are rejected without changes.
        check(!store.addToWishlist(99, 1, 5), "Unknown client was accepted");
        check(!store.addToWishlist(1, 99, 5), "Unknown product was accepted");
        check(!store.addToWishlist(1, 1, 0), "Zero quantity was accepted");
        check(store.getWishlistItems(1).size() == 3, "Rejected requests changed C1's wishlist");

        System.out.println();
        System.out.println("PASS: generated IDs, lookups, displays, wishlist "
                + "add/replace/remove, validation.");
    }

    private static void printWishlist(List<WishlistItem> wishlist) {
        for (WishlistItem item : wishlist) {
            System.out.println(item);
        }
    }

    private static void check(boolean condition, String message) {
        if (!condition) {
            System.out.println("FAIL: " + message);
            throw new AssertionError(message);
        }
    }
}
