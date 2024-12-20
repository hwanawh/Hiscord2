package client.ui;

import client.FileClient;
import models.User;
import models.UserManager;
import server.InfoManager;  // InfoManager를 임포트

import javax.swing.*;
import java.awt.*;
import java.io.*;
import java.net.Socket;

public class MainFrame extends JFrame {
    private User loggedUser; // 유저
    private String username;
    private String localImageUrl;
    private DataOutputStream dout;

    public MainFrame(String id, DataInputStream din, DataOutputStream dout, InfoManager infoManager) throws IOException {
        this.dout = dout;
        loggedUser = UserManager.getUserById(id);
        setTitle("Chat - " + loggedUser.getName());
        setSize(1000, 800);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());

        // TopPanel을 사용하여 버튼 추가
        TopPanel topPanel = new TopPanel(loggedUser, dout, this);  // TopPanel 생성
        add(topPanel, BorderLayout.NORTH);  // 상단에 배치

        // RightPanel 생성
        RightPanel rightPanel = new RightPanel(infoManager);
        add(rightPanel, BorderLayout.EAST);

        // ChannelPanel 생성
        ChannelPanel channelPanel = new ChannelPanel(dout, rightPanel);
        add(channelPanel, BorderLayout.WEST);

        // ChatPanel 생성
        ChatPanel chatPanel = new ChatPanel(dout);
        add(chatPanel, BorderLayout.CENTER);

        // MessageReaderThread 실행
        new MessageReaderThread(din, chatPanel, rightPanel).start();
        dout.writeUTF(loggedUser.getName());

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
                    } else if (message.startsWith("/message ")) {
                        // 메세지 처리
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
