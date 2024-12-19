package client.ui;

import client.FileClient;
import models.User;
import server.InfoManager;  // InfoManager를 임포트

import javax.swing.*;
import java.awt.*;
import java.io.*;
import java.net.Socket;

public class MainFrame extends JFrame {
    private User user; // 유저

    public MainFrame(String username, DataInputStream din, DataOutputStream dout, InfoManager infoManager) throws IOException {
        setTitle("Chat - " + username);
        setSize(1000, 800);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());

        // RightPanel 인스턴스 생성, InfoManager를 전달
        RightPanel rightPanel = new RightPanel(infoManager);  // InfoManager 전달
        add(rightPanel, BorderLayout.EAST);

        // ChannelPanel 생성 시 RightPanel 전달
        ChannelPanel channelPanel = new ChannelPanel(dout, rightPanel);
        add(channelPanel, BorderLayout.WEST);

        ChatPanel chatPanel = new ChatPanel(dout);
        add(chatPanel, BorderLayout.CENTER);

        // 내부 클래스 Thread 실행
        new MessageReaderThread(din, chatPanel, rightPanel).start();  // RightPanel도 전달
        dout.writeUTF(username);

        setLocationRelativeTo(null);
        setVisible(true);
    }

    // 내부 클래스 구현
    private static class MessageReaderThread extends Thread {
        private DataInputStream din;
        private final ChatPanel chatPanel;
        private final RightPanel rightPanel;  // RightPanel을 멤버로 추가

        public MessageReaderThread(DataInputStream din, ChatPanel chatPanel, RightPanel rightPanel) {
            this.din = din;
            this.chatPanel = chatPanel;
            this.rightPanel = rightPanel;
        }

        @Override // 처리할 메시지 채널변경, chat load
        public void run() {
            try {
                String message;
                while ((message = din.readUTF()) != null) {
                    if (message.startsWith("/join ")) {
                        String newChannel = message.substring(6).trim();
                        chatPanel.loadChat(newChannel);
                        System.out.println("chat load");
                        rightPanel.updateInfoPanel(newChannel.equals("channel1"));  // 채널 변경에 따라 RightPanel 업데이트
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
