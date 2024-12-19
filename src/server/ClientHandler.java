package server;

import models.User;

import java.io.*;
import java.net.Socket;
import java.util.*;

public class ClientHandler implements Runnable {
    private Socket socket;
    private BufferedReader in;
    private PrintWriter out;
    String username;
    private String currentChannel = "channel1";

    public ClientHandler(Socket socket) {
        this.socket = socket;
    }

    @Override
    public void run() {
        try {
            in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            out = new PrintWriter(socket.getOutputStream(), true);

            String message;
            while ((message = in.readLine()) != null) { //join시
                if (message.startsWith("/")) {
                    // 명령어 추출 (첫 번째 단어)
                    String command = message.split(" ", 2)[0].trim();
                    String argument = message.substring(command.length()).trim(); // 명령어 이후의 내용

                    switch (command) {
                        case "/join":
                            String newChannel = argument; // '/join ' 이후의 내용
                            ChannelManager.leaveChannel(currentChannel, this);
                            currentChannel = newChannel;
                            ChannelManager.joinChannel(currentChannel, this);
                            out.println("/join " + currentChannel);
                            break;

                        case "/login":
                            // argument는 "ID/PASSWORD" 형태
                            String[] credentials = argument.split("/", 2);
                            if (credentials.length == 2) {
                                String id = credentials[0].trim();
                                String password = credentials[1].trim();

                                // UserManager의 로그인 인증
                                if (UserManager.authenticateUser(id, password)) {
                                    User user = UserManager.getUserById(id); // User 객체 반환
                                    if (user != null) {
                                        // 인증 성공: 사용자 이름 반환
                                        out.println(user.getName());
                                    } else {
                                        // 인증 실패: 사용자 정보 없음
                                        out.println("Login Failed: Invalid credentials");
                                    }
                                } else {
                                    // 인증 실패
                                    out.println("Login Failed: Invalid credentials");
                                }
                            } else {
                                // 잘못된 명령어 형식 처리
                                out.println("Login Failed: Incorrect command format");
                            }
                            break;

                        case "/signup":
                            break;

                        default:
                            out.println("Unknown command: " + command);
                            break;
                    }
                } else {
                    // 일반 메시지인 경우
                    ChannelManager.broadcast(currentChannel, username + ": " + message);
                }

            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                socket.close();
                ChannelManager.leaveChannel(currentChannel, this);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }


    public void sendMessage(String message) {
        out.println(message);
    }
}
