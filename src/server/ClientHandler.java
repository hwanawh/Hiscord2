package server;

import java.io.*;
import java.net.Socket;
import java.util.*;

public class ClientHandler implements Runnable {
    private Socket socket;
    private BufferedReader in;
    private PrintWriter out;
    String username;
    private String currentChannel = "general";

    public ClientHandler(Socket socket) {
        this.socket = socket;
    }

    @Override
    public void run() {
        try {
            in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            out = new PrintWriter(socket.getOutputStream(), true);

            // Handle login
            username = in.readLine();
            ChannelManager.joinChannel(currentChannel, this);
            out.println(username + "님 환영합니다" +  "!");

            String message;
            while ((message = in.readLine()) != null) {
                if (message.startsWith("/join ")) {
                    String newChannel = message.substring(6).trim(); // '/join ' 이후의 모든 내용을 가져옴
                    ChannelManager.leaveChannel(currentChannel, this);
                    currentChannel = newChannel;
                    ChannelManager.joinChannel(currentChannel, this);
                    loadChat(currentChannel);
                } else {
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

    public void loadChat(String channelName) {
        this.currentChannel = channelName; // 현재 채널 업데이트
        String projectDir = System.getProperty("user.dir");
        String path = projectDir + "/resources/channel/" + currentChannel + "/chats.txt";
        System.out.println(path);
        try {
            File chatFile = new File(path);

            // 파일이 존재하는지 확인
            if (!chatFile.exists()) {
                out.println("[" + channelName + "] 채팅 기록 파일을 찾을 수 없습니다.");
                return;
            }

            BufferedReader reader = new BufferedReader(new FileReader(chatFile));
            String line;
            out.println("[" + channelName + "] 채팅 기록 로드 시작:");

            while ((line = reader.readLine()) != null) {
                // 데이터 포맷: 123,2024-12-16,16:05:37,안녕하세요,NULL,NULL
                String[] parts = line.split(",");

                if (parts.length >= 4) {
                    String id = parts[0].trim();
                    String date = parts[1].trim();
                    String time = parts[2].trim();
                    String message = parts[3].trim();

                    // 채팅 내용을 화면에 추가
                    out.println("[" + date + " " + time + "] " + id + ": " + message);
                } else {
                    out.println("잘못된 데이터 형식: " + line);
                }
            }

            reader.close();
            out.println("[" + channelName + "] 채팅 기록 로드 완료.");
        } catch (IOException e) {
            out.println("[" + channelName + "] 채팅 기록 로드 중 오류 발생: " + e.getMessage());
        }
    }

    public void sendMessage(String message) {
        out.println(message);
    }
}
