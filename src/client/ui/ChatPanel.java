package client.ui;

import javax.swing.*;
import javax.swing.text.StyleConstants;
import javax.swing.text.SimpleAttributeSet;
import javax.swing.text.StyledDocument;
import java.awt.*;
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

        JScrollPane chatScrollPane = new JScrollPane(chatArea);
        chatScrollPane.setBorder(BorderFactory.createEmptyBorder());
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
                    sendFile(dout, selectedFile);
                } catch (IOException ex) {
                    JOptionPane.showMessageDialog(this,
                            "파일 전송 중 오류가 발생했습니다: " + ex.getMessage(),
                            "오류",
                            JOptionPane.ERROR_MESSAGE);
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

    private void sendMessage(DataOutputStream dout) throws IOException {
        // 사용자 입력 메시지 가져오기
        String message = chatInput.getText().trim();

        if (!message.isEmpty()) {
            // 현재 날짜와 시간을 "yyyy-MM-dd HH:mm:ss" 형식으로 가져오기
            LocalDateTime now = LocalDateTime.now();
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
            String dateTime = now.format(formatter);

            // 날짜, 시간과 메시지를 합쳐서 전송
            String formattedMessage = "/message " + dateTime + " " + message;
            dout.writeUTF(formattedMessage);

            // 입력 필드 초기화
            chatInput.setText("");
        }
    }



    private void sendImage(DataOutputStream dout){

    }

    private void sendFile(DataOutputStream dout, File file) throws IOException {
        if (file == null || !file.exists()) {
            JOptionPane.showMessageDialog(this, "유효하지 않은 파일입니다.", "오류", JOptionPane.ERROR_MESSAGE);
            return;
        }

        // 파일 이름과 크기를 전송
        String message = "/file " + file.getName() + " " + file.length();
        dout.writeUTF(message);

        // 실제 파일 내용을 전송
        try (FileInputStream fis = new FileInputStream(file)) {
            byte[] buffer = new byte[4096];
            int bytesRead;
            while ((bytesRead = fis.read(buffer)) != -1) {
                dout.write(buffer, 0, bytesRead);
            }
            dout.flush();
        }

        JOptionPane.showMessageDialog(this, "파일 전송 완료: " + file.getName(), "알림", JOptionPane.INFORMATION_MESSAGE);
    }


    public void loadChat(String channelName) { //File을 직접 받아 출력?
        chatArea.setText(""); //어쩔수없이 지우고 로드;;
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
        } catch (IOException e) {
            appendMessage("[" + channelName + "] 채팅 기록 로드 중 오류 발생: " + e.getMessage());
        }
    }
}
