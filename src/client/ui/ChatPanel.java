package client.ui;

import javax.swing.*;
import javax.swing.text.StyleConstants;
import javax.swing.text.SimpleAttributeSet;
import javax.swing.text.StyledDocument;
import java.awt.*;
import java.io.PrintWriter;
import java.util.HashMap;
import java.util.Map;

public class ChatPanel extends JPanel {
    private JTextPane chatArea;
    private JTextField chatInput;
    private Map<String, String> emojiMap;

    public ChatPanel(PrintWriter out) {
        setLayout(new BorderLayout());
        setBackground(new Color(47, 49, 54));

        // 이모티콘 맵 초기화
        emojiMap = new HashMap<>();
        emojiMap.put(":emoji1:", "resources/emoji/emoticon.png");
        emojiMap.put(":emoji2:", "resources/emoji/emoticon2.png");

        // 채팅 영역 설정
        chatArea = new JTextPane();
        chatArea.setEditable(false);
        chatArea.setBackground(new Color(47, 49, 54));
        chatArea.setForeground(new Color(220, 221, 222));
        chatArea.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        JScrollPane chatScrollPane = new JScrollPane(chatArea);
        chatScrollPane.setBorder(BorderFactory.createEmptyBorder());
        add(chatScrollPane, BorderLayout.CENTER);

        // 채팅 입력 영역 설정
        JPanel inputPanel = new JPanel(new BorderLayout());
        inputPanel.setBackground(new Color(47, 49, 54));

        chatInput = new JTextField();
        chatInput.setBackground(new Color(64, 68, 75));
        chatInput.setForeground(new Color(220, 221, 222));
        chatInput.setCaretColor(new Color(220, 221, 222));
        inputPanel.add(chatInput, BorderLayout.CENTER);

        // 이모티콘 버튼
        JButton emojiButton = new JButton("😊");
        emojiButton.setBackground(new Color(47, 49, 54));
        emojiButton.setForeground(Color.WHITE);
        emojiButton.setFocusPainted(false);
        emojiButton.addActionListener(e -> showEmojiDialog()); // 버튼 클릭 시 이모티콘 창 표시

        // 메시지 전송 버튼
        JButton sendButton = new JButton("Send");
        sendButton.setBackground(new Color(88, 101, 242));
        sendButton.setForeground(Color.WHITE);
        sendButton.setFocusPainted(false);
        sendButton.addActionListener(e -> sendMessage(out));

        JPanel buttonPanel = new JPanel(new BorderLayout());
        buttonPanel.setBackground(new Color(47, 49, 54));
        buttonPanel.add(emojiButton, BorderLayout.WEST);
        buttonPanel.add(sendButton, BorderLayout.EAST);

        inputPanel.add(buttonPanel, BorderLayout.EAST);
        add(inputPanel, BorderLayout.SOUTH);

        // 채팅 메시지 전송 이벤트
        chatInput.addActionListener(e -> sendMessage(out));
    }

    // 이모티콘 선택을 위한 다이얼로그 표시
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

    // 이모티콘을 채팅 입력란에 추가하는 메서드
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

                        // 줄바꿈 추가
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
            out.println(message); // 서버로 전송
            chatInput.setText("");
        }
    }

}