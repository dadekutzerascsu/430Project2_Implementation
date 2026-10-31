import java.io.BufferedReader;
import java.io.IOException;
import java.io.UncheckedIOException;
import java.math.BigDecimal;
import java.util.ArrayDeque;
import java.util.Deque;
import java.util.List;
import java.util.NoSuchElementException;

/** Owns the warehouse FSM, transition matrix, role history, and console I/O. */
public final class Context {
    public static final int EXIT = 0;
    public static final int LOGIN_CLIENT = 1;
    public static final int LOGIN_CLERK = 2;
    public static final int LOGIN_MANAGER = 3;
    public static final int LOGOUT = 4;
    public static final int BECOME_CLIENT = 5;
    public static final int BECOME_CLERK = 6;

    private static final int RETURN = -1;
    private static final int TERMINATED = -2;

    private static final int[][] TRANSITIONS = {
        // exit, client login, clerk login, manager login, logout, become client, become clerk
        {TERMINATED, ClientMenuState.ID, ClerkMenuState.ID, ManagerMenuState.ID,
            OpeningState.ID, OpeningState.ID, OpeningState.ID},
        {ClientMenuState.ID, ClientMenuState.ID, ClientMenuState.ID, ClientMenuState.ID,
            RETURN, ClientMenuState.ID, ClientMenuState.ID},
        {ClerkMenuState.ID, ClerkMenuState.ID, ClerkMenuState.ID, ClerkMenuState.ID,
            RETURN, ClientMenuState.ID, ClerkMenuState.ID},
        {ManagerMenuState.ID, ManagerMenuState.ID, ManagerMenuState.ID, ManagerMenuState.ID,
            RETURN, ManagerMenuState.ID, ClerkMenuState.ID}
    };

    private final StoreSystem store;
    private final BufferedReader reader;
    private final State[] states = {
        new OpeningState(),
        new ClientMenuState(),
        new ClerkMenuState(),
        new ManagerMenuState()
    };
    private final Deque<State> previousStates = new ArrayDeque<State>();
    private State currentState = states[OpeningState.ID];
    private Integer activeClientId;

    public Context(StoreSystem store, BufferedReader reader) {
        if (store == null || reader == null) {
            throw new IllegalArgumentException("A warehouse and input reader are required.");
        }
        this.store = store;
        this.reader = reader;
    }

    public void run() {
        while (currentState != null) {
            currentState.handle(this);
        }
    }

    public State getCurrentState() {
        return currentState;
    }

    public String getCurrentStateName() {
        return currentState == null ? "Exited" : currentState.getName();
    }

    public int getActiveClientId() {
        if (activeClientId == null) {
            throw new IllegalStateException("No client is active.");
        }
        return activeClientId.intValue();
    }

    public Client getActiveClient() {
        return store.findClient(getActiveClientId());
    }

    /** Applies a transition through the state-by-event matrix. */
    public void transition(int event) {
        if (currentState == null || event < EXIT || event > BECOME_CLERK) {
            throw new IllegalArgumentException("Unknown FSM transition.");
        }
        int targetId = TRANSITIONS[currentState.getId()][event];
        if (targetId == TERMINATED) {
            currentState = null;
            previousStates.clear();
            activeClientId = null;
            return;
        }
        if (targetId == RETURN) {
            activeClientId = null;
            currentState = previousStates.isEmpty()
                    ? states[OpeningState.ID] : previousStates.pop();
            return;
        }
        State target = states[targetId];
        if (target != currentState) {
            previousStates.push(currentState);
            currentState = target;
        }
        if (currentState.getId() != ClientMenuState.ID) {
            activeClientId = null;
        }
    }

    public boolean selectClient() {
        int id = getPositiveNumber("Enter client ID:");
        if (store.findClient(id) == null) {
            System.out.println("Client not found.");
            return false;
        }
        activeClientId = Integer.valueOf(id);
        return true;
    }

    public String getToken(String prompt) {
        while (true) {
            System.out.println(prompt);
            try {
                String line = reader.readLine();
                if (line == null) {
                    throw new NoSuchElementException("End of input.");
                }
                line = line.trim();
                if (!line.isEmpty()) {
                    return line;
                }
                System.out.println("Input cannot be empty.");
            } catch (IOException exception) {
                throw new UncheckedIOException(exception);
            }
        }
    }

    public int getMenuChoice(String prompt, int minimum, int maximum) {
        while (true) {
            try {
                int choice = Integer.parseInt(getToken(prompt));
                if (choice >= minimum && choice <= maximum) {
                    return choice;
                }
            } catch (NumberFormatException exception) {
                System.out.println("Enter a number between " + minimum + " and " + maximum + ".");
                continue;
            }
            System.out.println("Enter a number between " + minimum + " and " + maximum + ".");
        }
    }

    private int getPositiveNumber(String prompt) {
        while (true) {
            int value = getMenuChoice(prompt, 1, Integer.MAX_VALUE);
            if (value > 0) {
                return value;
            }
        }
    }

    private int getNonnegativeNumber(String prompt) {
        return getMenuChoice(prompt, 0, Integer.MAX_VALUE);
    }

    private BigDecimal getMoney(String prompt) {
        while (true) {
            try {
                return Money.positive(new BigDecimal(getToken(prompt)));
            } catch (IllegalArgumentException exception) {
                System.out.println("Enter a positive amount in whole cents (for example 10.50).");
            }
        }
    }

    public void addClient() {
        Client client = store.addClient(getToken("Enter client name:"),
                getToken("Enter client address:"));
        System.out.println("Client added: " + client);
    }

    public void addProduct() {
        String name = getToken("Enter product name:");
        int quantity = getNonnegativeNumber("Enter quantity in stock:");
        BigDecimal price = getMoney("Enter unit price:");
        Product product = store.addProduct(name, quantity, price);
        System.out.println("Product added: " + product);
    }

    public void showClients() {
        System.out.println("All clients");
        List<Client> clients = store.getAllClients();
        if (clients.isEmpty()) {
            System.out.println("No clients found.");
        }
        for (Client client : clients) {
            System.out.println(client);
        }
    }

    public void showProducts() {
        System.out.println("All products");
        List<Product> products = store.getAllProducts();
        if (products.isEmpty()) {
            System.out.println("No products found.");
        }
        for (Product product : products) {
            System.out.println(product);
        }
    }

    public void showOutstandingBalances() {
        System.out.println("Clients with outstanding balances");
        List<Client> clients = store.getClientsWithOutstandingBalance();
        if (clients.isEmpty()) {
            System.out.println("No outstanding balances.");
        }
        for (Client client : clients) {
            System.out.println(client);
        }
    }

    public void addToWishlist(int clientId) {
        showProducts();
        int productId = getPositiveNumber("Enter product ID:");
        int quantity = getPositiveNumber("Enter wishlist quantity:");
        if (store.addToWishlist(clientId, productId, quantity)) {
            System.out.println("Product added to wishlist.");
        } else {
            System.out.println("Could not add product to wishlist.");
        }
    }

    public void removeFromWishlist(int clientId) {
        int productId = getPositiveNumber("Enter product ID to remove:");
        if (store.removeFromWishlist(clientId, productId)) {
            System.out.println("Product removed from wishlist.");
        } else {
            System.out.println("Product was not in the wishlist.");
        }
    }

    public void showWishlist(int clientId) {
        System.out.println("Wishlist for Client " + clientId);
        List<WishlistItem> items = store.getWishlistItems(clientId);
        if (items == null) {
            System.out.println("Client not found.");
        } else if (items.isEmpty()) {
            System.out.println("Wishlist is empty.");
        } else {
            for (WishlistItem item : items) {
                System.out.println(item);
            }
        }
    }

    public void showTransactions(int clientId) {
        List<Transaction> transactions = store.getTransactions(clientId);
        if (transactions.isEmpty()) {
            System.out.println("No transactions found.");
        } else {
            for (Transaction transaction : transactions) {
                System.out.println(transaction);
            }
        }
    }

    public void showInvoices(int clientId) {
        List<Invoice> invoices = store.getInvoices(clientId);
        if (invoices.isEmpty()) {
            System.out.println("No invoices found.");
        } else {
            for (Invoice invoice : invoices) {
                System.out.println(invoice);
            }
        }
    }

    public void placeOrder(int clientId) {
        List<OrderFulfillment> results = store.fillOrder(clientId);
        if (results.isEmpty()) {
            System.out.println("Wishlist is empty; no order was submitted.");
            return;
        }
        boolean suppliedItems = false;
        for (OrderFulfillment result : results) {
            System.out.println(result);
            suppliedItems |= result.getSuppliedQuantity() > 0;
        }
        if (suppliedItems) {
            List<Invoice> invoices = store.getInvoices(clientId);
            System.out.println(invoices.get(invoices.size() - 1));
        } else {
            System.out.println("No invoice generated; no items were supplied.");
        }
        System.out.println("Order submitted; processed wishlist entries cleared.");
    }

    public void receivePayment() {
        int clientId = getPositiveNumber("Enter client ID:");
        Client client = store.findClient(clientId);
        if (client == null) {
            System.out.println("Client not found.");
            return;
        }
        System.out.println(client.getBalanceDetails());
        store.receivePayment(clientId, getMoney("Enter payment amount:"));
        System.out.println("Payment recorded.");
        System.out.println(client.getBalanceDetails());
    }

    public void showProductWaitlist() {
        int productId = getPositiveNumber("Enter product ID:");
        Product product = store.findProduct(productId);
        if (product == null) {
            System.out.println("Product not found.");
            return;
        }
        List<WaitlistEntry> waitlist = store.getProductWaitlist(productId);
        System.out.println("Waitlist for " + product.getName() + " (P" + productId + ")");
        if (waitlist.isEmpty()) {
            System.out.println("Waitlist is empty.");
        } else {
            for (WaitlistEntry entry : waitlist) {
                System.out.println(entry);
            }
            System.out.println("Total waitlisted quantity: " + product.getTotalWaitlistedQuantity());
        }
    }

    public void receiveShipment() {
        int productId = getPositiveNumber("Enter product ID:");
        if (store.findProduct(productId) == null) {
            System.out.println("Product not found.");
            return;
        }
        int quantity = getPositiveNumber("Enter received quantity:");
        List<ShipmentFulfillment> results = store.receiveShipment(productId, quantity);
        if (results.isEmpty()) {
            System.out.println("No waitlisted requests; full shipment added to stock.");
        } else {
            System.out.println("Waitlist fulfillments:");
            for (ShipmentFulfillment result : results) {
                System.out.println(result);
                System.out.println(result.getInvoice());
            }
        }
        System.out.println(store.findProduct(productId));
    }

    public void saveWarehouse() {
        if (StoreSystem.save()) {
            System.out.println("Warehouse data saved.");
        } else {
            System.out.println("Could not save warehouse data.");
        }
    }
}
