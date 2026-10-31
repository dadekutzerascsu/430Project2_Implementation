import java.math.BigDecimal;

/** Run with java -cp bin WishlistItemTest; checks hardcoded expected results automatically. */
public class WishlistItemTest {

    public static void main(String[] args) {

        Product p1 = new Product(1, "P1", 10, new BigDecimal("1.00"));
        WishlistItem item = new WishlistItem(p1, 5);

        System.out.println("WishlistItem Test");
        System.out.println("-----------------");

        System.out.println(item);
        check(item.toString().equals(
                "Product ID: 1, Name: P1, Quantity In Stock: 10, "
                + "Sale Price: $1.00, Wishlist Quantity: 5"),
                "Wishlist item text does not match the expected hardcoded text");

        check(item.getQuantity() == 5, "Initial quantity should be 5");
        check(item.getProduct() == p1, "Item should reference the product");

        item.setQuantity(7);
        check(item.getQuantity() == 7, "Updated quantity should be 7");

        rejects(() -> item.setQuantity(0));
        rejects(() -> item.setQuantity(-2));
        rejects(() -> new WishlistItem(null, 5));
        rejects(() -> new WishlistItem(p1, 0));
        check(item.getQuantity() == 7, "Rejected updates should not change the quantity");

        System.out.println("PASS: wishlist item display, quantity update, validation.");
    }

    private static void check(boolean condition, String message) {
        if (!condition) {
            System.out.println("FAIL: " + message);
            throw new AssertionError(message);
        }
    }

    private static void rejects(Runnable action) {
        try {
            action.run();
            check(false, "Invalid operation was accepted");
        } catch (IllegalArgumentException expected) { }
    }
}
