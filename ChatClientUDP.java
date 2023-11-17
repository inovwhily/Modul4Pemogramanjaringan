import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.util.Scanner;

public class ChatClientUDP {
    public static void main(String[] args) {
        try {
            DatagramSocket clientSocket = new DatagramSocket();
            InetAddress serverAddress = InetAddress.getByName("localhost");

            Scanner scanner = new Scanner(System.in);

            // Get the nickname from the user
            System.out.print("Enter your nickname: ");
            String nickname = scanner.nextLine();

            // Send login message to the server
            String loginMessage = nickname + " has joined the chat.";
            byte[] loginData = loginMessage.getBytes();
            DatagramPacket loginPacket = new DatagramPacket(loginData, loginData.length,
                    serverAddress, 9876);
            clientSocket.send(loginPacket);

            // Start a new thread to receive messages from the server
            new Thread(() -> {
                while (true) {
                    try {
                        byte[] receiveData = new byte[1024];
                        DatagramPacket receivePacket = new DatagramPacket(receiveData, receiveData.length);
                        clientSocket.receive(receivePacket);

                        String message = new String(receivePacket.getData(), 0, receivePacket.getLength());

                        // Check if the message is not from the current client
                        if (!message.startsWith(nickname + ": ")) {
                            System.out.println("" + message);
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }).start();

            while (true) {
                System.out.print("->");
                String message = scanner.nextLine();

                // Prepend the nickname to the message
                message = nickname + ": " + message;

                byte[] sendData = message.getBytes();
                DatagramPacket sendPacket = new DatagramPacket(sendData, sendData.length,
                        serverAddress, 9876);
                clientSocket.send(sendPacket);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
