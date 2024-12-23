package client.ui;

import javax.swing.*;
import java.awt.*;
import java.io.*;

public class InfoPanel extends JPanel {

    private JTextArea infoTextArea;
    private DataInputStream din;
    private DataOutputStream dout;
    private String currentChannel;

    public InfoPanel(DataOutputStream dout) {
        this.dout=dout;
        setLayout(new BorderLayout());
        setBackground(new Color(47, 49, 54));
        setPreferredSize(new Dimension(200, 50));

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

    public void setCurrentChannel(String currentChannel){
        this.currentChannel=currentChannel;
    }

    public void loadInfo(String content) {
        if (content == null || content.trim().isEmpty()) {
            infoTextArea.setText("현재 표시할 공지가 없습니다.");
            return;
        }

        // 공지 내용을 꾸며서 표시
        StringBuilder formattedContent = new StringBuilder();

        formattedContent.append("==== 공지사항 ====\n\n"); // 제목 추가
        formattedContent.append(content.trim());          // 내용 추가
        formattedContent.append("\n\n==================");

        infoTextArea.setText(formattedContent.toString()); // 텍스트 영역에 설정

        // 스크롤바 초기화
        infoTextArea.setCaretPosition(0);

        System.out.println("공지가 로드되었습니다:\n" + formattedContent);
    }

    private void openNoticeEditDialog() {
        String currentNotice = infoTextArea.getText()
                .replace("==== 공지사항 ====\n\n", "")
                .replace("\n\n==================", "")
                .trim();

        showEditDialog(currentChannel, currentNotice);
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
        buttonPanel.setBackground(new Color(47, 49, 54));

        JButton saveButton = new JButton("저장");
        saveButton.setBackground(new Color(88, 101, 242));
        saveButton.setForeground(Color.WHITE);
        saveButton.setFocusPainted(false);
        saveButton.addActionListener(e -> {
            String updatedNotice = textArea.getText().trim();

            if (dout != null) {
                try {
                    dout.writeUTF("/info " + channelName + " "+updatedNotice);
                    editDialog.dispose();
                } catch (IOException ex) {
                    JOptionPane.showMessageDialog(this, "서버로 전송 중 오류가 발생했습니다.", "오류", JOptionPane.ERROR_MESSAGE);
                    ex.printStackTrace();
                }
            } else {
                JOptionPane.showMessageDialog(this, "서버 연결이 없습니다.", "오류", JOptionPane.ERROR_MESSAGE);
            }
        });

        JButton cancelButton = new JButton("취소");
        cancelButton.setBackground(new Color(153, 153, 153));
        cancelButton.setForeground(Color.WHITE);
        cancelButton.setFocusPainted(false);
        cancelButton.addActionListener(e -> editDialog.dispose()); // 취소 시 다이얼로그 닫기

        buttonPanel.add(saveButton);
        buttonPanel.add(cancelButton);

        editDialog.add(buttonPanel, BorderLayout.SOUTH);
        editDialog.setVisible(true);
    }

    private JScrollPane createTransparentScrollPane(JTextArea textArea) {
        JScrollPane scrollPane = new JScrollPane(textArea);
        scrollPane.setOpaque(false);
        scrollPane.getViewport().setOpaque(false);
        scrollPane.setBorder(BorderFactory.createEmptyBorder());
        return scrollPane;
    }
}
