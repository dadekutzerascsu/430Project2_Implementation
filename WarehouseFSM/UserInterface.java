import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UncheckedIOException;
import java.util.NoSuchElementException;

/** Starts the text-based warehouse finite state machine. */
public final class UserInterface {
    private UserInterface() {
    }

    public static void main(String[] args) {
        BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
        try {
            StoreSystem store = selectWarehouse(reader);
            new Context(store, reader).run();
        } catch (NoSuchElementException exception) {
            System.out.println("Input ended; exiting.");
        }
    }

    private static StoreSystem selectWarehouse(BufferedReader reader) {
        System.out.println("Look for saved warehouse data and use it? (y/n)");
        String answer = readLine(reader);
        if (answer.equalsIgnoreCase("y") || answer.equalsIgnoreCase("yes")) {
            StoreSystem saved = StoreSystem.retrieve();
            if (saved != null) {
                System.out.println("The warehouse has been retrieved from WarehouseData.");
                return saved;
            }
            System.out.println("No saved data found; starting a new warehouse.");
        }
        return StoreSystem.instance();
    }

    private static String readLine(BufferedReader reader) {
        try {
            String line = reader.readLine();
            if (line == null) {
                throw new NoSuchElementException("End of input.");
            }
            return line.trim();
        } catch (IOException exception) {
            throw new UncheckedIOException(exception);
        }
    }
}
