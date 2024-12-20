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
        this.dout = dout; // DataOutputStream을 전달받기
        loggedUser = UserManager.getUserById(id);
        username = loggedUser.getName();
        localImageUrl = System.getProperty("user.dir") + "/client_resources/Hiscord.png";

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

        // MyPage로 가는 버튼을 우측 상단에 배치
        JPanel topPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));  // FlowLayout으로 우측 정렬
        topPanel.setBackground(new Color(47, 49, 54));
        JButton myPageButton = new JButton("설정");
        myPageButton.setPreferredSize(new Dimension(80, 30));  // 버튼 크기 설정
        myPageButton.addActionListener(e -> {
            MyPage myPage = new MyPage(loggedUser, dout, MainFrame.this);  // MainFrame을 부모로 전달
            myPage.setVisible(true);  // JDialog이므로 setVisible을 true로 설정하여 표시
            // dispose()를 호출하지 않음, 창이 닫히면 자동으로 돌아옴
        });

        topPanel.add(myPageButton);
        add(topPanel, BorderLayout.NORTH);  // 상단에 버튼 배치

        // 내부 클래스 Thread 실행
        new MessageReaderThread(din, chatPanel, rightPanel).start();  // RightPanel도 전달
        dout.writeUTF(username);

        String projectDir = System.getProperty("user.dir");
        String path = projectDir + "/resources/user.txt";
        FileClient.uploadFile(path);

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
