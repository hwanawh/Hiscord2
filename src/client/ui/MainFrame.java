package client.ui;

import client.FileClient;
import models.User;
import models.UserManager;


import javax.swing.*;
import java.awt.*;
import java.io.*;
import java.net.Socket;

public class MainFrame extends JFrame {
    private User loggedUser; // 유저
    private String username;
    private String localImageUrl;
    private DataOutputStream dout;


    public MainFrame(String id, DataInputStream din, DataOutputStream dout) throws IOException {
        this.dout = dout;
        loggedUser = UserManager.getUserById(id);
        setTitle("Chat - " + loggedUser.getName());
        setSize(1000, 800);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());
        dout.writeUTF("/memberLoad ");
        // TopPanel을 사용하여 버튼 추가
        TopPanel topPanel = new TopPanel(loggedUser, dout, this);  // TopPanel 생성
        add(topPanel, BorderLayout.NORTH);  // 상단에 배치

        // RightPanel 생성
        RightPanel rightPanel = new RightPanel();
        add(rightPanel, BorderLayout.EAST);

        // ChannelPanel 생성
        ChannelPanel channelPanel = new ChannelPanel(dout, rightPanel);
        add(channelPanel, BorderLayout.WEST);

        // ChatPanel 생성
        ChatPanel chatPanel = new ChatPanel(dout);
        add(chatPanel, BorderLayout.CENTER);

        // MessageReaderThread 실행
        new MessageReaderThread(din, dout,chatPanel, rightPanel).start();
        dout.writeUTF(loggedUser.getName());

        setLocationRelativeTo(null);
        setVisible(true);
    }

    // 내부 클래스 구현
    private static class MessageReaderThread extends Thread {
        private DataInputStream din;
        private DataOutputStream dout;
        private final ChatPanel chatPanel;
        private final RightPanel rightPanel;  // RightPanel을 멤버로 추가
        private File testImage = new File("C:\\demo\\Hiscord2\\client_resources\\default.png");

        public MessageReaderThread(DataInputStream din, DataOutputStream dout,ChatPanel chatPanel, RightPanel rightPanel) {
            this.dout =dout;
            this.din = din;
            this.chatPanel = chatPanel;
            this.rightPanel = rightPanel;
        }

        @Override // 처리할 메시지: 채널 변경, chat load
        public void run() {
            try {
                String message;
                while ((message = din.readUTF()) != null) {
                    String command = message.contains(" ") ? message.substring(0, message.indexOf(" ")) : message;

                    switch (command) {
                        case "/join":
                            String newChannel = message.substring(6).trim();
                            dout.writeUTF("/chatload "+newChannel);
                            chatPanel.cleanup();
                            //chatPanel.loadChat(newChannel);
                            System.out.println("chat load");
                            rightPanel.updateInfoPanel(newChannel.equals("channel1")); // 채널 변경에 따라 RightPanel 업데이트
                            break;

                        case "/message":
                            // 메시지 처리
                            String chatMessage = message.substring(9).trim();
                            String[] parts = chatMessage.split(",");

                            // 변수 할당
                            String profileUrl = System.getProperty("user.dir")+parts[0];
                            String senderName = parts[1];
                            String timestamp = parts[2];
                            String greeting = parts[3];
                            String filename = parts.length > 4 ? parts[4] : null;
                            System.out.println("profileUrl: " + profileUrl);
                            System.out.println("senderName: " + senderName);
                            System.out.println("timestamp: " + timestamp);
                            System.out.println("greeting: " + greeting);
                            System.out.println("filename: " + filename);
                            if (filename != null && !filename.equals("null")) {
                                // 이미지 파일 수신
                                try {
                                    // 이미지 크기 받기
                                    long fileSize = din.readLong();
                                    System.out.println("M1)파일 크기: " + fileSize + " bytes");

                                    // 파일을 ByteArray로 저장
                                    byte[] imageBytes = new byte[(int) fileSize];
                                    din.readFully(imageBytes);

                                    // 임시 디렉토리에 파일 저장
                                    File tempFile = new File(System.getProperty("user.dir")+"/client_resources" + filename); // 파일 이름에 접두어 추가
                                    try (FileOutputStream fileOutputStream = new FileOutputStream(tempFile)) {
                                        fileOutputStream.write(imageBytes);
                                        System.out.println("M2)파일 저장 완료: " + tempFile.getAbsolutePath());

                                        // File 객체를 appendMessage에 전달
                                        chatPanel.appendMessage(profileUrl, senderName, timestamp, greeting, tempFile);
                                    } catch (IOException e) {
                                        System.err.println("파일 저장 실패: " + e.getMessage());
                                        e.printStackTrace(); // 예외 추적
                                    }
                                } catch (IOException e) {
                                    System.err.println("파일 수신 실패: " + e.getMessage());
                                    e.printStackTrace(); // 예외 추적
                                }
                            }
                            else{
                                chatPanel.appendMessage(profileUrl,senderName,timestamp,greeting,null);
                            }

                            System.out.println(chatMessage);
                            System.out.println("message");
                            break;

                        case "/image":
                            //chatPanel.appendImage(din);
                            break;

                        case "/memberLoad":
                            String member = message.substring(9).trim();
                            String[] part = member.split(",");
                            profileUrl = System.getProperty("user.dir")+part[0];
                            String name = part[1];
                            rightPanel.loadMemberPanel(profileUrl,name);
                            break;
                        default:
                            // 기본 메시지 처리
                            //chatPanel.appendMessage(message);
                            System.out.println("message"+message);
                            System.out.println("default message");
                            break;
                    }
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
