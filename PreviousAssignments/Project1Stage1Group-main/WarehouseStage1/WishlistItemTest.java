/**
 * Driver used to test the WishlistItem class.
 */
public class WishlistItemTest {

    public static void main(String[] args) {

        Product p1 =
                new Product(1, "P1", 10, 1.00);

        WishlistItem item =
                new WishlistItem(p1, 5);

        System.out.println("WishlistItem Test");
        System.out.println("-----------------");

        System.out.println(item);

        System.out.println(
                "should show P1 with wishlist quantity 5");

        System.out.println(
                item.getQuantity());

        System.out.println(
                "should be 5");

        item.setQuantity(7);

        System.out.println(
                item.getQuantity());

        System.out.println(
                "should now be 7");
    }
}
