package server;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class ChatServer {
    private static final int PORT = 12345;
    private static ExecutorService pool = Executors.newFixedThreadPool(10);
     // UserManager 객체 하나만 생성

    public static void main(String[] args) {
        System.out.println("Chat server started...");
        try (ServerSocket serverSocket = new ServerSocket(PORT)) {
            while (true) {

                Socket clientSocket = serverSocket.accept();
                DataInputStream din = new DataInputStream(clientSocket.getInputStream());
                DataOutputStream dout = new DataOutputStream(clientSocket.getOutputStream());

                System.out.println("New client connected");
                FileServer fileServer = new FileServer(din,dout);
                ClientHandler clientHandler = new ClientHandler(din,dout); // UserManager를 ClientHandler에 전달
                pool.execute(clientHandler);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
