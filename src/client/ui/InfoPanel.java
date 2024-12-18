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

    // InfoManager를 외부에서 전달받는 생성자
    public InfoPanel(InfoManager infoManager) {
        setLayout(new BorderLayout());
        setBackground(new Color(47, 49, 54));
        setPreferredSize(new Dimension(200, 50));

        this.infoManager = infoManager; // 외부에서 전달받은 InfoManager를 사용

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
