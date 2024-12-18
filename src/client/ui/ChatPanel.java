package client.ui;

import javax.swing.*;
import javax.swing.text.StyleConstants;
import javax.swing.text.SimpleAttributeSet;
import javax.swing.text.StyledDocument;
import java.awt.*;
import java.io.*;
import java.util.HashMap;
import java.util.Map;

public class ChatPanel extends JPanel {
    private JTextPane chatArea;
    private JTextField chatInput;
    private Map<String, String> emojiMap;

    public ChatPanel(PrintWriter out) {
        setLayout(new BorderLayout());
        setBackground(new Color(47, 49, 54));

        initializeEmojiMap();
        initializeChatArea();
        initializeInputPanel(out);
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

        JScrollPane chatScrollPane = new JScrollPane(chatArea);
        chatScrollPane.setBorder(BorderFactory.createEmptyBorder());
        add(chatScrollPane, BorderLayout.CENTER);
    }

    private void initializeInputPanel(PrintWriter out) {
        JPanel inputPanel = new JPanel(new BorderLayout());
        inputPanel.setBackground(new Color(47, 49, 54));

        chatInput = new JTextField();
        chatInput.setBackground(new Color(64, 68, 75));
        chatInput.setForeground(new Color(220, 221, 222));
        chatInput.setCaretColor(new Color(220, 221, 222));
        inputPanel.add(chatInput, BorderLayout.CENTER);

        JPanel buttonPanel = createButtonPanel(out);
        inputPanel.add(buttonPanel, BorderLayout.EAST);

        add(inputPanel, BorderLayout.SOUTH);
    }

    private JPanel createButtonPanel(PrintWriter out) {
        JPanel buttonPanel = new JPanel(new BorderLayout());
        buttonPanel.setBackground(new Color(47, 49, 54));

        JButton emojiButton = createEmojiButton();
        JButton sendButton = createSendButton(out);

        buttonPanel.add(emojiButton, BorderLayout.WEST);
        buttonPanel.add(sendButton, BorderLayout.EAST);

        return buttonPanel;
    }

    private JButton createEmojiButton() {
        JButton emojiButton = new JButton("😊");
        emojiButton.setBackground(new Color(47, 49, 54));
        emojiButton.setForeground(Color.WHITE);
        emojiButton.setFocusPainted(false);
        emojiButton.addActionListener(e -> showEmojiDialog());
        return emojiButton;
    }

    private JButton createSendButton(PrintWriter out) {
        JButton sendButton = new JButton("Send");
        sendButton.setBackground(new Color(88, 101, 242));
        sendButton.setForeground(Color.WHITE);
        sendButton.setFocusPainted(false);
        sendButton.addActionListener(e -> sendMessage(out));
        chatInput.addActionListener(e -> sendMessage(out));
        return sendButton;
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

    public void appendMessage(String message) {
        StyledDocument doc = chatArea.getStyledDocument();
        SimpleAttributeSet textAttrs = new SimpleAttributeSet();

        try {
            while (!message.isEmpty()) {
                boolean emojiFound = false;

                for (Map.Entry<String, String> entry : emojiMap.entrySet()) {
                    if (message.contains(entry.getKey())) {
                        String[] parts = message.split(entry.getKey(), 2);
                        if (!parts[0].isEmpty()) {
                            doc.insertString(doc.getLength(), parts[0], textAttrs);
                        }
                        chatArea.setCaretPosition(doc.getLength());
                        chatArea.insertIcon(new ImageIcon(entry.getValue()));

                        doc.insertString(doc.getLength(), "\n", textAttrs);

                        message = parts.length > 1 ? parts[1] : "";
                        emojiFound = true;
                        break;
                    }
                }

                if (!emojiFound) {
                    doc.insertString(doc.getLength(), message + "\n", textAttrs);
                    break;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void sendMessage(PrintWriter out) {
        String message = chatInput.getText().trim();
        if (!message.isEmpty()) {
            out.println(message);
            chatInput.setText("");
        }
    }

    public void loadChat(String channelName) {
        String projectDir = System.getProperty("user.dir");
        String path = projectDir + "/resources/channel/" + channelName + "/chats.txt";
        System.out.println(path);

        try {
            File chatFile = new File(path);

            if (!chatFile.exists()) {
                appendMessage("[" + channelName + "] 채팅 기록 파일을 찾을 수 없습니다.");
                return;
            }

            BufferedReader reader = new BufferedReader(new FileReader(chatFile));
            String line;
            appendMessage("[" + channelName + "] 채팅 기록 로드 시작:");

            while ((line = reader.readLine()) != null) {
                String[] parts = line.split(",");

                if (parts.length >= 4) {
                    String id = parts[0].trim();
                    String date = parts[1].trim();
                    String time = parts[2].trim();
                    String message = parts[3].trim();

                    appendMessage("[" + date + " " + time + "] " + id + ": " + message);
                } else {
                    appendMessage("잘못된 데이터 형식: " + line);
                }
            }

            reader.close();
            appendMessage("[" + channelName + "] 채팅 기록 로드 완료.");
        } catch (IOException e) {
            appendMessage("[" + channelName + "] 채팅 기록 로드 중 오류 발생: " + e.getMessage());
        }
    }
}
