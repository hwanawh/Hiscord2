package client;

import client.ui.LoginFrame;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;

public class ChatClient {
    public static void main(String[] args) {
        try {
            // 서버 소켓 연결
            Socket socket = new Socket("127.0.0.1", 12345);
            DataInputStream din = new DataInputStream(socket.getInputStream());
            DataOutputStream dout = new DataOutputStream(socket.getOutputStream());
            // FileManager에 소켓 설정
            FileClient.setSocket(socket);
            FileClient.uploadFile("C:\\Users\\doror\\Desktop\\Hiscord\\resources\\user.txt");
            // 로그인 프레임 실행
            new LoginFrame(socket,din,dout);
        } catch (IOException e) {
            System.err.println("서버 연결 실패: " + e.getMessage());
        }
    }
}
