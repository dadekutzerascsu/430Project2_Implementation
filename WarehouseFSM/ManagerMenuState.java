/** Menu and operations available to a warehouse manager. */
public final class ManagerMenuState implements State {
    public static final int ID = 3;

    @Override
    public int getId() {
        return ID;
    }

    @Override
    public String getName() {
        return "ManagerMenuState";
    }

    @Override
    public void handle(Context context) {
        System.out.println();
        System.out.println("Manager Menu");
        System.out.println("1 Add a product");
        System.out.println("2 Display a product waitlist");
        System.out.println("3 Receive a shipment");
        System.out.println("4 Become a clerk");
        System.out.println("5 Save warehouse data");
        System.out.println("0 Logout");
        int choice = context.getMenuChoice("Choose an operation:", 0, 5);

        switch (choice) {
            case 0:
                context.transition(Context.LOGOUT);
                break;
            case 1:
                context.addProduct();
                break;
            case 2:
                context.showProductWaitlist();
                break;
            case 3:
                context.receiveShipment();
                break;
            case 4:
                context.transition(Context.BECOME_CLERK);
                break;
            case 5:
                context.saveWarehouse();
                break;
            default:
                throw new IllegalStateException("Unsupported manager menu choice.");
        }
    }
}
