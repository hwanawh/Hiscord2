package client.ui;

import client.FileClient;

import javax.imageio.ImageIO;
import javax.swing.*;
import javax.swing.text.StyleConstants;
import javax.swing.text.SimpleAttributeSet;
import javax.swing.text.StyledDocument;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Map;

public class ChatPanel extends JPanel {
    private JTextPane chatArea;
    private JTextField chatInput;
    private Map<String, String> emojiMap;

    public ChatPanel(DataOutputStream dout) {
        setLayout(new BorderLayout());
        setBackground(new Color(47, 49, 54));

        initializeEmojiMap();
        initializeChatArea();
        initializeInputPanel(dout);
    }

    private void initializeEmojiMap() {
        emojiMap = new HashMap<>();
        emojiMap.put(":emoji1:", "resources/emoji/emoticon.png");
        emojiMap.put(":emoji2:", "resources/emoji/emoticon2.png");
    }

    private void initializeChatArea() {
        chatArea = new JTextPane();
        chatArea.setEditable(false);
        chatArea.setBackground(new Color(47, 49, 54));
        chatArea.setForeground(new Color(220, 221, 222));
        chatArea.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        JPanel chatContainer = new JPanel();
        chatContainer.setLayout(new BoxLayout(chatContainer, BoxLayout.Y_AXIS));
        chatContainer.setBackground(new Color(47, 49, 54));

        JScrollPane chatScrollPane = new JScrollPane(chatContainer);
        chatScrollPane.setBorder(BorderFactory.createEmptyBorder());
        chatScrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);

        add(chatScrollPane, BorderLayout.CENTER);

        // 기존 chatArea의 역할을 교체
        this.chatArea = new JTextPane();
        this.chatArea.setEditable(false);
        this.chatArea.setBackground(new Color(47, 49, 54));
        this.chatArea.setBorder(BorderFactory.createEmptyBorder());
    }

    private void initializeInputPanel(DataOutputStream dout) {
        JPanel inputPanel = new JPanel(new BorderLayout());
        inputPanel.setBackground(new Color(47, 49, 54));

        chatInput = new JTextField();
        chatInput.setBackground(new Color(64, 68, 75));
        chatInput.setForeground(new Color(220, 221, 222));
        chatInput.setCaretColor(new Color(220, 221, 222));
        inputPanel.add(chatInput, BorderLayout.CENTER);

        JPanel buttonPanel = createButtonPanel(dout);
        inputPanel.add(buttonPanel, BorderLayout.EAST);

        add(inputPanel, BorderLayout.SOUTH);
    }

    private JPanel createButtonPanel(DataOutputStream dout) {
        JPanel buttonPanel = new JPanel(new GridLayout(1, 3, 5, 0));
        buttonPanel.setBackground(new Color(47, 49, 54));

        JButton emojiButton = createEmojiButton();
        JButton sendButton = createSendButton(dout);
        JButton fileButton = createFileButton(dout);

        buttonPanel.add(emojiButton);
        buttonPanel.add(sendButton);
        buttonPanel.add(fileButton);

        return buttonPanel;
    }

    private JButton createSendButton(DataOutputStream dout) {
        JButton sendButton = new JButton("Send");
        sendButton.setBackground(new Color(88, 101, 242));
        sendButton.setForeground(Color.WHITE);
        sendButton.setFocusPainted(false);
        sendButton.addActionListener(e -> {
            try {
                sendMessage(dout);
            } catch (IOException ex) {
                throw new RuntimeException(ex);
            }
        });
        chatInput.addActionListener(e -> {
            try {
                sendMessage(dout);
            } catch (IOException ex) {
                throw new RuntimeException(ex);
            }
        });
        return sendButton;
    }

    private JButton createEmojiButton() {
        JButton emojiButton = new JButton("😊");
        emojiButton.setBackground(new Color(47, 49, 54));
        emojiButton.setForeground(Color.WHITE);
        emojiButton.setFocusPainted(false);
        emojiButton.addActionListener(e -> showEmojiDialog());
        return emojiButton;
    }

    private JButton createFileButton(DataOutputStream dout) {
        JButton fileButton = new JButton("파일 추가");
        fileButton.setBackground(new Color(47, 49, 54));
        fileButton.setForeground(Color.WHITE);
        fileButton.setFocusPainted(false);
        fileButton.addActionListener(e -> {
            JFileChooser fileChooser = new JFileChooser();
            int returnValue = fileChooser.showOpenDialog(this);
            if (returnValue == JFileChooser.APPROVE_OPTION) {
                File selectedFile = fileChooser.getSelectedFile();
                try {
                    sendMessage(dout,selectedFile);
                    System.out.println(selectedFile.getName());
                } catch (IOException ex) {
                    throw new RuntimeException(ex);
                }
            }
        });
        return fileButton;
    }

    private void showEmojiDialog() {
        JDialog emojiDialog = new JDialog((Frame) null, "이모티콘", true);
        emojiDialog.setLayout(new GridLayout(1, emojiMap.size()));
        emojiDialog.setSize(450, 250);
        emojiDialog.setLocationRelativeTo(this);

        for (Map.Entry<String, String> entry : emojiMap.entrySet()) {
            JButton emojiButton = new JButton(new ImageIcon(entry.getValue()));
            emojiButton.addActionListener(e -> {
                insertEmoji(entry.getKey());
                emojiDialog.dispose();
            });
            emojiDialog.add(emojiButton);
        }
        emojiDialog.setVisible(true);
    }

    private void insertEmoji(String emojiCode) {
        chatInput.setText(chatInput.getText() + " " + emojiCode);
    }

    public void appendMessage(String profileUrl, String senderName, String timestamp, String greeting, File imageFile) {
        try {
            // 메시지 패널 생성
            JPanel messagePanel = new JPanel();
            messagePanel.setLayout(new BorderLayout());
            messagePanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
            messagePanel.setPreferredSize(new Dimension(400, 150)); // 일정한 크기 설정
            messagePanel.setMaximumSize(new Dimension(400, 150));
            messagePanel.setBackground(new Color(64, 68, 75)); // 배경 색상 추가

            // 프로필 사진
            ImageIcon profileIcon = new ImageIcon(profileUrl);
            Image scaledProfileImage = profileIcon.getImage().getScaledInstance(40, 40, Image.SCALE_SMOOTH);
            JLabel profileLabel = new JLabel(new ImageIcon(scaledProfileImage));
            profileLabel.setBorder(BorderFactory.createEmptyBorder(0, 10, 0, 10));
            messagePanel.add(profileLabel, BorderLayout.WEST);

            // 작성자, 타임라인, 메시지
            JPanel contentPanel = new JPanel();
            contentPanel.setLayout(new BoxLayout(contentPanel, BoxLayout.Y_AXIS));
            contentPanel.setBackground(new Color(64, 68, 75));

            JLabel authorLabel = new JLabel(senderName);
            authorLabel.setForeground(Color.WHITE);
            authorLabel.setFont(new Font("Malgun Gothic", Font.BOLD, 14));
            JLabel timelineLabel = new JLabel(timestamp);
            timelineLabel.setForeground(Color.LIGHT_GRAY);
            timelineLabel.setFont(new Font("Malgun Gothic", Font.ITALIC, 12));

            contentPanel.add(authorLabel);
            contentPanel.add(timelineLabel);

            // 텍스트 메시지 추가
            if (greeting != null && !greeting.isEmpty()) {
                JLabel messageLabel = new JLabel(greeting);
                messageLabel.setForeground(Color.WHITE);
                messageLabel.setFont(new Font("Malgun Gothic", Font.PLAIN, 14));
                contentPanel.add(messageLabel);
            }

            // 이미지 파일이 존재하는 경우 처리
            if (imageFile != null && imageFile.exists()) {
                try {
                    // 파일에서 이미지 읽기
                    BufferedImage image = ImageIO.read(imageFile);
                    if (image != null) {
                        JLabel imageLabel = new JLabel(new ImageIcon(image.getScaledInstance(200, 150, Image.SCALE_SMOOTH)));
                        imageLabel.setBorder(BorderFactory.createEmptyBorder(10, 0, 0, 0)); // 이미지와 텍스트 간 간격
                        contentPanel.add(imageLabel);
                    } else {
                        System.err.println("유효하지 않은 이미지 파일.");
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

            messagePanel.add(contentPanel, BorderLayout.CENTER);

            // chatContainer에 추가
            JScrollPane scrollPane = (JScrollPane) this.getComponent(0); // ScrollPane 가져오기
            JPanel chatContainer = (JPanel) scrollPane.getViewport().getView(); // ScrollPane의 View 가져오기
            chatContainer.setLayout(new BoxLayout(chatContainer, BoxLayout.Y_AXIS)); // 메시지 수직 정렬

            messagePanel.setAlignmentX(Component.LEFT_ALIGNMENT); // 왼쪽 정렬 유지
            chatContainer.add(messagePanel);
            chatContainer.add(Box.createVerticalStrut(10)); // 메시지 간 간격 추가

            chatContainer.revalidate();
            chatContainer.repaint();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }



    private void sendMessage(DataOutputStream dout) throws IOException {
        // 사용자 입력 메시지 가져오기
        String message = chatInput.getText().trim();
        String formattedMessage;
        if (!message.isEmpty()) {
            // 현재 날짜와 시간을 "yyyy-MM-dd HH:mm:ss" 형식으로 가져오기
            LocalDateTime now = LocalDateTime.now();
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
            String dateTime = now.format(formatter);
            formattedMessage = "/message " + dateTime + "," + message;

            formattedMessage = "/message " + dateTime + "," + message;
            System.out.println("formated"+formattedMessage);
            dout.writeUTF(formattedMessage);

            // 날짜, 시간과 메시지를 합쳐서 전송
            // 입력 필드 초기화
            chatInput.setText("");
        }
    }

    private void sendMessage(DataOutputStream dout,File selectedFile) throws IOException {
        // 사용자 입력 메시지 가져오기
        String message = chatInput.getText().trim();
        String formattedMessage;
        if (!message.isEmpty()||selectedFile!=null) {
            // 현재 날짜와 시간을 "yyyy-MM-dd HH:mm:ss" 형식으로 가져오기
            LocalDateTime now = LocalDateTime.now();
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
            String dateTime = now.format(formatter);

            if(selectedFile!=null){//파일 전송 코드
                formattedMessage = "/message " + dateTime + "," + message;
                FileClient.uploadFile(formattedMessage,selectedFile);
            } else{
                formattedMessage = "/message " + dateTime + "," + message;
                System.out.println("formated"+formattedMessage);
                dout.writeUTF(formattedMessage);
            }
            // 날짜, 시간과 메시지를 합쳐서 전송
            // 입력 필드 초기화
            chatInput.setText("");
        }
    }




}
