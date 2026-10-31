import java.util.*;
import java.io.*;
import java.math.BigDecimal;

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
    private static final int RECEIVE_PAYMENT = 8;
    private static final int SHOW_CLIENT_DETAILS = 9;
    private static final int SHOW_TRANSACTIONS = 10;
    private static final int REVIEW_WISHLIST = 11;
    private static final int RECEIVE_SHIPMENT = 12;
    private static final int SHOW_PRODUCT_WAITLIST = 13;
    private static final int SHOW_INVOICES = 14;
    private static final int SHOW_OUTSTANDING_BALANCES = 15;
    private static final int SAVE = 16;
    private static final int REMOVE_FROM_WISHLIST = 17;

    /**
     * Creates the UserInterface and offers to
     * resume from previously saved data.
     */
    private UserInterface() {

        if (yesOrNo("Look for saved warehouse data and use it?")) {
            retrieveSavedData();
        } else {
            storeSystem = StoreSystem.instance();
        }
    }

    /** Reads a yes/no answer; only input starting with y or Y counts as yes. */
    private boolean yesOrNo(String prompt) {
        String answer = getToken(prompt + " (y/n)");
        return answer.charAt(0) == 'y' || answer.charAt(0) == 'Y';
    }

    /** Loads saved data when available, otherwise starts a new warehouse. */
    private void retrieveSavedData() {
        StoreSystem saved = StoreSystem.retrieve();
        if (saved == null) {
            System.out.println("No saved data found; starting a new warehouse.");
            storeSystem = StoreSystem.instance();
        } else {
            System.out.println("The warehouse has been retrieved from the file WarehouseData.");
            storeSystem = saved;
        }
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

                if (line == null) {
                    throw new NoSuchElementException("End of input.");
                }
                line = line.trim();

                StringTokenizer tokenizer =
                        new StringTokenizer(
                                line,
                                "\n\r\f");

                if (tokenizer.hasMoreTokens()) {
                    return tokenizer.nextToken();
                }

            } catch (IOException ioe) {

                throw new UncheckedIOException(ioe);
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
     * Reads a positive monetary amount directly as a decimal, without rounding.
     *
     * @param prompt prompt shown to the user
     * @return validated amount in whole cents
     */
    public BigDecimal getMoney(String prompt) {

        do {

            try {

                String item =
                        getToken(prompt);

                return Money.positive(new BigDecimal(item));

            } catch (IllegalArgumentException exception) {

                System.out.println(
                        "Enter a positive amount in whole cents (for example 10.50).");
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
                    && value <= REMOVE_FROM_WISHLIST) {

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
            "Enter a number between 0 and " + REMOVE_FROM_WISHLIST + ":");

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

        System.out.println(RECEIVE_PAYMENT + " to receive a client payment");
        System.out.println(SHOW_CLIENT_DETAILS + " to show a client's details and balance");
        System.out.println(SHOW_TRANSACTIONS + " to show a client's transactions");
        System.out.println(REVIEW_WISHLIST + " to review a wishlist at checkout");
        System.out.println(RECEIVE_SHIPMENT + " to receive a product shipment");
        System.out.println(SHOW_PRODUCT_WAITLIST + " to show a product's waitlist");
        System.out.println(SHOW_INVOICES + " to show a client's invoices");
        System.out.println(SHOW_OUTSTANDING_BALANCES + " to show clients that owe money");
        System.out.println(SAVE + " to save the warehouse data");
        System.out.println(REMOVE_FROM_WISHLIST + " to remove a product from a wishlist");

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

        BigDecimal price =
                getMoney(
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

        displayWishlist(clientID, wishlist);
    }

    /** Reviews a wishlist and allows edits or additional products before fulfillment. */
    public void reviewWishlist() {
        int clientID = getNumber("Enter client ID");
        List<WishlistItem> wishlist = storeSystem.getWishlistItems(clientID);
        if (wishlist == null) {
            System.out.println("Client not found.");
            return;
        }

        while (true) {
            wishlist = storeSystem.getWishlistItems(clientID);
            displayWishlist(clientID, wishlist);
            System.out.println("1 to change an item's quantity");
            System.out.println("2 to add another product");
            System.out.println("3 to finish review");
            int action = getNumber("Choose review action:");

            if (action == 1) {
                if (wishlist.isEmpty()) {
                    System.out.println("There are no wishlist items to change.");
                    continue;
                }
                int productID = getNumber("Enter product ID to change");
                int quantity = getNumber("Enter new quantity");
                if (storeSystem.updateWishlistQuantity(clientID, productID, quantity)) {
                    System.out.println("Wishlist quantity updated.");
                } else {
                    System.out.println("Could not update that wishlist item.");
                }
            } else if (action == 2) {
                showProducts();
                int productID = getNumber("Enter product ID to add");
                int quantity = getNumber("Enter quantity");
                if (storeSystem.addToWishlist(clientID, productID, quantity)) {
                    System.out.println("Product added to wishlist.");
                } else {
                    System.out.println("Could not add product to wishlist.");
                }
            } else if (action == 3) {
                List<OrderFulfillment> results = storeSystem.fillOrder(clientID);
                if (results.isEmpty()) {
                    System.out.println("Wishlist is empty; no order was submitted.");
                } else {
                    boolean invoiceCreated = false;
                    for (OrderFulfillment result : results) {
                        System.out.println(result);
                        invoiceCreated |= result.getSuppliedQuantity() > 0;
                    }
                    if (invoiceCreated) {
                        List<Invoice> invoices = storeSystem.getInvoices(clientID);
                        System.out.println(invoices.get(invoices.size() - 1));
                    } else {
                        System.out.println("No invoice generated; no items were supplied.");
                    }
                    System.out.println("Order submitted; processed wishlist entries cleared.");
                }
                return;
            } else {
                System.out.println("Choose 1, 2 or 3.");
            }
        }
    }

    private void displayWishlist(int clientID, List<WishlistItem> wishlist) {
        System.out.println();
        System.out.println("Wishlist for Client " + clientID);
        System.out.println("---------------------");
        if (wishlist.isEmpty()) {
            System.out.println("Wishlist is empty.");
        } else {
            for (WishlistItem item : wishlist) {
                System.out.println(item);
            }
        }
        System.out.println();
    }

    /** Follows the payment use case: find client, show balance, credit payment. */
    public void receivePayment() {
        int clientID = getNumber("Enter client ID");
        Client client = storeSystem.findClient(clientID);
        if (client == null) {
            System.out.println("Client not found.");
            return;
        }
        System.out.println(client.getBalanceDetails());
        BigDecimal amount = getMoney("Enter payment amount");
        storeSystem.receivePayment(clientID, amount);
        System.out.println("Payment recorded.");
        System.out.println(client.getBalanceDetails());
    }

    public void showClientDetails() {
        Client client = storeSystem.findClient(getNumber("Enter client ID"));
        if (client == null) {
            System.out.println("Client not found.");
            return;
        }
        System.out.println(client.getDetails());
    }

    public void showTransactions() {
        List<Transaction> transactions =
                storeSystem.getTransactions(getNumber("Enter client ID"));
        if (transactions.isEmpty()) {
            System.out.println("No transactions found.");
            return;
        }
        for (Transaction transaction : transactions) {
            System.out.println(transaction);
        }
    }

    /** Receives a shipment: waitlist fills are invoiced/charged, leftovers restock. */
    public void receiveShipment() {
        int productID = getNumber("Enter product ID");
        Product product = storeSystem.findProduct(productID);
        if (product == null) {
            System.out.println("Product not found.");
            return;
        }
        int quantity = getNumber("Enter received quantity");
        if (quantity <= 0) {
            System.out.println("Received quantity must be positive.");
            return;
        }

        List<ShipmentFulfillment> fulfillments =
                storeSystem.receiveShipment(productID, quantity);
        if (fulfillments.isEmpty()) {
            System.out.println("No waitlisted requests; full shipment added to stock.");
        } else {
            System.out.println("Waitlist fulfillments:");
            for (ShipmentFulfillment fulfillment : fulfillments) {
                System.out.println(fulfillment);
                System.out.println(fulfillment.getInvoice());
            }
        }
        System.out.println(product);
    }

    /** Displays a product's FIFO waitlist and total waitlisted quantity. */
    public void showProductWaitlist() {
        int productID = getNumber("Enter product ID");
        Product product = storeSystem.findProduct(productID);
        if (product == null) {
            System.out.println("Product not found.");
            return;
        }

        List<WaitlistEntry> waitlist = storeSystem.getProductWaitlist(productID);
        System.out.println();
        System.out.println("Waitlist for " + product.getName()
                + " (Product " + productID + ")");
        System.out.println("--------------------------------");
        if (waitlist.isEmpty()) {
            System.out.println("Waitlist is empty.");
        } else {
            for (WaitlistEntry entry : waitlist) {
                System.out.println(entry);
            }
            System.out.println("Total waitlisted quantity: "
                    + product.getTotalWaitlistedQuantity());
        }
        System.out.println();
    }

    /** Displays each dated invoice for one client. */
    public void showInvoices() {
        int clientID = getNumber("Enter client ID");
        Client client = storeSystem.findClient(clientID);
        if (client == null) {
            System.out.println("Client not found.");
            return;
        }

        List<Invoice> invoices = storeSystem.getInvoices(clientID);
        System.out.println();
        System.out.println("Invoices for Client " + clientID
                + " (" + client.getName() + ")");
        System.out.println("--------------------------------");
        if (invoices.isEmpty()) {
            System.out.println("No invoices found.");
        }
        for (Invoice invoice : invoices) {
            System.out.println(invoice);
            System.out.println();
        }
    }

    /** Displays every client that currently owes money. */
    public void showOutstandingBalances() {
        List<Client> owing = storeSystem.getClientsWithOutstandingBalance();
        System.out.println();
        System.out.println("Clients with Outstanding Balances");
        System.out.println("---------------------------------");
        if (owing.isEmpty()) {
            System.out.println("No clients owe money.");
        }
        for (Client client : owing) {
            System.out.println(client);
        }
        System.out.println();
    }

    /** Saves the warehouse, including ID counters, to the WarehouseData file. */
    public void save() {
        if (StoreSystem.save()) {
            System.out.println("The warehouse has been saved to the file WarehouseData.");
        } else {
            System.out.println("The warehouse could not be saved.");
        }
    }

    /** Removes a product from a client's wishlist (add/delete business process). */
    public void removeFromWishlist() {
        int clientID = getNumber("Enter client ID");
        int productID = getNumber("Enter product ID");
        if (storeSystem.removeFromWishlist(clientID, productID)) {
            System.out.println("Product removed from wishlist.");
        } else {
            System.out.println("Could not remove product from wishlist.");
        }
    }

    /**
     * Processes commands until the user exits.
     */
    public void process() {

        int command;

        help();

        try {
            while ((command = getCommand()) != EXIT) {
                try {
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
                        case RECEIVE_PAYMENT:
                            receivePayment();
                            break;
                        case SHOW_CLIENT_DETAILS:
                            showClientDetails();
                            break;
                        case SHOW_TRANSACTIONS:
                            showTransactions();
                            break;
                        case REVIEW_WISHLIST:
                            reviewWishlist();
                            break;
                        case RECEIVE_SHIPMENT:
                            receiveShipment();
                            break;
                        case SHOW_PRODUCT_WAITLIST:
                            showProductWaitlist();
                            break;
                        case SHOW_INVOICES:
                            showInvoices();
                            break;
                        case SHOW_OUTSTANDING_BALANCES:
                            showOutstandingBalances();
                            break;
                        case SAVE:
                            save();
                            break;
                        case REMOVE_FROM_WISHLIST:
                            removeFromWishlist();
                            break;
                    }
                } catch (IllegalArgumentException exception) {
                    System.out.println("Error: " + exception.getMessage());
                }
            }
        } catch (NoSuchElementException exception) {
            // End redirected input or a closed console without a stack trace.
        } catch (UncheckedIOException exception) {
            System.out.println("Could not read input: " + exception.getCause().getMessage());
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

        try {
            UserInterface
                    .instance()
                    .process();
        } catch (NoSuchElementException exception) {
            // End of input before startup finished.
            System.out.println("Exiting Warehouse System.");
        }
    }
}
