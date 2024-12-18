package client.ui;

import models.User;

import javax.swing.*;
import java.awt.*;
import java.io.*;
import java.net.Socket;

public class MainFrame extends JFrame {
    private User user; // 유저

    public MainFrame(String username,BufferedReader in,PrintWriter out) {
        setTitle("Chat - " + username);
        setSize(1000, 800);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());

        // RightPanel 인스턴스 생성
        RightPanel rightPanel = new RightPanel();
        add(rightPanel, BorderLayout.EAST);

        // ChannelPanel 생성 시 RightPanel 전달
        ChannelPanel channelPanel = new ChannelPanel(out, rightPanel);
        add(channelPanel, BorderLayout.WEST);

        ChatPanel chatPanel = new ChatPanel(out);
        add(chatPanel, BorderLayout.CENTER);

        // 내부 클래스 Thread 실행
        new MessageReaderThread(in, chatPanel).start();

        // 사용자 이름 전송
        out.println(username);

        setLocationRelativeTo(null);
        setVisible(true);
    }

    // 내부 클래스 구현
    private static class MessageReaderThread extends Thread {
        private final BufferedReader in;
        private final ChatPanel chatPanel;

        public MessageReaderThread(BufferedReader in, ChatPanel chatPanel) {
            this.in = in;
            this.chatPanel = chatPanel;
        }

        @Override // 처리할 메시지 채널변경, chat load
        public void run() {
            try {
                String message;
                while ((message = in.readLine()) != null) {
                    if (message.startsWith("/join ")) {
                        String newChannel = message.substring(6).trim();
                        chatPanel.loadChat(newChannel);
                        System.out.println("chat load");
                    } else {
                        chatPanel.appendMessage(message);
                        System.out.println("message");
                    }
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
