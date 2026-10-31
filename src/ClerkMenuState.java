/** Menu and operations available to a warehouse clerk. */
public final class ClerkMenuState implements State {
    public static final int ID = 2;

    @Override
    public int getId() {
        return ID;
    }

    @Override
    public String getName() {
        return "ClerkMenuState";
    }

    @Override
    public void handle(Context context) {
        System.out.println();
        System.out.println("Clerk Menu");
        System.out.println("1 Add a client");
        System.out.println("2 Show products");
        System.out.println("3 Show all clients");
        System.out.println("4 Show clients with outstanding balances");
        System.out.println("5 Record a client payment");
        System.out.println("6 Become a client");
        System.out.println("7 Save warehouse data");
        System.out.println("0 Logout");
        int choice = context.getMenuChoice("Choose an operation:", 0, 7);

        switch (choice) {
            case 0:
                context.transition(Context.LOGOUT);
                break;
            case 1:
                context.addClient();
                break;
            case 2:
                context.showProducts();
                break;
            case 3:
                context.showClients();
                break;
            case 4:
                context.showOutstandingBalances();
                break;
            case 5:
                context.receivePayment();
                break;
            case 6:
                if (context.selectClient()) {
                    context.transition(Context.BECOME_CLIENT);
                }
                break;
            case 7:
                context.saveWarehouse();
                break;
            default:
                throw new IllegalStateException("Unsupported clerk menu choice.");
        }
    }
}
