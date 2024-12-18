package client.ui;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

import models.User;
import server.UserManager;

public class LoginFrame extends JFrame {
    private JTextField usernameField, idField;
    private JPasswordField passwordField;

    public LoginFrame() throws IOException {
//        Socket socket = new Socket("localhost", 12345);
//        BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
//        PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
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
        addLogo();

        // 중앙 패널
        addCenterPanel(backgroundColor, textColor, buttonColor, signUpButtonColor);

        // 화면 가운데에 띄우기
        setLocationRelativeTo(null);
        setVisible(true);
    }

    // 이미지 추가 메서드
    private void addLogo() {
        ImageIcon logoIcon = new ImageIcon("resources/images/Hiscord.png");
        Image logoImage = logoIcon.getImage();
        Image scaledImage = logoImage.getScaledInstance(500, 400, Image.SCALE_SMOOTH); // 이미지 크기 조정
        ImageIcon scaledIcon = new ImageIcon(scaledImage);

        JLabel logoLabel = new JLabel(scaledIcon);
        logoLabel.setHorizontalAlignment(SwingConstants.CENTER);
        add(logoLabel, BorderLayout.NORTH);
    }

    // 중앙 패널 추가 메서드
    private void addCenterPanel(Color backgroundColor, Color textColor, Color buttonColor, Color signUpButtonColor) {
        JPanel centerPanel = new JPanel();
        centerPanel.setLayout(new GridBagLayout());
        centerPanel.setBackground(backgroundColor);
        add(centerPanel, BorderLayout.CENTER);

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(15, 20, 15, 20);  // 여백 조정
        gbc.anchor = GridBagConstraints.CENTER;

        addUsernameField(centerPanel, gbc, textColor);
        addIdField(centerPanel, gbc, textColor);
        addPasswordField(centerPanel, gbc, textColor);
        addLoginButton(centerPanel, gbc, buttonColor);
        addSignUpButton(centerPanel, gbc, signUpButtonColor);
    }

    // 이름 입력 필드 추가 메서드
    private void addUsernameField(JPanel centerPanel, GridBagConstraints gbc, Color textColor) {
        JLabel usernameLabel = new JLabel("이름: ");
        usernameLabel.setForeground(textColor);
        usernameLabel.setFont(new Font("맑은 고딕", Font.BOLD, 22));
        gbc.gridx = 0;
        gbc.gridy = 0;
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
        usernameField.setPreferredSize(new Dimension(300, 40));
        centerPanel.add(usernameField, gbc);
    }

    // 아이디 입력 필드 추가 메서드
    private void addIdField(JPanel centerPanel, GridBagConstraints gbc, Color textColor) {
        JLabel idLabel = new JLabel("아이디: ");
        idLabel.setForeground(textColor);
        idLabel.setFont(new Font("맑은 고딕", Font.BOLD, 22));
        gbc.gridx = 0;
        gbc.gridy = 1;
        centerPanel.add(idLabel, gbc);

        idField = new JTextField();
        idField.setBackground(new Color(64, 68, 75));
        idField.setForeground(textColor);
        idField.setCaretColor(textColor);
        idField.setFont(new Font("맑은 고딕", Font.PLAIN, 18));
        idField.setBorder(BorderFactory.createEmptyBorder(10, 15, 10, 15));
        gbc.gridx = 1;
        gbc.gridy = 1;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        idField.setPreferredSize(new Dimension(300, 40));
        centerPanel.add(idField, gbc);
    }

    // 비밀번호 입력 필드 추가 메서드
    private void addPasswordField(JPanel centerPanel, GridBagConstraints gbc, Color textColor) {
        JLabel passwordLabel = new JLabel("비밀번호: ");
        passwordLabel.setForeground(textColor);
        passwordLabel.setFont(new Font("맑은 고딕", Font.BOLD, 22));
        gbc.gridx = 0;
        gbc.gridy = 2;
        centerPanel.add(passwordLabel, gbc);

        passwordField = new JPasswordField();
        passwordField.setBackground(new Color(64, 68, 75));
        passwordField.setForeground(textColor);
        passwordField.setCaretColor(textColor);
        passwordField.setFont(new Font("맑은 고딕", Font.PLAIN, 18));
        passwordField.setBorder(BorderFactory.createEmptyBorder(10, 15, 10, 15));
        gbc.gridx = 1;
        gbc.gridy = 2;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        passwordField.setPreferredSize(new Dimension(300, 40));
        centerPanel.add(passwordField, gbc);
    }

    // 로그인 버튼 추가 메서드
    private void addLoginButton(JPanel centerPanel, GridBagConstraints gbc, Color buttonColor) {
        JButton loginButton = new JButton("로그인");
        loginButton.setBackground(buttonColor);
        loginButton.setForeground(Color.WHITE);
        loginButton.setFont(new Font("맑은 고딕", Font.BOLD, 26));
        loginButton.setFocusPainted(false);
        loginButton.setBorder(BorderFactory.createEmptyBorder());
        loginButton.setPreferredSize(new Dimension(250, 50));
        gbc.gridx = 0;
        gbc.gridy = 3;
        gbc.gridwidth = 2;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        centerPanel.add(loginButton, gbc);

        loginButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                handleLogin();
            }
        });
    }

    // 회원 가입 버튼 추가 메서드
    private void addSignUpButton(JPanel centerPanel, GridBagConstraints gbc, Color signUpButtonColor) {
        JButton signUpButton = new JButton("회원 가입");
        signUpButton.setBackground(signUpButtonColor);
        signUpButton.setForeground(Color.WHITE);
        signUpButton.setFont(new Font("맑은 고딕", Font.BOLD, 26));
        signUpButton.setFocusPainted(false);
        signUpButton.setBorder(BorderFactory.createEmptyBorder());
        signUpButton.setPreferredSize(new Dimension(250, 50));
        gbc.gridx = 0;
        gbc.gridy = 4;
        gbc.gridwidth = 2;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        centerPanel.add(signUpButton, gbc);

        signUpButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                new SignUpFrame();
                dispose();
            }
        });
    }

    // 로그인 처리 메서드
    private void handleLogin() {
        String username = usernameField.getText();
        String id = idField.getText();
        String password = new String(passwordField.getPassword());
        User user = UserManager.authenticateUser(id,password);

        if (!username.isEmpty() && !id.isEmpty() && !password.isEmpty()) {
            if (user!=null) {
                new MainFrame(user);
                dispose();
            } else {
                JOptionPane.showMessageDialog(LoginFrame.this, "아이디나 비밀번호가 잘못되었습니다.", "Error", JOptionPane.ERROR_MESSAGE);
            }
        } else {
            JOptionPane.showMessageDialog(LoginFrame.this, "모든 필드를 입력하세요!", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
}
