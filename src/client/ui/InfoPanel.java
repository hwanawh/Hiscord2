//info panel
package client.ui;

import models.Info;
import server.InfoManager;

import javax.swing.*;
import java.awt.*;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

public class InfoPanel extends JPanel {
    private JTextArea infoTextArea;
    private InfoManager infoManager;
    private JButton editNoticeButton; // 수정 버튼 추가
    private String currentChannel = "channel1"; // 예시: 현재 채널을 'channel1'로 설정

    public InfoPanel() {
        setLayout(new BorderLayout());
        setBackground(new Color(47, 49, 54));
        setPreferredSize(new Dimension(200, 50));

        infoManager = new InfoManager(); // InfoManager 초기화

        JLabel infoLabel = new JLabel("정보 패널");
        infoLabel.setForeground(new Color(220, 221, 222));
        infoLabel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        add(infoLabel, BorderLayout.NORTH);

        infoTextArea = new JTextArea();
        infoTextArea.setBackground(new Color(47, 49, 54));
        infoTextArea.setForeground(new Color(220, 221, 222));
        infoTextArea.setEditable(false);
        infoTextArea.setLineWrap(true); // 자동 줄 바꿈 설정
        infoTextArea.setWrapStyleWord(true); // 단어가 잘리지 않도록 설정
        infoTextArea.setText(""); // 초기에는 아무 내용도 표시하지 않음

        JScrollPane infoScrollPane = createTransparentScrollPane(infoTextArea);
        infoScrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER); // 가로 스크롤바 비활성화
        add(infoScrollPane, BorderLayout.CENTER);

        // 공지 수정 버튼
        editNoticeButton = new JButton("공지 수정");
        editNoticeButton.setFont(new Font("맑은 고딕", Font.PLAIN, 14));
        editNoticeButton.setForeground(new Color(220, 221, 222));  // 텍스트 색상
        editNoticeButton.setBackground(new Color(48, 49, 54));  // 어두운 배경
        editNoticeButton.setFocusPainted(false);  // 포커스 시 효과 제거
        editNoticeButton.setBorder(BorderFactory.createLineBorder(new Color(67, 70, 75), 2)); // 테두리 추가
        editNoticeButton.setPreferredSize(new Dimension(120, 40));  // 버튼 크기 조정
        editNoticeButton.setCursor(new Cursor(Cursor.HAND_CURSOR)); // 커서 변경

        // 마우스 이벤트로 버튼 색상 변화
        editNoticeButton.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                editNoticeButton.setBackground(new Color(79, 84, 91)); // 마우스 오버 시 색상 변화
            }

            public void mouseExited(java.awt.event.MouseEvent evt) {
                editNoticeButton.setBackground(new Color(48, 49, 54)); // 원래 색상으로 복귀
            }
        });

        editNoticeButton.addActionListener(e -> {
            String currentInfo = infoTextArea.getText();
            // JTextArea로 여러 줄 입력 받을 수 있도록 설정
            JTextArea editArea = new JTextArea(currentInfo, 20, 40); // 20줄, 40자 크기로 텍스트 영역 설정
            JScrollPane scrollPane = new JScrollPane(editArea); // 스크롤을 추가하여 긴 내용도 보이게 설정
            int option = JOptionPane.showConfirmDialog(this, scrollPane, "공지 수정", JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);

            if (option == JOptionPane.OK_OPTION) {
                String newInfo = editArea.getText(); // 수정된 공지 내용
                if (newInfo != null && !newInfo.isEmpty()) {
                    // 수정된 공지를 InfoManager에 전달하여 파일에 반영
                    infoManager.updateNotice(currentChannel, newInfo);
                    updateInfo(currentChannel); // 수정 후 정보 갱신
                }
            }
        });
        add(editNoticeButton, BorderLayout.SOUTH);
    }

    public void updateInfo(String channelName) {
        Info info = infoManager.getInfoByChannelName(channelName);
        if (info != null) {
            infoTextArea.setText(loadInfoFromFile(info.getFilePath()));
        } else {
            infoTextArea.setText("유효하지 않은 채널입니다.");
        }
    }

    private String loadInfoFromFile(String filePath) {
        StringBuilder content = new StringBuilder();

        try (BufferedReader br = new BufferedReader(new FileReader(filePath))) {
            String line;
            while ((line = br.readLine()) != null) {
                content.append(line).append("\n");
            }
        } catch (IOException e) {
            content.append("정보를 불러오는 데 오류가 발생했습니다.");
        }

        return content.toString();
    }

    private JScrollPane createTransparentScrollPane(JTextArea textArea) {
        JScrollPane scrollPane = new JScrollPane(textArea);

        // 스크롤바 설정
        scrollPane.setOpaque(false);
        scrollPane.getViewport().setOpaque(false);
        scrollPane.setBorder(BorderFactory.createEmptyBorder());
        return scrollPane;
    }
}
