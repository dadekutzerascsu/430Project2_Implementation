/** Initial state for selecting a warehouse role. */
public final class OpeningState implements State {
    public static final int ID = 0;

    @Override
    public int getId() {
        return ID;
    }

    @Override
    public String getName() {
        return "OpeningState";
    }

    @Override
    public void handle(Context context) {
        System.out.println();
        System.out.println("Warehouse");
        System.out.println("1 to log in as a client");
        System.out.println("2 to log in as a clerk");
        System.out.println("3 to log in as a manager");
        System.out.println("0 to exit");
        int choice = context.getMenuChoice("Choose a role:", 0, 3);

        if (choice == Context.EXIT) {
            context.transition(Context.EXIT);
        } else if (choice == Context.LOGIN_CLIENT) {
            if (context.selectClient()) {
                context.transition(Context.LOGIN_CLIENT);
            }
        } else if (choice == Context.LOGIN_CLERK) {
            context.transition(Context.LOGIN_CLERK);
        } else {
            context.transition(Context.LOGIN_MANAGER);
        }
    }
}
