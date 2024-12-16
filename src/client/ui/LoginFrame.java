package client.ui;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import models.User;

public class LoginFrame extends JFrame {
    private JTextField usernameField, idField;
    private JPasswordField passwordField;

    public LoginFrame() {
        setTitle("Hiscord");
        setSize(1200, 1000); // 창 크기 조정 
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());

        Color backgroundColor = new Color(54, 57, 63);
        Color textColor = new Color(220, 221, 222);
        Color buttonColor = new Color(88, 101, 242);
        Color signUpButtonColor = new Color(60, 179, 113);

        getContentPane().setBackground(backgroundColor);

        // 이미지 추가
        ImageIcon logoIcon = new ImageIcon("resources/images/Hiscord.png");
        Image logoImage = logoIcon.getImage();
        Image scaledImage = logoImage.getScaledInstance(500, 400, Image.SCALE_SMOOTH); // 이미지 크기 조정
        ImageIcon scaledIcon = new ImageIcon(scaledImage);

        JLabel logoLabel = new JLabel(scaledIcon);
        logoLabel.setHorizontalAlignment(SwingConstants.CENTER);
        add(logoLabel, BorderLayout.NORTH);

        // 전체 패널을 담을 JPanel 생성
        JPanel centerPanel = new JPanel();
        centerPanel.setLayout(new GridBagLayout());
        centerPanel.setBackground(backgroundColor);
        add(centerPanel, BorderLayout.CENTER);

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(15, 20, 15, 20);  // 여백 조정
        gbc.anchor = GridBagConstraints.CENTER;

        // 이름 입력 필드
        JLabel usernameLabel = new JLabel("이름: ");
        usernameLabel.setForeground(textColor);
        usernameLabel.setFont(new Font("맑은 고딕", Font.BOLD, 22));
        gbc.gridwidth = 1;
        gbc.gridx = 0;
        gbc.gridy = 0; // 위치 조정 (위로 올림)
        centerPanel.add(usernameLabel, gbc);

        usernameField = new JTextField();
        usernameField.setBackground(new Color(64, 68, 75));
        usernameField.setForeground(textColor);
        usernameField.setCaretColor(textColor);
        usernameField.setFont(new Font("맑은 고딕", Font.PLAIN, 18));
        usernameField.setBorder(BorderFactory.createEmptyBorder(10, 15, 10, 15));
        gbc.gridx = 1;
        gbc.gridy = 0; 
        gbc.fill = GridBagConstraints.HORIZONTAL;
        usernameField.setPreferredSize(new Dimension(300, 40)); // 입력창 크기 조정
        centerPanel.add(usernameField, gbc);

        // 아이디 입력 필드
        JLabel idLabel = new JLabel("아이디: ");
        idLabel.setForeground(textColor);
        idLabel.setFont(new Font("맑은 고딕", Font.BOLD, 22));
        gbc.gridx = 0;
        gbc.gridy = 1; // 위치 조정 (위로 올림)
        centerPanel.add(idLabel, gbc);

        idField = new JTextField();
        idField.setBackground(new Color(64, 68, 75));
        idField.setForeground(textColor);
        idField.setCaretColor(textColor);
        idField.setFont(new Font("맑은 고딕", Font.PLAIN, 18));
        idField.setBorder(BorderFactory.createEmptyBorder(10, 15, 10, 15));
        gbc.gridx = 1;
        gbc.gridy = 1; // 위치 조정 (위로 올림)
        gbc.fill = GridBagConstraints.HORIZONTAL;
        idField.setPreferredSize(new Dimension(300, 40)); // 입력창 크기 조정
        centerPanel.add(idField, gbc);

        // 비밀번호 입력 필드
        JLabel passwordLabel = new JLabel("비밀번호: ");
        passwordLabel.setForeground(textColor);
        passwordLabel.setFont(new Font("맑은 고딕", Font.BOLD, 22));
        gbc.gridx = 0;
        gbc.gridy = 2; // 위치 조정 (위로 올림)
        centerPanel.add(passwordLabel, gbc);

        passwordField = new JPasswordField();
        passwordField.setBackground(new Color(64, 68, 75));
        passwordField.setForeground(textColor);
        passwordField.setCaretColor(textColor);
        passwordField.setFont(new Font("맑은 고딕", Font.PLAIN, 18));
        passwordField.setBorder(BorderFactory.createEmptyBorder(10, 15, 10, 15));
        gbc.gridx = 1;
        gbc.gridy = 2; // 위치 조정 (위로 올림)
        gbc.fill = GridBagConstraints.HORIZONTAL;
        passwordField.setPreferredSize(new Dimension(300, 40)); // 입력창 크기 조정
        centerPanel.add(passwordField, gbc);

        // 로그인 버튼
        JButton loginButton = new JButton("로그인");
        loginButton.setBackground(buttonColor);
        loginButton.setForeground(Color.WHITE);
        loginButton.setFont(new Font("맑은 고딕", Font.BOLD, 26));
        loginButton.setFocusPainted(false);
        loginButton.setBorder(BorderFactory.createEmptyBorder());
        loginButton.setPreferredSize(new Dimension(250, 50)); // 버튼 크기 조정
        gbc.gridx = 0;
        gbc.gridy = 3; 
        gbc.gridwidth = 2;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        centerPanel.add(loginButton, gbc);

        // 회원 가입 버튼
        JButton signUpButton = new JButton("회원 가입");
        signUpButton.setBackground(signUpButtonColor);
        signUpButton.setForeground(Color.WHITE);
        signUpButton.setFont(new Font("맑은 고딕", Font.BOLD, 26));
        signUpButton.setFocusPainted(false);
        signUpButton.setBorder(BorderFactory.createEmptyBorder());
        signUpButton.setPreferredSize(new Dimension(250, 50)); // 버튼 크기 조정
        gbc.gridx = 0;
        gbc.gridy = 4; 
        gbc.gridwidth = 2;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        centerPanel.add(signUpButton, gbc);

        // 로그인 버튼 클릭 이벤트 처리
        loginButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String username = usernameField.getText();
                String id = idField.getText();
                String password = new String(passwordField.getPassword());

                if (!username.isEmpty() && !id.isEmpty() && !password.isEmpty()) {
                    if (User.isValidUser(id, password)) {
                        new MainFrame(username);
                        dispose();
                    } else {
                        JOptionPane.showMessageDialog(LoginFrame.this, "아이디나 비밀번호가 잘못되었습니다.", "Error", JOptionPane.ERROR_MESSAGE);
                    }
                } else {
                    JOptionPane.showMessageDialog(LoginFrame.this, "모든 필드를 입력하세요!", "Error", JOptionPane.ERROR_MESSAGE);
                }
            }
        });

        // 회원 가입 버튼 클릭 이벤트 처리
        signUpButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                new SignUpFrame();
                dispose();
            }
        });

        // 화면 가운데에 띄우기
        setLocationRelativeTo(null);
        setVisible(true);
    }
}
