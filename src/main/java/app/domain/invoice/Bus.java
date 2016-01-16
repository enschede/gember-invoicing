package app.domain.invoice;

/**
 * Created by marc on 15/01/16.
 */
public class Bus {
    public void executeCommand(Command command) {
        command.execute();
    }
}
