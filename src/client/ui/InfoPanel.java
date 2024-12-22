package client.ui;

import models.Info;
import server.InfoManager;

import javax.swing.*;
import java.awt.*;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import javax.swing.*;
import java.awt.*;

public class InfoPanel extends JPanel {

    private JTextArea infoTextArea;
    private InfoManager infoManager;


    public InfoPanel() {
        setLayout(new BorderLayout());
        setBackground(new Color(47, 49, 54));
        setPreferredSize(new Dimension(200, 50));

        this.infoManager = new InfoManager(); // InfoManager 초기화

        JLabel infoLabel = new JLabel("정보 패널");
        infoLabel.setForeground(new Color(220, 221, 222));
        infoLabel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        add(infoLabel, BorderLayout.NORTH);

        infoTextArea = new JTextArea();
        infoTextArea.setBackground(new Color(47, 49, 54));
        infoTextArea.setForeground(new Color(220, 221, 222));
        infoTextArea.setEditable(false);
        infoTextArea.setLineWrap(true);
        infoTextArea.setWrapStyleWord(true);
        infoTextArea.setText(""); // 초기에는 아무 내용도 표시하지 않음

        JScrollPane infoScrollPane = createTransparentScrollPane(infoTextArea);
        infoScrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        add(infoScrollPane, BorderLayout.CENTER);

        JPanel bottomPanel = new JPanel(new BorderLayout());
        bottomPanel.setBackground(new Color(47, 49, 54));

        JButton updateButton = new JButton("공지 수정");
        updateButton.setBackground(new Color(88, 101, 242));
        updateButton.setForeground(Color.WHITE);
        updateButton.setFocusPainted(false);
        updateButton.addActionListener(e -> openNoticeEditDialog());

        bottomPanel.add(updateButton, BorderLayout.EAST);

        add(bottomPanel, BorderLayout.SOUTH);
    }

    public void updateInfo(String channelName) {
        Info info = infoManager.getInfoByChannelName(channelName);
        if (info != null) {
            infoTextArea.setText(loadInfoFromFile(info.getFilePath()));
        } else {
            infoTextArea.setText("유효하지 않은 채널입니다.");
        }
    }

    private void openNoticeEditDialog() {
        String channelName = "channel1"; // 예시로 채널 1을 사용, 실제로는 사용자나 설정에 따라 결정됨
        Info info = infoManager.getInfoByChannelName(channelName);
        if (info != null) {
            String currentNotice = loadInfoFromFile(info.getFilePath());
            showEditDialog(channelName, currentNotice);
        }
    }

    private void showEditDialog(String channelName, String currentNotice) {
        JDialog editDialog = new JDialog((Frame) null, "공지 수정", true);
        editDialog.setLayout(new BorderLayout());
        editDialog.setSize(400, 300);
        editDialog.setLocationRelativeTo(null);

        JTextArea textArea = new JTextArea(currentNotice);
        textArea.setBackground(new Color(47, 49, 54));
        textArea.setForeground(Color.WHITE);
        textArea.setLineWrap(true);
        textArea.setWrapStyleWord(true);

        textArea.setCaretColor(Color.WHITE);

        JScrollPane scrollPane = new JScrollPane(textArea);
        editDialog.add(scrollPane, BorderLayout.CENTER);

        JPanel buttonPanel = new JPanel();
        JButton saveButton = new JButton("저장");
        saveButton.addActionListener(e -> {
            String updatedNotice = textArea.getText();
            infoManager.updateNotice(channelName, updatedNotice); // 공지 수정
            updateInfo(channelName); // 패널 갱신
            editDialog.dispose(); // 창 닫기
        });
        buttonPanel.add(saveButton);

        JButton cancelButton = new JButton("취소");
        cancelButton.addActionListener(e -> editDialog.dispose()); // 취소 시 창 닫기
        buttonPanel.add(cancelButton);

        editDialog.add(buttonPanel, BorderLayout.SOUTH);
        editDialog.setVisible(true);
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
        scrollPane.setOpaque(false);
        scrollPane.getViewport().setOpaque(false);
        scrollPane.setBorder(BorderFactory.createEmptyBorder());
        return scrollPane;
    }
}
