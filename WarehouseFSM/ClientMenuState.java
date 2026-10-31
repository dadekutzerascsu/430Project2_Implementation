import java.util.List;

/** Menu and operations available to the active client. */
public final class ClientMenuState implements State {
    public static final int ID = 1;

    @Override
    public int getId() {
        return ID;
    }

    @Override
    public String getName() {
        return "ClientMenuState";
    }

    @Override
    public void handle(Context context) {
        System.out.println();
        System.out.println("Client Menu - " + context.getActiveClient().getName()
                + " (C" + context.getActiveClientId() + ")");
        System.out.println("1 Show client details");
        System.out.println("2 Show products");
        System.out.println("3 Show transactions");
        System.out.println("4 Add item to wishlist");
        System.out.println("5 Display wishlist");
        System.out.println("6 Place order");
        System.out.println("7 Show invoices");
        System.out.println("8 Remove item from wishlist");
        System.out.println("0 Logout");
        int choice = context.getMenuChoice("Choose an operation:", 0, 8);

        switch (choice) {
            case 0:
                context.transition(Context.LOGOUT);
                break;
            case 1:
                System.out.println(context.getActiveClient().getDetails());
                break;
            case 2:
                context.showProducts();
                break;
            case 3:
                context.showTransactions(context.getActiveClientId());
                break;
            case 4:
                context.addToWishlist(context.getActiveClientId());
                break;
            case 5:
                context.showWishlist(context.getActiveClientId());
                break;
            case 6:
                context.placeOrder(context.getActiveClientId());
                break;
            case 7:
                context.showInvoices(context.getActiveClientId());
                break;
            case 8:
                context.removeFromWishlist(context.getActiveClientId());
                break;
            default:
                throw new IllegalStateException("Unsupported client menu choice.");
        }
    }
}
