import java.io.DataInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Date;
import java.text.SimpleDateFormat;
import java.io.File;

public class FileServerTCP {
    public static void main(String[] args) {
        try {
            ServerSocket serverSocket = new ServerSocket(12345);
            System.out.println("Server is running. Waiting for clients...");

            while (true) {
                Socket clientSocket = serverSocket.accept();
                new Thread(new FileHandler(clientSocket)).start();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}

class FileHandler implements Runnable {
    private Socket clientSocket;

    public FileHandler(Socket clientSocket) {
        this.clientSocket = clientSocket;
    }

    @Override
    public void run() {
        try {
            DataInputStream dis = new DataInputStream(clientSocket.getInputStream());
            String fileName = dis.readUTF();
            long fileSize = dis.readLong();

            String postfixs = new SimpleDateFormat("yyyyMMddHHmmss").format(new Date());

            
			String receivedFolderPath = "terima" + File.separator;
			File receivedFolder = new File(receivedFolderPath);
			if (!receivedFolder.exists()) {
				receivedFolder.mkdir();
			}

			FileOutputStream fos = new FileOutputStream(receivedFolderPath + postfixs + "_" + fileName);
			
			
			
            byte[] buffer = new byte[4096];

            int bytesRead;
            while (fileSize > 0 && (bytesRead = dis.read(buffer, 0, (int) Math.min(buffer.length, fileSize))) != -1) {
                fos.write(buffer, 0, bytesRead);
                fileSize -= bytesRead;
            }

            System.out.println("File received: " + fileName);
            fos.close();
            dis.close();
            clientSocket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}