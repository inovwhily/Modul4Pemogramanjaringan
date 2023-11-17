import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.util.HashMap;
import java.util.Map;

public class ChatServerUDP {
    private static Map<String, InetAddress> clientAddresses = new HashMap<>();
    private static Map<String, Integer> clientPorts = new HashMap<>();

    public static void main(String[] args) {
        try {
            DatagramSocket serverSocket = new DatagramSocket(9876);
            System.out.println("Server is running. Waiting for clients...");

            while (true) {
                byte[] receiveData = new byte[1024];
                DatagramPacket receivePacket = new DatagramPacket(receiveData, receiveData.length);
                serverSocket.receive(receivePacket);

                String message = new String(receivePacket.getData(), 0, receivePacket.getLength());
                String senderNickname = message.split(":")[0];

                if (!clientAddresses.containsKey(senderNickname)) {
                    clientAddresses.put(senderNickname, receivePacket.getAddress());
                    clientPorts.put(senderNickname, receivePacket.getPort());
                }

                System.out.println(senderNickname + ": " + message);

                // Broadcast the message to all clients
                broadcastMessage(message, senderNickname, serverSocket);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static void broadcastMessage(String message, String senderNickname, DatagramSocket serverSocket) {
        for (Map.Entry<String, InetAddress> entry : clientAddresses.entrySet()) {
            String receiverNickname = entry.getKey();

            // Skip broadcasting to the sender
            if (!receiverNickname.equals(senderNickname)) {
                try {
                    byte[] sendData = message.getBytes();
                    InetAddress receiverInetAddress = entry.getValue();
                    int receiverPort = clientPorts.get(receiverNickname);
                    DatagramPacket sendPacket = new DatagramPacket(sendData, sendData.length,
                            receiverInetAddress, receiverPort);
                    serverSocket.send(sendPacket);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}
