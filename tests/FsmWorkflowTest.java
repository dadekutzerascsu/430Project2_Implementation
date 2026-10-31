import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.math.BigDecimal;
import java.util.List;

/** Exercises the assignment workflow through the FSM menus and checks its results. */
public class FsmWorkflowTest {
    public static void main(String[] args) {
        StoreSystem store = StoreSystem.instance();
        ByteArrayOutputStream output = new ByteArrayOutputStream();
        PrintStream originalOut = System.out;
        Context context;
        try {
            System.setOut(new PrintStream(output));
            context = new Context(store, new BufferedReader(new InputStreamReader(
                    new ByteArrayInputStream(buildInput().getBytes()))));
            context.run();
        } finally {
            System.setOut(originalOut);
        }

        check(context.getCurrentState() == null, "FSM did not exit after the workflow");
        check(store.getAllClients().size() == 5, "Expected five clients");
        check(store.getAllProducts().size() == 5, "Expected five products");
        check(store.findClient(1).getBalance().equals(new BigDecimal("0.00")),
                "C1 should not have placed an order");
        check(store.findClient(2).getBalance().equals(new BigDecimal("49.00")),
                "C2 order total should be $49.00");
        check(store.findClient(3).getBalance().equals(new BigDecimal("45.00")),
                "C3 should be charged only for supplied items");
        int[] expectedStock = {0, 7, 30, 33, 44};
        for (int id = 1; id <= expectedStock.length; id++) {
            check(store.findProduct(id).getQuantityInStock() == expectedStock[id - 1],
                    "Unexpected stock for P" + id);
        }
        List<WaitlistEntry> p1Waitlist = store.getProductWaitlist(1);
        check(p1Waitlist.size() == 1
                && p1Waitlist.get(0).getClient().getClientID() == 3
                && p1Waitlist.get(0).getRemainingQuantity() == 3,
                "C3's P1 shortage should be waitlisted");
        check(output.toString().contains("Wishlist for Client 1")
                && output.toString().contains("Clients with outstanding balances")
                && output.toString().contains("Manager Menu")
                && output.toString().contains("Client not found."),
                "Expected workflow screens or invalid-ID handling were not shown");

        System.out.println("FSM workflow test passed: menus, role transitions, orders, "
                + "balances, and waitlist verified.");
    }

    private static String buildInput() {
        StringBuilder input = new StringBuilder();
        choose(input, 1);
        line(input, 999);
        choose(input, 2);
        for (int id = 1; id <= 5; id++) {
            choose(input, 1);
            line(input, "C" + id);
            line(input, id + "00 Main Street");
        }
        choose(input, 3);
        choose(input, 6);
        line(input, 999);
        choose(input, 0);

        choose(input, 3);
        int[] stock = {10, 20, 30, 40, 50};
        for (int id = 1; id <= 5; id++) {
            choose(input, 1);
            line(input, "P" + id);
            line(input, stock[id - 1]);
            line(input, id + ".00");
        }
        choose(input, 4);
        choose(input, 2);

        choose(input, 6);
        line(input, 1);
        wishlist(input, new int[][] {{1, 5}, {3, 5}, {5, 5}});
        choose(input, 5);
        choose(input, 0);

        choose(input, 6);
        line(input, 2);
        wishlist(input, new int[][] {{1, 7}, {2, 7}, {4, 7}});
        choose(input, 5);
        choose(input, 0);
        choose(input, 0);
        choose(input, 0);

        choose(input, 1);
        line(input, 3);
        wishlist(input, new int[][] {{1, 6}, {2, 6}, {5, 6}});
        choose(input, 5);
        choose(input, 0);

        choose(input, 1);
        line(input, 2);
        choose(input, 6);
        choose(input, 0);

        choose(input, 2);
        choose(input, 3);
        choose(input, 6);
        line(input, 3);
        choose(input, 6);
        choose(input, 0);
        choose(input, 3);
        choose(input, 4);
        choose(input, 0);
        choose(input, 0);
        return input.toString();
    }

    private static void wishlist(StringBuilder input, int[][] items) {
        for (int[] item : items) {
            choose(input, 4);
            line(input, item[0]);
            line(input, item[1]);
        }
    }

    private static void choose(StringBuilder input, int choice) {
        line(input, choice);
    }

    private static void line(StringBuilder input, Object value) {
        input.append(value).append('\n');
    }

    private static void check(boolean condition, String message) {
        if (!condition) {
            throw new AssertionError(message);
        }
    }
}
