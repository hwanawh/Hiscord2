package client.ui;

import models.User;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.DataOutputStream;
import java.io.IOException;

public class MyPage extends JDialog {  // JDialog로 변경
    private User loggedUser;
    private DataOutputStream dout; // DataOutputStream 필드

    public MyPage(User loggedUser, DataOutputStream dout, JFrame parentFrame) {  // 부모 JFrame 전달
        super(parentFrame, "MyPage - " + loggedUser.getName(), true);  // 모달 대화상자로 설정
        this.loggedUser = loggedUser;
        this.dout = dout; // 필드 초기화
        setSize(400, 300);
        setLayout(new GridLayout(6, 2, 10, 10));  // 여백을 추가하여 더 깔끔한 레이아웃

        // 배경색 설정
        getContentPane().setBackground(new Color(47, 49, 54));  // 어두운 회색 배경
        setTitle("MyPage - " + loggedUser.getName());

        // 사용자 정보 입력 필드 (아이디, 이름, 비밀번호)
        JLabel idLabel = new JLabel("아이디:");
        idLabel.setForeground(Color.WHITE);  // 텍스트 색상 변경
        add(idLabel);

        JTextField idField = new JTextField(loggedUser.getId());
        idField.setEditable(false);  // 아이디는 수정 불가
        idField.setBackground(new Color(230, 230, 230));  // 밝은 회색 배경
        idField.setForeground(Color.BLACK);  // 텍스트 색상
        add(idField);

        JLabel nameLabel = new JLabel("이름:");
        nameLabel.setForeground(Color.WHITE);  // 텍스트 색상 변경
        add(nameLabel);

        JTextField nameField = new JTextField(loggedUser.getName());
        nameField.setBackground(new Color(230, 230, 230));  // 밝은 회색 배경
        nameField.setForeground(Color.BLACK);  // 텍스트 색상
        add(nameField);

        JLabel passwordLabel = new JLabel("비밀번호:");
        passwordLabel.setForeground(Color.WHITE);  // 텍스트 색상 변경
        add(passwordLabel);

        JPasswordField passwordField = new JPasswordField(loggedUser.getPassword());
        passwordField.setBackground(new Color(230, 230, 230));  // 밝은 회색 배경
        passwordField.setForeground(Color.BLACK);  // 텍스트 색상
        add(passwordField);

        // 닫기 버튼
        JButton closeButton = new JButton("닫기");
        closeButton.setBackground(new Color(50, 50, 50));  // 어두운 회색 버튼
        closeButton.setForeground(Color.WHITE);
        closeButton.setFocusPainted(false);
        closeButton.setBorderPainted(false);
        closeButton.addActionListener(e -> dispose());  // 창 닫기
        add(closeButton);

        // 수정 버튼
        JButton updateButton = new JButton("수정");
        updateButton.setBackground(new Color(72, 103, 204));  // 디스코드 스타일의 파란색 버튼
        updateButton.setForeground(Color.WHITE);  // 버튼 텍스트 색상
        updateButton.setFocusPainted(false);  // 포커스 효과 제거
        updateButton.setBorderPainted(false);  // 테두리 제거
        updateButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String newName = nameField.getText();
                String newPassword = new String(passwordField.getPassword());

                // 서버에 수정된 정보 전송
                updateUserInfo(newName, newPassword);

                // UI 갱신 후 종료
                JOptionPane.showMessageDialog(MyPage.this, "정보가 수정되었습니다.");
                dispose();  // MyPage 창 닫기 (챗창은 유지)
            }
        });
        add(updateButton);

        setLocationRelativeTo(null);  // 화면 중앙에 위치하도록 설정
        setVisible(true);
    }

    private void updateUserInfo(String newName, String newPassword) {
        try {
            dout.writeUTF("/updateUserInfo " + newName + "/" + newPassword);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
