package client.ui;

import models.User;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.*;
import java.nio.file.*;

public class MyPage extends JDialog {
    private User loggedUser;
    private DataOutputStream dout;

    public MyPage(User loggedUser, DataOutputStream dout, JFrame parentFrame) {
        super(parentFrame, "MyPage - " + loggedUser.getName(), true);  // 모달 대화상자
        this.loggedUser = loggedUser;
        this.dout = dout;

        setSize(400, 500);
        setLayout(new GridBagLayout());  // GridBagLayout으로 변경
        GridBagConstraints gbc = new GridBagConstraints();  // 레이아웃 제어를 위한 GridBagConstraints 객체 생성

        // 배경색 설정
        getContentPane().setBackground(new Color(47, 49, 54));

        // 프로필 라벨
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.anchor = GridBagConstraints.WEST;
        JLabel profileLabel = new JLabel("프로필:");
        profileLabel.setForeground(Color.WHITE);
        add(profileLabel, gbc);

        // 프로필 사진을 표시할 JLabel
        gbc.gridx = 1;
        JLabel profileImageLabel = new JLabel();
        add(profileImageLabel, gbc);

        // 프로필 사진 변경 버튼
        gbc.gridx = 1;
        gbc.gridy = 1;
        gbc.fill = GridBagConstraints.HORIZONTAL;  // 버튼을 가로로 확장
        JButton changeProfileButton = new JButton("프로필 사진 변경");
        changeProfileButton.setBackground(new Color(72, 103, 204));
        changeProfileButton.setForeground(Color.WHITE);
        changeProfileButton.setFocusPainted(false);
        changeProfileButton.setBorderPainted(false);
        changeProfileButton.addActionListener(e -> selectAndSaveProfileImage(profileImageLabel));
        add(changeProfileButton, gbc);

        // 사용자 정보 입력 필드 (아이디, 이름, 비밀번호)
        gbc.gridx = 0;
        gbc.gridy = 2;
        gbc.anchor = GridBagConstraints.WEST;
        JLabel idLabel = new JLabel("아이디:");
        idLabel.setForeground(Color.WHITE);
        add(idLabel, gbc);

        gbc.gridx = 1;
        JTextField idField = new JTextField(loggedUser.getId());
        idField.setEditable(false);
        idField.setBackground(new Color(230, 230, 230));
        idField.setForeground(Color.BLACK);
        add(idField, gbc);

        gbc.gridx = 0;
        gbc.gridy = 3;
        JLabel nameLabel = new JLabel("이름:");
        nameLabel.setForeground(Color.WHITE);
        add(nameLabel, gbc);

        gbc.gridx = 1;
        JTextField nameField = new JTextField(loggedUser.getName());
        nameField.setBackground(new Color(230, 230, 230));
        nameField.setForeground(Color.BLACK);
        add(nameField, gbc);

        gbc.gridx = 0;
        gbc.gridy = 4;
        JLabel passwordLabel = new JLabel("비밀번호:");
        passwordLabel.setForeground(Color.WHITE);
        add(passwordLabel, gbc);

        gbc.gridx = 1;
        JPasswordField passwordField = new JPasswordField(loggedUser.getPassword());
        passwordField.setBackground(new Color(230, 230, 230));
        passwordField.setForeground(Color.BLACK);
        add(passwordField, gbc);

        // 닫기 버튼
        gbc.gridx = 0;
        gbc.gridy = 5;
        JButton closeButton = new JButton("닫기");
        closeButton.setBackground(new Color(50, 50, 50));
        closeButton.setForeground(Color.WHITE);
        closeButton.setFocusPainted(false);
        closeButton.setBorderPainted(false);
        closeButton.addActionListener(e -> dispose());
        add(closeButton, gbc);

        // 수정 버튼
        gbc.gridx = 1;
        JButton updateButton = new JButton("수정");
        updateButton.setBackground(new Color(72, 103, 204));
        updateButton.setForeground(Color.WHITE);
        updateButton.setFocusPainted(false);
        updateButton.setBorderPainted(false);
        updateButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String newName = nameField.getText();
                String newPassword = new String(passwordField.getPassword());
                String newProfileUrl = loggedUser.getProfileUrl();  // 프로필 URL은 이미 저장된 파일을 사용

                updateUserInfo(newName, newPassword, newProfileUrl);

                // UI 갱신 후 종료
                JOptionPane.showMessageDialog(MyPage.this, "정보가 수정되었습니다.");
                dispose();
            }
        });
        add(updateButton, gbc);

        setLocationRelativeTo(null);  // 화면 중앙에 위치하도록 설정
        setVisible(true);
    }

    private void selectAndSaveProfileImage(JLabel profileImageLabel) {
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setDialogTitle("프로필 사진 선택");
        fileChooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
        int result = fileChooser.showOpenDialog(this);

        if (result == JFileChooser.APPROVE_OPTION) {
            File selectedFile = fileChooser.getSelectedFile();
            String filePath = selectedFile.getPath();

            File destDir = new File("client_resources");
            if (!destDir.exists()) {
                destDir.mkdirs();  // client_resources 폴더가 없으면 생성
            }

            // Correct path construction: no need to manually add backslashes
            String destPath = "client_resources" + File.separator + selectedFile.getName();

            try {
                Files.copy(selectedFile.toPath(), Paths.get(destPath), StandardCopyOption.REPLACE_EXISTING);
                System.out.println("프로필 사진이 저장되었습니다: " + destPath);

                ImageIcon profileImage = new ImageIcon(destPath);
                profileImageLabel.setIcon(profileImage);

                loggedUser.setProfileUrl(destPath);
            } catch (IOException ex) {
                ex.printStackTrace();
                JOptionPane.showMessageDialog(this, "파일을 복사하는 중 오류가 발생했습니다.");
            }
        }
    }


    private void updateUserInfo(String newName, String newPassword, String newProfileUrl) {
        try {
            dout.writeUTF("/updateUserInfo " + newName + "/" + newPassword + "/" + newProfileUrl);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
