package client;

import client.ui.LoginFrame;

import java.io.IOException;
import java.net.Socket;

public class ChatClient {
    public static void main(String[] args) {
        try {
            // 서버 소켓 연결
            Socket socket = new Socket("127.0.0.1", 12345);

            // FileManager에 소켓 설정
            FileClient.setSocket(socket);

            // 로그인 프레임 실행
            new LoginFrame(socket);
        } catch (IOException e) {
            System.err.println("서버 연결 실패: " + e.getMessage());
        }
    }
}
