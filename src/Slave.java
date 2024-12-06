import java.io.IOException;
import java.net.*;

/**
 * The Slave class represents the slave mode of the Distributed Averaging System.
 * It sends a single UDP message containing a number to the master and then exits.
 *
 * This class implements the Startable interface and defines the start method to send a message.
 */
public class Slave implements Startable {
    /** The port number to send the message to. */
    private final int port;

    /** The numeric message to send. */
    private final int number;

    /**
     * Constructs a Slave instance with the specified port and number.
     *
     * @param port The port to send messages to.
     * @param number The number to send in the UDP message.
     */
    public Slave(int port, int number) {
        this.port = port;
        this.number = number;
    }

    /**
     * Starts the slave operation by sending the number as a UDP message to the master.
     */
    @Override
    public void start() {
        try {
            DatagramSocket socket = new DatagramSocket();
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