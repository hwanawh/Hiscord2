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

    public void appendMessage(String message) {
        String profileImagePath="C:\\demo\\Hiscord2\\client_resources\\Hiscord.png";
        String author="name";
        String timeline="11";
        try {
            // 메시지 패널 생성 (JPanel 사용)
            JPanel messagePanel = new JPanel();
            messagePanel.setLayout(new BorderLayout());
            messagePanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

            // 프로필 사진 추가
            ImageIcon profileIcon = new ImageIcon(profileImagePath);
            Image scaledProfileImage = profileIcon.getImage().getScaledInstance(40, 40, Image.SCALE_SMOOTH); // 크기를 40x40으로 조정
            JLabel profileLabel = new JLabel(new ImageIcon(scaledProfileImage));
            profileLabel.setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 10));
            messagePanel.add(profileLabel, BorderLayout.WEST);

            // 작성자, 타임라인, 메시지 내용을 포함할 패널
            JPanel contentPanel = new JPanel();
            contentPanel.setLayout(new BoxLayout(contentPanel, BoxLayout.Y_AXIS));

            // 작성자와 타임라인 추가
            JLabel authorLabel = new JLabel(author);
            JLabel timelineLabel = new JLabel(timeline);
            authorLabel.setFont(new Font("Arial", Font.BOLD, 14));
            timelineLabel.setFont(new Font("Arial", Font.ITALIC, 12));
            contentPanel.add(authorLabel);
            contentPanel.add(timelineLabel);

            // 메시지 내용 처리 (텍스트와 이모지 포함)
            JPanel messageContentPanel = new JPanel();
            messageContentPanel.setLayout(new FlowLayout(FlowLayout.LEFT));

            while (!message.isEmpty()) {
                boolean emojiFound = false;

                // 이모지를 처리
                for (Map.Entry<String, String> entry : emojiMap.entrySet()) {
                    if (message.contains(entry.getKey())) {
                        String[] parts = message.split(entry.getKey(), 2);

                        // 텍스트가 있으면 추가
                        if (!parts[0].isEmpty()) {
                            JLabel textLabel = new JLabel(parts[0]);
                            messageContentPanel.add(textLabel);
                        }

                        // 이모지 추가
                        ImageIcon emojiIcon = new ImageIcon(entry.getValue());
                        JLabel emojiLabel = new JLabel(emojiIcon);
                        messageContentPanel.add(emojiLabel);

                        // 메시지 업데이트
                        message = parts.length > 1 ? parts[1] : "";
                        emojiFound = true;
                        break;
                    }
                }

                // 이모지가 없으면 나머지 메시지 추가
                if (!emojiFound) {
                    JLabel textLabel = new JLabel(message);
                    messageContentPanel.add(textLabel);
                    message = ""; // 메시지 처리 완료
                }
            }

            contentPanel.add(messageContentPanel);

            // 메시지 패널에 추가
            messagePanel.add(contentPanel, BorderLayout.CENTER);

            // chatArea에 추가
            chatArea.setCaretPosition(chatArea.getDocument().getLength());
            chatArea.insertComponent(messagePanel);

            // 화면 갱신
            chatArea.revalidate();
            chatArea.repaint();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void appendImage(DataInputStream din) {
        StyledDocument doc = chatArea.getStyledDocument();
        SimpleAttributeSet textAttrs = new SimpleAttributeSet();

        try {
            // 이미지 크기 수신
            long imageSize = din.readLong(); // 이미지 크기 읽기
            byte[] imageBytes = new byte[(int) imageSize];
            din.readFully(imageBytes); // 이미지 데이터를 모두 읽기

            // 바이트 데이터를 BufferedImage로 변환
            BufferedImage image = ImageIO.read(new ByteArrayInputStream(imageBytes));
            if (image != null) {
                // 이미지가 유효하면 chatArea에 추가
                chatArea.setCaretPosition(doc.getLength());
                chatArea.insertIcon(new ImageIcon(image));
                System.out.println("이미지 추가 완료");
            } else {
                System.err.println("유효하지 않은 이미지 데이터.");
            }
        } catch (IOException e) {
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
                formattedMessage = "/message " + dateTime;
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
