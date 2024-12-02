import java.io.IOException;
import java.net.*;
import java.util.ArrayList;

/**
 * The Master class represents the master mode of the Distributed Averaging System.
 * It listens for UDP messages and processes them.
 */
public class Master implements Startable {
    private final int port;
    private final int number;
    private DatagramSocket socket;
    private final ArrayList<Integer> receivedNumbers = new ArrayList<>();

    public Master(int port, int number) throws SocketException {
        this.port = port;
        this.number = number;
        this.socket = new DatagramSocket(port);
    }

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
                    System.out.println("-1 received. Broadcasting and shutting down...");
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

    private int calculateAverage() {
        int sum = 0;
        for (int num : receivedNumbers) {
            if (num != 0) sum += num;
        }
        return sum / receivedNumbers.size();
    }

    private void broadcast(String message) {
        try {
            InetAddress broadcastAddress = InetAddress.getByName("255.255.255.255");
            DatagramPacket packet = new DatagramPacket(message.getBytes(), message.length(), broadcastAddress, port);
            socket.send(packet);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}