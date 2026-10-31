import java.math.BigDecimal;

/** Run with java -cp bin ClientTest; checks hardcoded expected results automatically. */
public class ClientTest {

    public static void main(String[] args) {

        Client c1 = new Client(1, "C1", "100 Main Street");
        Product p1 = new Product(1, "P1", 10, new BigDecimal("1.00"));
        Product p2 = new Product(2, "P2", 20, new BigDecimal("2.00"));

        System.out.println("Client Test");
        System.out.println("-----------");

        System.out.println(c1);
        check(c1.getDetails().equals(
                "Client ID: 1, Name: C1, Address: 100 Main Street, Balance: $0.00"),
                "Client details do not match the expected hardcoded text");
        check(c1.getBalance().equals(new BigDecimal("0.00")),
                "New client balance should be $0.00");

        check(c1.findWishlistItem(1) == null,
                "Empty wishlist should not contain P1");

        c1.addWishlistItem(p1, 5);
        WishlistItem item = c1.findWishlistItem(1);
        System.out.println(item);
        check(item != null && item.getQuantity() == 5,
                "P1 should be wishlisted with quantity 5");

        c1.addWishlistItem(p1, 7);
        check(c1.findWishlistItem(1).getQuantity() == 7,
                "Re-adding P1 should replace the quantity with 7");
        check(c1.getWishlistItems().size() == 1,
                "Re-adding P1 should not create a second entry");

        c1.addWishlistItem(p2, 3);
        check(c1.getWishlistItems().size() == 2,
                "Wishlist should contain two products");

        check(c1.removeWishlistItem(2), "Removing a listed product should succeed");
        check(c1.getWishlistItems().size() == 1,
                "Wishlist should contain one product after removal");
        check(!c1.removeWishlistItem(2), "Removing an unlisted product should fail");
        check(c1.findWishlistItem(1).getQuantity() == 7,
                "Removal should not change the remaining entry");

        System.out.println("PASS: client details, balance, wishlist add/replace/remove.");
    }

    private static void check(boolean condition, String message) {
        if (!condition) {
            System.out.println("FAIL: " + message);
            throw new AssertionError(message);
        }
    }
}
