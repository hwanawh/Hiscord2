package client.ui;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.*;

public class SignUpFrame extends JFrame {
    private JTextField nameField, useridField;
    private JPasswordField passwordField;

    public SignUpFrame() {
        setTitle("Sign Up");
        setSize(500, 400); // 창 크기 확대
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLayout(null);

        // 다크 테마 색상
        Color backgroundColor = new Color(54, 57, 63);
        Color textColor = new Color(220, 221, 222);
        Color buttonColor = new Color(88, 101, 242);

        // 전체 배경 색상
        getContentPane().setBackground(backgroundColor);

        // 제목 라벨
        JLabel titleLabel = new JLabel("회원 가입");
        titleLabel.setBounds(50, 30, 400, 40); // 크기와 위치 조정
        titleLabel.setHorizontalAlignment(SwingConstants.CENTER);
        titleLabel.setForeground(textColor);
        titleLabel.setFont(new Font("맑은 고딕", Font.BOLD, 24)); // 폰트 크기 키움
        add(titleLabel);

        // 이름 라벨
        JLabel nameLabel = new JLabel("이름:");
        nameLabel.setBounds(50, 100, 100, 30);
        nameLabel.setForeground(textColor);
        nameLabel.setFont(new Font("맑은 고딕", Font.BOLD, 16));
        add(nameLabel);

        // 이름 입력 필드
        nameField = new JTextField();
        nameField.setBounds(150, 100, 250, 40); // 크기 확장
        nameField.setBackground(new Color(64, 68, 75));
        nameField.setForeground(textColor);
        nameField.setCaretColor(textColor);
        nameField.setFont(new Font("맑은 고딕", Font.PLAIN, 16)); // 폰트 크기 키움
        nameField.setBorder(BorderFactory.createEmptyBorder(5, 10, 5, 10)); // 여백 조정
        add(nameField);

        // 아이디 라벨
        JLabel useridLabel = new JLabel("아이디:");
        useridLabel.setBounds(50, 160, 100, 30);
        useridLabel.setForeground(textColor);
        useridLabel.setFont(new Font("맑은 고딕", Font.BOLD, 16));
        add(useridLabel);

        // 아이디 입력 필드
        useridField = new JTextField();
        useridField.setBounds(150, 160, 250, 40); // 크기 확장
        useridField.setBackground(new Color(64, 68, 75));
        useridField.setForeground(textColor);
        useridField.setCaretColor(textColor);
        useridField.setFont(new Font("맑은 고딕", Font.PLAIN, 16)); // 폰트 크기 키움
        useridField.setBorder(BorderFactory.createEmptyBorder(5, 10, 5, 10)); // 여백 조정
        add(useridField);

        // 비밀번호 라벨
        JLabel passwordLabel = new JLabel("비밀번호:");
        passwordLabel.setBounds(50, 220, 100, 30);
        passwordLabel.setForeground(textColor);
        passwordLabel.setFont(new Font("맑은 고딕", Font.BOLD, 16));
        add(passwordLabel);

        // 비밀번호 입력 필드
        passwordField = new JPasswordField();
        passwordField.setBounds(150, 220, 250, 40); // 크기 확장
        passwordField.setBackground(new Color(64, 68, 75));
        passwordField.setForeground(textColor);
        passwordField.setCaretColor(textColor);
        passwordField.setFont(new Font("맑은 고딕", Font.PLAIN, 16)); // 폰트 크기 키움
        passwordField.setBorder(BorderFactory.createEmptyBorder(5, 10, 5, 10)); // 여백 조정
        add(passwordField);

        // 회원가입 버튼
        JButton signUpButton = new JButton("회원 가입");
        signUpButton.setBounds(150, 280, 250, 50); // 버튼 크기 키움
        signUpButton.setBackground(buttonColor);
        signUpButton.setForeground(Color.WHITE);
        signUpButton.setFont(new Font("맑은 고딕", Font.BOLD, 18)); // 폰트 크기 키움
        signUpButton.setFocusPainted(false);
        signUpButton.setBorder(BorderFactory.createEmptyBorder());
        signUpButton.setCursor(new Cursor(Cursor.HAND_CURSOR)); // 클릭 시 손 모양 커서
        add(signUpButton);

        // 회원가입 버튼 이벤트 리스너
        signUpButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String name = nameField.getText();
                String userid = useridField.getText();
                String password = new String(passwordField.getPassword());

                // 입력값 확인
                if (name.isEmpty() || userid.isEmpty() || password.isEmpty()) {
                    JOptionPane.showMessageDialog(SignUpFrame.this, "모든 항목을 채워주세요.", "Error", JOptionPane.ERROR_MESSAGE);
                } else if (isUserIdDuplicated(userid)) { // 아이디 중복 확인
                    JOptionPane.showMessageDialog(SignUpFrame.this, "이미 사용 중인 아이디입니다.", "Error", JOptionPane.ERROR_MESSAGE);
                } else {
                    // 사용자 정보 저장
                    saveUserInfo(name, userid, password);
                    JOptionPane.showMessageDialog(SignUpFrame.this, "회원 가입 성공!", "Success", JOptionPane.INFORMATION_MESSAGE);
                    dispose(); // 회원가입 후 창 닫기
                    new LoginFrame(); // 로그인 프레임 열기
                }
            }
        });

        setLocationRelativeTo(null); // 창을 화면 중앙에 배치

        setVisible(true);
    }

    // 사용자 정보를 Hiscord/resources/user.txt 파일에 저장하는 메서드
    private void saveUserInfo(String name, String userid, String password) {
        try {
            // 현재 디렉토리 출력
            String currentDir = System.getProperty("user.dir");
            String path = currentDir + File.separator + "resources" + File.separator + "user.txt";

            File dir = new File("Hiscord/resources");

            // 디렉토리가 없으면 생성
            if (!dir.exists()) {
                dir.mkdirs();
                System.out.println("디렉토리 생성됨: " + dir.getAbsolutePath());
            }

            // 파일을 덧붙이기 모드로 열기
            BufferedWriter writer = new BufferedWriter(new FileWriter(path, true));
            writer.write(name + "," + userid + "," + password);
            writer.newLine();  // 새 줄 추가
            writer.close();

            System.out.println("파일에 저장됨: " + path);
        } catch (IOException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "사용자 정보를 저장하는 중 오류가 발생했습니다!", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }


    private boolean isUserIdDuplicated(String userid) {
        try {
            // 현재 디렉토리 출력
            String currentDir = System.getProperty("user.dir");
            String path = currentDir + File.separator + "resources" + File.separator + "user.txt";

            File file = new File(path);

            // 파일이 존재하지 않으면 중복 없음
            if (!file.exists()) {
                return false;
            }

            BufferedReader reader = new BufferedReader(new FileReader(file));
            String line;

            while ((line = reader.readLine()) != null) {
                String[] parts = line.split(",");
                if (parts.length >= 2 && parts[1].equals(userid)) {
                    reader.close();
                    return true; // 중복된 아이디 발견
                }
            }

            reader.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return false; // 중복된 아이디 없음
    }
}
