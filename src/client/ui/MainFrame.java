package client.ui;

import javax.swing.*;
import java.awt.*;
import java.io.*;
import java.net.Socket;
import java.util.Arrays;

public class MainFrame extends JFrame {
    public MainFrame(String username) {
        setTitle("Chat - " + username);
        setSize(1200, 1000);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());

        try {
            Socket socket = new Socket("localhost", 12345);
            BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            PrintWriter out = new PrintWriter(socket.getOutputStream(), true);

            // 채팅 패널 및 채널 패널 추가
            ChatPanel chatPanel = new ChatPanel(out);
            add(chatPanel, BorderLayout.CENTER);

            ChannelPanel channelPanel = new ChannelPanel(out);
            add(channelPanel, BorderLayout.WEST);

            // 접속 중인 멤버 패널 추가
            RightPanel rightPanel = new RightPanel();
            add(rightPanel, BorderLayout.EAST);

            // 서버 메시지 수신
            new Thread(() -> {
                try {
                    String message;
                    while ((message = in.readLine()) != null) {
                        if (message.startsWith("/members")) {
                            // 멤버 리스트 업데이트 메시지 처리
                            String[] members = message.substring(9).split(",");
                            rightPanel.updateMembers(Arrays.asList(members));
                        } else {
                            chatPanel.appendMessage(message);
                        }
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }).start();

            out.println(username);
        } catch (IOException e) {
            e.printStackTrace();
        }

        setLocationRelativeTo(null);
        setVisible(true);
    }
}
