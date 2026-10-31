/** A state in the warehouse user-interface finite state machine. */
public interface State {
    int getId();
    String getName();
    void handle(Context context);
}
