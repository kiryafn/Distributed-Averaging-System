import java.io.IOException;
import java.net.*;
import java.util.ArrayList;

/**
 * The Master class represents the master mode of a Distributed Averaging System.
 * It listens on the specified port for incoming UDP messages and processes them according to a predefined protocol.
 *
 * The protocol expects integer messages, where:
 * - Receiving a signal of -1 stops the server and broadcasts this termination signal to all listening clients.
 * - Receiving 0 triggers the computation and broadcasting of the average of all received numbers.
 * - Any other integer is stored for future average computation.
 *
 * Upon initialization, it binds to the specified port and starts listening for messages.
 * Implements the Startable interface, specifically the start method to commence operation.
 *
 * @throws SocketException If the socket cannot be opened or bound to the specified port.
 */
public class Master implements Startable {

    /** The port number on which the server operates. */
    private final int port;

    /** The initial number received to be added to the list of numbers. */
    private final int number;

    /** Socket for receiving and sending UDP packets. */
    private final DatagramSocket socket;

    /** List for storing all received numbers. */
    private final ArrayList<Integer> receivedNumbers = new ArrayList<>();

    /**
     * Constructor for the Master class, initializing the port, number, and network socket.
     *
     * @param port The port to bind and listen for UDP messages.
     * @param number The number to be added to the list of received numbers.
     * @throws SocketException If the socket cannot be opened or bound to the specified port.
     */
    public Master(int port, int number) throws SocketException {
        this.port = port;
        this.number = number;
        this.socket = new DatagramSocket(port);
    }

    /**
     * Activates master mode operation.
     * Listens for incoming messages and processes them in accordance with the protocol.
     */
    @Override
    public void start() {
        System.out.println("Master mode activated on port " + port);
        receivedNumbers.add(number);

        byte[] buffer = new byte[1024];
        try {
            while (true) {
                DatagramPacket packet = new DatagramPacket(buffer, buffer.length);
                socket.receive(packet);
                String message = new String(packet.getData(), 0, packet.getLength());
                int received = Integer.parseInt(message);

                if (received == -1) {
                    System.out.println("-1 received. Broadcasting termination signal and stopping...");
                    broadcast("-1");
                    break;
                } else if (received == 0) {
                    int average = calculateAverage();
                    System.out.println("Broadcasting average: " + average);
                    broadcast(String.valueOf(average));
                } else {
                    System.out.println("Received: " + received);
                    receivedNumbers.add(received);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            socket.close();
        }
    }

    /**
     * Calculates the integer average of all non-zero stored numbers.
     *
     * @return The average value as an integer.
     */
    private int calculateAverage() {
        int sum = 0;
        for (int num : receivedNumbers) {
            if (num != 0) sum += num;
        }
        return (int)Math.floor(sum / receivedNumbers.size());
    }

    /**
     * Sends the specified message to the broadcast address.
     *
     * @param message The message to be sent.
     */
    private void broadcast(String message) {
        try {
            InetAddress broadcastAddress = getBroadcastAddress();
            byte[] messageBytes = message.getBytes();
            DatagramPacket packet = new DatagramPacket(messageBytes, messageBytes.length, broadcastAddress, port);
            socket.send(packet);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Determines and returns the broadcast address for the current network.
     *
     * @return InetAddress representing the broadcast address.
     * @throws UnknownHostException If the local host cannot be resolved into an address.
     */
    private InetAddress getBroadcastAddress() throws UnknownHostException {
        InetAddress localHost = InetAddress.getLocalHost();
        byte[] ip = localHost.getAddress();
        ip[3] = (byte) 255; // Set the last byte to 255 for broadcasting
        return InetAddress.getByAddress(ip);
    }
}