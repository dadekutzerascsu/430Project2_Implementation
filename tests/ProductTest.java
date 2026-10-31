import java.math.BigDecimal;

/** Run with java -cp bin ProductTest; checks hardcoded expected results automatically. */
public class ProductTest {

    public static void main(String[] args) {

        Product p1 = new Product(1, "P1", 10, new BigDecimal("1.00"));
        Product p2 = new Product(2, "P2", 20, new BigDecimal("2.00"));
        Product p3 = new Product(3, "P3", 30, new BigDecimal("3.00"));

        System.out.println("Product Test");
        System.out.println("------------");

        System.out.println(p1);
        System.out.println(p2);
        System.out.println(p3);

        check(p1.getDetails().equals(
                "Product ID: 1, Name: P1, Quantity In Stock: 10, Sale Price: $1.00"),
                "P1 details do not match the expected hardcoded text");
        check(p2.getDetails().equals(
                "Product ID: 2, Name: P2, Quantity In Stock: 20, Sale Price: $2.00"),
                "P2 details do not match the expected hardcoded text");
        check(p3.getDetails().equals(
                "Product ID: 3, Name: P3, Quantity In Stock: 30, Sale Price: $3.00"),
                "P3 details do not match the expected hardcoded text");

        check(p1.fulfillFromStock(4) == 4, "P1 should supply 4 units from stock");
        check(p1.getQuantityInStock() == 6, "P1 stock should drop to 6");
        p1.addStock(10);
        check(p1.getQuantityInStock() == 16, "P1 stock should rise to 16");
        check(p1.getTotalWaitlistedQuantity() == 0, "New product should have no waitlist");

        rejects(() -> new Product(0, "Bad ID", 1, new BigDecimal("1.00")));
        rejects(() -> new Product(4, " ", 1, new BigDecimal("1.00")));
        rejects(() -> new Product(4, "Bad stock", -1, new BigDecimal("1.00")));
        rejects(() -> new Product(4, "Bad price", 1, new BigDecimal("0.00")));
        rejects(() -> p1.addStock(0));

        System.out.println("PASS: product details, stock supply/restock, validation.");
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
