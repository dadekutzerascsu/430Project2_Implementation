import java.util.*;
import java.io.*;

/**
 * Controls the warehouse system.
 * StoreSystem manages clients, products,
 * and client wishlists.
 */
public class StoreSystem implements Serializable {

    private static final long serialVersionUID = 1L;

    private static StoreSystem storeSystem;

    private List<Client> clients;
    private List<Product> products;

    private int nextClientID;
    private int nextProductID;

    /**
     * Creates the StoreSystem.
     */
    private StoreSystem() {

        clients = new LinkedList<Client>();
        products = new LinkedList<Product>();

        nextClientID = 1;
        nextProductID = 1;
    }

    /**
     * Returns the single StoreSystem instance.
     *
     * @return StoreSystem instance
     */
    public static StoreSystem instance() {

        if (storeSystem == null) {

            storeSystem =
                    new StoreSystem();
        }

        return storeSystem;
    }

    /**
     * Creates and adds a new client.
     *
     * @param name client name
     * @param address client address
     * @return newly created Client
     */
    public Client addClient(String name,
                            String address) {

        Client client =
                new Client(
                        generateClientID(),
                        name,
                        address);

        clients.add(client);

        return client;
    }

    /**
     * Generates a unique client ID.
     *
     * @return next client ID
     */
    public int generateClientID() {
        return nextClientID++;
    }

    /**
     * Creates and adds a new product.
     *
     * @param name product name
     * @param quantityInStock quantity in stock
     * @param salePrice unit price
     * @return newly created Product
     */
    public Product addProduct(
            String name,
            int quantityInStock,
            double salePrice) {

        Product product =
                new Product(
                        generateProductID(),
                        name,
                        quantityInStock,
                        salePrice);

        products.add(product);

        return product;
    }

    /**
     * Generates a unique product ID.
     *
     * @return next product ID
     */
    public int generateProductID() {
        return nextProductID++;
    }

    /**
     * Searches for a client.
     *
     * @param clientID client ID
     * @return Client if found, otherwise null
     */
    public Client findClient(int clientID) {

        for (Client client : clients) {

            if (client.getClientID()
                    == clientID) {

                return client;
            }
        }

        return null;
    }

    /**
     * Searches for a product.
     *
     * @param productID product ID
     * @return Product if found, otherwise null
     */
    public Product findProduct(
            int productID) {

        for (Product product : products) {

            if (product.getProductID()
                    == productID) {

                return product;
            }
        }

        return null;
    }

    /**
     * Adds a product to a client's wishlist.
     *
     * @param clientID client ID
     * @param productID product ID
     * @param quantity desired quantity
     * @return true if successful, otherwise false
     */
    public boolean addToWishlist(
            int clientID,
            int productID,
            int quantity) {

        Client client =
                findClient(clientID);

        Product product =
                findProduct(productID);

        if (client == null
                || product == null) {

            return false;
        }

        return client.addWishlistItem(
                product,
                quantity);
    }

    /**
     * Returns an iterator for all clients.
     *
     * @return client Iterator
     */
    public Iterator<Client> getClients() {
        return clients.iterator();
    }

    /**
     * Returns an iterator for all products.
     *
     * @return product Iterator
     */
    public Iterator<Product> getProducts() {
        return products.iterator();
    }

    /**
     * Returns all clients.
     *
     * @return client list
     */
    public List<Client> getAllClients() {
        return clients;
    }

    /**
     * Returns all products.
     *
     * @return product list
     */
    public List<Product> getAllProducts() {
        return products;
    }

    /**
     * Returns the wishlist for a client.
     *
     * @param clientID client ID
     * @return wishlist or null
     */
    public List<WishlistItem>
            getWishlistItems(int clientID) {

        Client client =
                findClient(clientID);

        if (client == null) {
            return null;
        }

        return client.getWishlistItems();
    }
}
