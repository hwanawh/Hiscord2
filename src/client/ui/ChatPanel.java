package client.ui;

import client.FileClient;

import javax.imageio.ImageIO;
import javax.swing.*;
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

    public void cleanup() { // chatArea zero
        chatArea.setText("");
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
                    sendMessage(dout, selectedFile);
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
            JPanel messagePanel = new JPanel();
            messagePanel.setLayout(new BorderLayout());
            messagePanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
            messagePanel.setPreferredSize(new Dimension(400, 150));
            messagePanel.setMaximumSize(new Dimension(400, 150));
            messagePanel.setBackground(new Color(64, 68, 75));


            JPanel headerPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));
            headerPanel.setBackground(new Color(64, 68, 75));

            ImageIcon profileIcon = new ImageIcon(profileUrl);
            Image scaledProfileImage = profileIcon.getImage().getScaledInstance(40, 40, Image.SCALE_SMOOTH);
            JLabel profileLabel = new JLabel(new ImageIcon(scaledProfileImage));
            headerPanel.add(profileLabel);

            JLabel senderLabel = new JLabel(senderName);
            senderLabel.setForeground(Color.WHITE);
            senderLabel.setFont(new Font("Malgun Gothic", Font.BOLD, 14));
            headerPanel.add(senderLabel);

            messagePanel.add(headerPanel, BorderLayout.NORTH);

            // 타임스탬프
            JLabel timestampLabel = new JLabel(timestamp);
            timestampLabel.setForeground(Color.LIGHT_GRAY);
            timestampLabel.setFont(new Font("Malgun Gothic", Font.ITALIC, 12));
            messagePanel.add(timestampLabel, BorderLayout.SOUTH);

            // 텍스트 메시지 추가
            if (greeting != null && !greeting.isEmpty()) {
                JLabel messageLabel = new JLabel(greeting);
                messageLabel.setForeground(Color.WHITE);
                messageLabel.setFont(new Font("Malgun Gothic", Font.PLAIN, 14));
                messagePanel.add(messageLabel, BorderLayout.CENTER);
            }

            // 이미지 파일 추가
            if (imageFile != null && imageFile.exists()) {
                try {
                    BufferedImage image = ImageIO.read(imageFile);
                    if (image != null) {
                        JLabel imageLabel = new JLabel(new ImageIcon(image.getScaledInstance(200, 150, Image.SCALE_SMOOTH)));
                        imageLabel.setBorder(BorderFactory.createEmptyBorder(10, 0, 0, 0));
                        messagePanel.add(imageLabel, BorderLayout.CENTER);
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

            // 채팅창에 추가
            JScrollPane scrollPane = (JScrollPane) this.getComponent(0);
            JPanel chatContainer = (JPanel) scrollPane.getViewport().getView();
            chatContainer.setLayout(new BoxLayout(chatContainer, BoxLayout.Y_AXIS));

            messagePanel.setAlignmentX(Component.LEFT_ALIGNMENT);
            chatContainer.add(messagePanel);
            chatContainer.add(Box.createVerticalStrut(10));


            chatContainer.revalidate();
            chatContainer.repaint();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }



    private void sendMessage(DataOutputStream dout) throws IOException {
        String message = chatInput.getText().trim();
        if (!message.isEmpty()) {
            LocalDateTime now = LocalDateTime.now();
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
            String dateTime = now.format(formatter);

            // 이모티콘이 포함된 메시지 처리
            for (Map.Entry<String, String> entry : emojiMap.entrySet()) {
                if (message.contains(entry.getKey())) {
                    // 이모티콘 코드가 있을 경우, 해당 이미지로 변환
                    File emojiFile = new File(entry.getValue()); // 경로에 해당하는 이미지 파일을 로드
                    if (emojiFile.exists()) {
                        // 이미지 파일을 보내는 방식으로 수정
                        sendMessageWithImage(dout, emojiFile, message, dateTime);
                        return; // 이모티콘 메시지는 이미지로 전송되었으므로 더 이상 텍스트로 보내지 않음
                    }
                }
            }

            // 텍스트 메시지로 전송
            String formattedMessage = "/message " + dateTime + "," + message;
            dout.writeUTF(formattedMessage);
            chatInput.setText("");
        }
    }

    private void sendMessageWithImage(DataOutputStream dout, File imageFile, String message, String dateTime) throws IOException {
        // 이모티콘을 파일로 보내는 방식
        if (imageFile != null && imageFile.exists()) {
            String formattedMessage = "/message " + dateTime + "," + message;
            FileClient.uploadFile(formattedMessage, imageFile); // 이미지 파일을 전송
            chatInput.setText("");
        }
    }


    private void sendMessage(DataOutputStream dout, File selectedFile) throws IOException {
        String message = chatInput.getText().trim();
        if (!message.isEmpty() || selectedFile != null) {
            LocalDateTime now = LocalDateTime.now();
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
            String dateTime = now.format(formatter);

            if (selectedFile != null) {
                String formattedMessage = "/message " + dateTime + "," + message;
                FileClient.uploadFile(formattedMessage, selectedFile);
            } else {
                String formattedMessage = "/message " + dateTime + "," + message;
                dout.writeUTF(formattedMessage);
            }
            chatInput.setText("");
        }
    }
}
