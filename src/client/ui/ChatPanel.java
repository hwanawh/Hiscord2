// ChatPanel.java
package client.ui;

import javax.swing.*;
import java.awt.*;
import java.io.PrintWriter;

public class ChatPanel extends JPanel {
    private JTextArea chatArea;
    private JTextField chatInput;

    public ChatPanel(PrintWriter out) {
        setLayout(new BorderLayout());
        setBackground(new Color(47, 49, 54));

        // 채팅 영역 설정
        chatArea = new JTextArea();
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

        JButton sendButton = new JButton("Send");
        sendButton.setBackground(new Color(88, 101, 242));
        sendButton.setForeground(Color.WHITE);
        sendButton.setFocusPainted(false);
        inputPanel.add(sendButton, BorderLayout.EAST);

        add(inputPanel, BorderLayout.SOUTH);

        // 채팅 메시지 전송 이벤트
        chatInput.addActionListener(e -> sendMessage(out));
        sendButton.addActionListener(e -> sendMessage(out));
    }

    public void appendMessage(String message) {
        chatArea.append(message + "\n");
    }

    private void sendMessage(PrintWriter out) {
        String message = chatInput.getText().trim();
        if (!message.isEmpty()) {
            out.println(message);
            chatInput.setText("");
        }
    }
}