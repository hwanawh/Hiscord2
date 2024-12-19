package client.ui;

import client.FileClient;
import models.User;

import javax.swing.*;
import java.awt.*;
import java.io.*;
import java.net.Socket;

public class MainFrame extends JFrame {
    private User user; // 유저

    public MainFrame(String username,DataInputStream din,DataOutputStream dout) throws IOException {
        setTitle("Chat - " + username);
        setSize(1000, 800);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());


        // RightPanel 인스턴스 생성
        RightPanel rightPanel = new RightPanel();
        add(rightPanel, BorderLayout.EAST);

        // ChannelPanel 생성 시 RightPanel 전달
        ChannelPanel channelPanel = new ChannelPanel(dout, rightPanel);
        add(channelPanel, BorderLayout.WEST);

        ChatPanel chatPanel = new ChatPanel(dout);
        add(chatPanel, BorderLayout.CENTER);

        // 내부 클래스 Thread 실행
        new MessageReaderThread(din, chatPanel).start();
        //String projectDir = System.getProperty("user.dir");
        //String path = projectDir + "/resources/user.txt";
        //FileClient.uploadFile(path);
        // 사용자 이름 전송
        dout.writeUTF(username);

        setLocationRelativeTo(null);
        setVisible(true);
    }

    // 내부 클래스 구현
    private static class MessageReaderThread extends Thread {
        private DataInputStream din;
        private final ChatPanel chatPanel;

        public MessageReaderThread(DataInputStream din, ChatPanel chatPanel) {
            this.din = din;
            this.chatPanel = chatPanel;
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
