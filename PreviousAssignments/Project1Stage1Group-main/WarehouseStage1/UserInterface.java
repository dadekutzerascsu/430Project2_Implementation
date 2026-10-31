import java.util.*;
import java.io.*;

/**
 * Provides an interactive user interface
 * for the warehouse system.
 */
public class UserInterface {

    private static UserInterface userInterface;

    private BufferedReader reader =
            new BufferedReader(
                    new InputStreamReader(System.in));

    private static StoreSystem storeSystem;

    private static final int EXIT = 0;
    private static final int ADD_CLIENT = 1;
    private static final int SHOW_CLIENTS = 2;
    private static final int ADD_PRODUCT = 3;
    private static final int SHOW_PRODUCTS = 4;
    private static final int ADD_TO_WISHLIST = 5;
    private static final int SHOW_WISHLIST = 6;
    private static final int HELP = 7;

    /**
     * Creates the UserInterface.
     */
    private UserInterface() {
        storeSystem = StoreSystem.instance();
    }

    /**
     * Returns the single UserInterface instance.
     *
     * @return UserInterface instance
     */
    public static UserInterface instance() {

        if (userInterface == null) {
            userInterface =
                    new UserInterface();
        }

        return userInterface;
    }

    /**
     * Reads text input from the user.
     *
     * @param prompt prompt shown to the user
     * @return entered text
     */
    public String getToken(String prompt) {

        do {

            try {

                System.out.println(prompt);

                String line =
                        reader.readLine();

                StringTokenizer tokenizer =
                        new StringTokenizer(
                                line,
                                "\n\r\f");

                if (tokenizer.hasMoreTokens()) {
                    return tokenizer.nextToken();
                }

            } catch (IOException ioe) {

                System.exit(0);
            }

        } while (true);
    }

    /**
     * Reads an integer from the user.
     *
     * @param prompt prompt shown to the user
     * @return integer entered
     */
    public int getNumber(String prompt) {

        do {

            try {

                String item =
                        getToken(prompt);

                Integer number =
                        Integer.valueOf(item);

                return number.intValue();

            } catch (NumberFormatException nfe) {

                System.out.println(
                        "Please input a number.");
            }

        } while (true);
    }

    /**
     * Reads a decimal number from the user.
     *
     * @param prompt prompt shown to the user
     * @return decimal number entered
     */
    public double getDouble(String prompt) {

        do {

            try {

                String item =
                        getToken(prompt);

                return Double.parseDouble(item);

            } catch (NumberFormatException nfe) {

                System.out.println(
                        "Please input a valid price.");
            }

        } while (true);
    }

    /**
     * Reads a command from the user.
     *
     * @return command number
     */
    public int getCommand() {

        do {

            try {

                int value =
                        Integer.parseInt(
                                getToken(
                                        "Enter command: "
                                                + HELP
                                                + " for help"));

                if (value >= EXIT
                        && value <= HELP) {

                    return value;
                }

            } catch (NumberFormatException nfe) {

                System.out.println(
                        "Enter a number.");
            }

        } while (true);
    }

    /**
     * Displays the available commands.
     */
    public void help() {

        System.out.println();
        System.out.println(
                "Enter a number between 0 and 7:");

        System.out.println(
                EXIT + " to Exit");

        System.out.println(
                ADD_CLIENT + " to add a client");

        System.out.println(
                SHOW_CLIENTS + " to show all clients");

        System.out.println(
                ADD_PRODUCT + " to add a product");

        System.out.println(
                SHOW_PRODUCTS + " to show all products");

        System.out.println(
                ADD_TO_WISHLIST
                        + " to add a product to a wishlist");

        System.out.println(
                SHOW_WISHLIST
                        + " to show a client's wishlist");

        System.out.println(
                HELP + " for help");

        System.out.println();
    }

    /**
     * Adds a client to the warehouse system.
     */
    public void addClient() {

        String name =
                getToken("Enter client name");

        String address =
                getToken("Enter client address");

        Client result =
                storeSystem.addClient(
                        name,
                        address);

        if (result == null) {

            System.out.println(
                    "Could not add client.");

        } else {

            System.out.println(
                    "Client added:");

            System.out.println(result);
        }
    }

    /**
     * Displays all clients.
     */
    public void showClients() {

        Iterator<Client> clients =
                storeSystem.getClients();

        System.out.println();
        System.out.println("All Clients");
        System.out.println("-----------");

        while (clients.hasNext()) {

            Client client =
                    clients.next();

            System.out.println(client);
        }

        System.out.println();
    }

    /**
     * Adds a product to the warehouse system.
     */
    public void addProduct() {

        String name =
                getToken("Enter product name");

        int quantity =
                getNumber(
                        "Enter quantity in stock");

        double price =
                getDouble(
                        "Enter unit price");

        Product result =
                storeSystem.addProduct(
                        name,
                        quantity,
                        price);

        if (result == null) {

            System.out.println(
                    "Could not add product.");

        } else {

            System.out.println(
                    "Product added:");

            System.out.println(result);
        }
    }

    /**
     * Displays all products.
     */
    public void showProducts() {

        Iterator<Product> products =
                storeSystem.getProducts();

        System.out.println();
        System.out.println("All Products");
        System.out.println("------------");

        while (products.hasNext()) {

            Product product =
                    products.next();

            System.out.println(product);
        }

        System.out.println();
    }

    /**
     * Adds a product to a client's wishlist.
     */
    public void addToWishlist() {

        int clientID =
                getNumber(
                        "Enter client ID");

        int productID =
                getNumber(
                        "Enter product ID");

        int quantity =
                getNumber(
                        "Enter wishlist quantity");

        boolean result =
                storeSystem.addToWishlist(
                        clientID,
                        productID,
                        quantity);

        if (result) {

            System.out.println(
                    "Product added to wishlist.");

        } else {

            System.out.println(
                    "Could not add product to wishlist.");
        }
    }

    /**
     * Displays a client's wishlist.
     */
    public void showWishlist() {

        int clientID =
                getNumber(
                        "Enter client ID");

        List<WishlistItem> wishlist =
                storeSystem.getWishlistItems(
                        clientID);

        if (wishlist == null) {

            System.out.println(
                    "Client not found.");

            return;
        }

        System.out.println();
        System.out.println(
                "Wishlist for Client "
                        + clientID);

        System.out.println(
                "---------------------");

        if (wishlist.size() == 0) {

            System.out.println(
                    "Wishlist is empty.");

        } else {

            for (WishlistItem item :
                    wishlist) {

                System.out.println(item);
            }
        }

        System.out.println();
    }

    /**
     * Processes commands until the user exits.
     */
    public void process() {

        int command;

        help();

        while ((command = getCommand())
                != EXIT) {

            switch (command) {

                case ADD_CLIENT:
                    addClient();
                    break;

                case SHOW_CLIENTS:
                    showClients();
                    break;

                case ADD_PRODUCT:
                    addProduct();
                    break;

                case SHOW_PRODUCTS:
                    showProducts();
                    break;

                case ADD_TO_WISHLIST:
                    addToWishlist();
                    break;

                case SHOW_WISHLIST:
                    showWishlist();
                    break;

                case HELP:
                    help();
                    break;
            }
        }

        System.out.println(
                "Exiting Warehouse System.");
    }

    /**
     * Starts the warehouse program.
     *
     * @param args command-line arguments
     */
    public static void main(String[] args) {

        UserInterface
                .instance()
                .process();
    }
}
