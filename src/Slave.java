import java.io.IOException;
import java.net.*;

/**
 * The Slave class represents the slave mode of the Distributed Averaging System.
 * It sends a single UDP message to the master and exits.
 */

public class Slave implements Startable {
    private final int port;
    private final int number;

    public Slave(int port, int number) {
        this.port = port;
        this.number = number;
    }

    @Override
    public void start() {
        try (DatagramSocket socket = new DatagramSocket()) {
            String message = String.valueOf(number);
            InetAddress localHost = InetAddress.getByName("localhost");
            DatagramPacket packet = new DatagramPacket(message.getBytes(), message.length(), localHost, port);
            socket.send(packet);
            System.out.println("Message sent: " + message);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
