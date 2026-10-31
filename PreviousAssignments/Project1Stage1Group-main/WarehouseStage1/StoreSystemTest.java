import java.util.*;

/**
 * Driver used to test the StoreSystem class.
 */
public class StoreSystemTest {

    public static void main(String[] args) {

        StoreSystem store =
                StoreSystem.instance();

        System.out.println(
                "StoreSystem Test");

        System.out.println(
                "----------------");


        /*
         * Create five clients:
         * C1 through C5
         */

        store.addClient(
                "C1",
                "100 Main Street");

        store.addClient(
                "C2",
                "200 Main Street");

        store.addClient(
                "C3",
                "300 Main Street");

        store.addClient(
                "C4",
                "400 Main Street");

        store.addClient(
                "C5",
                "500 Main Street");


        System.out.println();
        System.out.println(
                "All Clients");

        Iterator<Client> clients =
                store.getClients();

        while (clients.hasNext()) {

            Client client =
                    clients.next();

            System.out.println(client);
        }


        /*
         * Create five products:
         * P1 through P5
         */

        store.addProduct(
                "P1", 10, 1.00);

        store.addProduct(
                "P2", 20, 2.00);

        store.addProduct(
                "P3", 30, 3.00);

        store.addProduct(
                "P4", 40, 4.00);

        store.addProduct(
                "P5", 50, 5.00);


        System.out.println();
        System.out.println(
                "All Products");

        Iterator<Product> products =
                store.getProducts();

        while (products.hasNext()) {

            Product product =
                    products.next();

            System.out.println(product);
        }


        /*
         * C1:
         * 5 each of P1, P3 and P5
         */

        store.addToWishlist(
                1, 1, 5);

        store.addToWishlist(
                1, 3, 5);

        store.addToWishlist(
                1, 5, 5);

        System.out.println();
        System.out.println(
                "C1 Wishlist");

        printWishlist(
                store.getWishlistItems(1));


        /*
         * C2:
         * 7 each of P1, P2 and P4
         */

        store.addToWishlist(
                2, 1, 7);

        store.addToWishlist(
                2, 2, 7);

        store.addToWishlist(
                2, 4, 7);

        System.out.println();
        System.out.println(
                "C2 Wishlist");

        printWishlist(
                store.getWishlistItems(2));


        /*
         * C3:
         * 6 each of P1, P2 and P5
         */

        store.addToWishlist(
                3, 1, 6);

        store.addToWishlist(
                3, 2, 6);

        store.addToWishlist(
                3, 5, 6);

        System.out.println();
        System.out.println(
                "C3 Wishlist");

        printWishlist(
                store.getWishlistItems(3));


        /*
         * Add 7 each of P3 and P5
         * to C2.
         */

        store.addToWishlist(
                2, 3, 7);

        store.addToWishlist(
                2, 5, 7);

        System.out.println();
        System.out.println(
                "Updated C2 Wishlist");

        printWishlist(
                store.getWishlistItems(2));


        /*
         * Print C3 wishlist again.
         */

        System.out.println();
        System.out.println(
                "C3 Wishlist");

        printWishlist(
                store.getWishlistItems(3));
    }

    /**
     * Prints the contents of a wishlist.
     *
     * @param wishlist list of WishlistItems
     */
    public static void printWishlist(
            List<WishlistItem> wishlist) {

        if (wishlist == null) {

            System.out.println(
                    "Wishlist not found");

            return;
        }

        for (WishlistItem item :
                wishlist) {

            System.out.println(item);
        }
    }
}
