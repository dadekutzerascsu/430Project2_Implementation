import java.util.*;
import java.io.*;

/**
 * Represents a client in the warehouse system.
 */
public class Client implements Serializable {

    private static final long serialVersionUID = 1L;

    private int clientID;
    private String name;
    private String address;
    private double balance;
    private List<WishlistItem> wishlistItems;

    /**
     * Creates a Client.
     *
     * @param clientID the unique client ID
     * @param name the client's name
     * @param address the client's address
     */
    public Client(int clientID, String name, String address) {

        this.clientID = clientID;
        this.name = name;
        this.address = address;
        this.balance = 0.0;

        wishlistItems = new LinkedList<WishlistItem>();
    }

    /**
     * Returns the client ID.
     *
     * @return client ID
     */
    public int getClientID() {
        return clientID;
    }

    /**
     * Returns the client's name.
     *
     * @return client name
     */
    public String getName() {
        return name;
    }

    /**
     * Returns the client's address.
     *
     * @return client address
     */
    public String getAddress() {
        return address;
    }

    /**
     * Returns the client's credit/debit balance.
     *
     * @return client balance
     */
    public double getBalance() {
        return balance;
    }

    /**
     * Searches the client's wishlist for a product.
     *
     * @param productID the product ID
     * @return WishlistItem if found, otherwise null
     */
    public WishlistItem findWishlistItem(int productID) {

        for (WishlistItem item : wishlistItems) {

            if (item.getProduct().getProductID() == productID) {
                return item;
            }
        }

        return null;
    }

    /**
     * Adds a product to the client's wishlist.
     * If the product already exists, the quantity is updated.
     *
     * @param product the product
     * @param quantity the desired quantity
     * @return true when the operation is completed
     */
    public boolean addWishlistItem(Product product, int quantity) {

        WishlistItem item =
                findWishlistItem(product.getProductID());

        if (item == null) {

            wishlistItems.add(
                    new WishlistItem(product, quantity));

        } else {

            item.setQuantity(quantity);
        }

        return true;
    }

    /**
     * Returns all wishlist items.
     *
     * @return list of WishlistItems
     */
    public List<WishlistItem> getWishlistItems() {
        return wishlistItems;
    }

    /**
     * Returns the client's information.
     *
     * @return client details
     */
    public String getDetails() {

        return "Client ID: " + clientID
                + ", Name: " + name
                + ", Address: " + address
                + ", Balance: $"
                + String.format("%.2f", balance);
    }

    /**
     * Returns the client's information.
     *
     * @return client information
     */
    public String toString() {
        return getDetails();
    }
}
