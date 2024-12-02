import java.net.SocketException;

/**
 * Main class for the Distributed Averaging System (DAS).
 * Determines whether to run in master or slave mode based on port availability.
 */
public class DAS {
    public static void main(String[] args) {
        if (args.length != 2) {
            System.out.println("Usage: java DAS <port> <number>");
            return;
        }

        try {
            int port = Integer.parseInt(args[0]);
            int number = Integer.parseInt(args[1]);

            try {
                Startable master = new Master(port, number);
                master.start();
            } catch (SocketException e) {
                Startable slave = new Slave(port, number);
                slave.start();
            }
        } catch (NumberFormatException e) {
            System.out.println("Invalid arguments. Both <port> and <number> must be integers.");
        }
    }
}