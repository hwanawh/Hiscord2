package client.ui;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.*;

public class SignUpFrame extends JFrame {
    private JTextField nameField, useridField, profileUrlField;
    private JPasswordField passwordField;
    private BufferedReader in;
    private PrintWriter out;

    public SignUpFrame(BufferedReader in, PrintWriter out) {
        this.in = in;
        this.out = out;
        setTitle("Sign Up");
        setSize(500, 500); // Increased size for the additional field
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLayout(null);

        configureTheme();
        initializeComponents();
        setLocationRelativeTo(null); // Center the window
        setVisible(true);
    }

    private void configureTheme() {
        Color backgroundColor = new Color(54, 57, 63);
        getContentPane().setBackground(backgroundColor);
    }

    private void initializeComponents() {
        // Title label
        JLabel titleLabel = new JLabel("회원 가입");
        titleLabel.setBounds(50, 30, 400, 40);
        titleLabel.setHorizontalAlignment(SwingConstants.CENTER);
        titleLabel.setForeground(new Color(220, 221, 222));
        titleLabel.setFont(new Font("맑은 고딕", Font.BOLD, 24));

        // Name field and label
        nameField = createTextField();
        nameField.setBounds(150, 100, 250, 40);

        JLabel nameLabel = createLabel("이름:", 50, 100);

        // User ID field and label
        useridField = createTextField();
        useridField.setBounds(150, 160, 250, 40);

        JLabel useridLabel = createLabel("아이디:", 50, 160);

        // Password field and label
        passwordField = new JPasswordField();
        passwordField.setBounds(150, 220, 250, 40);
        styleTextField(passwordField);

        JLabel passwordLabel = createLabel("비밀번호:", 50, 220);

        // Profile URL field and label
        profileUrlField = createTextField();
        profileUrlField.setBounds(150, 280, 170, 40);
        profileUrlField.setEditable(false); // Make field read-only

        JLabel profileUrlLabel = createLabel("프로필 사진:", 50, 280);

        JButton selectProfileButton = new JButton("파일 선택");
        selectProfileButton.setBounds(330, 280, 100, 40);
        styleButton(selectProfileButton);

        selectProfileButton.addActionListener(e -> chooseProfilePicture());

        // Sign-up button
        JButton signUpButton = new JButton("회원 가입");
        signUpButton.setBounds(150, 340, 250, 50);
        styleButton(signUpButton);

        signUpButton.addActionListener(createSignUpActionListener());

        // Add components to the frame
        add(titleLabel);
        add(nameLabel);
        add(nameField);
        add(useridLabel);
        add(useridField);
        add(passwordLabel);
        add(passwordField);
        add(profileUrlLabel);
        add(profileUrlField);
        add(selectProfileButton);
        add(signUpButton);
    }

    private JTextField createTextField() {
        JTextField textField = new JTextField();
        styleTextField(textField);
        return textField;
    }

    private JLabel createLabel(String text, int x, int y) {
        JLabel label = new JLabel(text);
        label.setBounds(x, y, 100, 30);
        label.setForeground(new Color(220, 221, 222));
        label.setFont(new Font("맑은 고딕", Font.BOLD, 16));
        return label;
    }

    private void styleTextField(JTextField textField) {
        textField.setBackground(new Color(64, 68, 75));
        textField.setForeground(new Color(220, 221, 222));
        textField.setCaretColor(new Color(220, 221, 222));
        textField.setFont(new Font("맑은 고딕", Font.PLAIN, 16));
        textField.setBorder(BorderFactory.createEmptyBorder(5, 10, 5, 10));
    }

    private void styleButton(JButton button) {
        button.setBackground(new Color(88, 101, 242));
        button.setForeground(Color.WHITE);
        button.setFont(new Font("맑은 고딕", Font.BOLD, 18));
        button.setFocusPainted(false);
        button.setBorder(BorderFactory.createEmptyBorder());
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
    }

    private void chooseProfilePicture() {
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
        int result = fileChooser.showOpenDialog(this);
        if (result == JFileChooser.APPROVE_OPTION) {
            File selectedFile = fileChooser.getSelectedFile();
            profileUrlField.setText(selectedFile.getAbsolutePath());
        }
    }

    private ActionListener createSignUpActionListener() {
        return e -> {
            String name = nameField.getText();
            String userid = useridField.getText();
            String password = new String(passwordField.getPassword());
            String profileUrl = profileUrlField.getText();

            if (name.isEmpty() || userid.isEmpty() || password.isEmpty() || profileUrl.isEmpty()) {
                JOptionPane.showMessageDialog(this, "모든 항목을 채워주세요.", "Error", JOptionPane.ERROR_MESSAGE);
            } else if (isUserIdDuplicated(userid)) {
                JOptionPane.showMessageDialog(this, "이미 사용 중인 아이디입니다.", "Error", JOptionPane.ERROR_MESSAGE);
            } else {
                saveUserInfo(name, userid, password, profileUrl);
                JOptionPane.showMessageDialog(this, "회원 가입 성공!", "Success", JOptionPane.INFORMATION_MESSAGE);
                dispose();
                new MainFrame(name,in,out); //바로 메인 프레임 진입
            }
        };
    }

    private void saveUserInfo(String name, String userid, String password, String profileUrl) {
        // Implement logic to save user information and profile URL
    }

    private boolean isUserIdDuplicated(String userid) {
        // Implement logic to check if the user ID is duplicated
        return false;
    }
}
