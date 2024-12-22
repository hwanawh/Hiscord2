package client.ui;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.*;
import java.net.Socket;

import server.InfoManager;

public class LoginFrame extends JFrame {
    private JTextField idField;
    private JPasswordField passwordField;
    private DataInputStream din;
    private DataOutputStream dout;

    public LoginFrame(Socket socket,DataInputStream din,DataOutputStream dout) throws IOException {
        this.din=din;
        this.dout=dout;
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


        addIdField(centerPanel, gbc, textColor);
        addPasswordField(centerPanel, gbc, textColor);
        addLoginButton(centerPanel, gbc, buttonColor);
        addSignUpButton(centerPanel, gbc, signUpButtonColor);
    }

    // 아이디 입력 필드 추가 메서드
    private void addIdField(JPanel centerPanel, GridBagConstraints gbc, Color textColor) {
        JLabel idLabel = new JLabel("아이디: ");
        idLabel.setForeground(textColor);
        idLabel.setFont(new Font("맑은 고딕", Font.BOLD, 22));
        gbc.gridx = 0;
        gbc.gridy = 0;
        centerPanel.add(idLabel, gbc);

        idField = new JTextField();
        idField.setBackground(new Color(64, 68, 75));
        idField.setForeground(textColor);
        idField.setCaretColor(textColor);
        idField.setFont(new Font("맑은 고딕", Font.PLAIN, 18));
        idField.setBorder(BorderFactory.createEmptyBorder(10, 15, 10, 15));
        gbc.gridx = 1;
        gbc.gridy = 0;
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
        gbc.gridy = 1;
        centerPanel.add(passwordLabel, gbc);

        passwordField = new JPasswordField();
        passwordField.setBackground(new Color(64, 68, 75));
        passwordField.setForeground(textColor);
        passwordField.setCaretColor(textColor);
        passwordField.setFont(new Font("맑은 고딕", Font.PLAIN, 18));
        passwordField.setBorder(BorderFactory.createEmptyBorder(10, 15, 10, 15));
        gbc.gridx = 1;
        gbc.gridy = 1;
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
        gbc.gridy = 2;
        gbc.gridwidth = 2;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        centerPanel.add(loginButton, gbc);

        loginButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                try {
                    handleLogin();
                } catch (IOException ex) {
                    throw new RuntimeException(ex);
                }
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
        gbc.gridy = 3;
        gbc.gridwidth = 2;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        centerPanel.add(signUpButton, gbc);

        signUpButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                new SignUpFrame(din,dout);
                dispose();
            }
        });
    }

    // 로그인 처리 메서드
    private void handleLogin() throws IOException {
        String id = idField.getText();
        String password = new String(passwordField.getPassword());

        if (!id.isEmpty() && !password.isEmpty()) {
            try {
                // 로그인 요청 메시지 서버로 전송
                dout.writeUTF("/login " + id + "/" + password);

                // 서버 응답을 기다리는 스레드 생성
                Thread responseThread = new Thread(() -> {
                    try {
                        // 서버의 응답을 기다림
                        String authentication = din.readUTF();
                        System.out.println("auth: " + authentication); // 응답 확인 로그

                        // 응답 처리 (Event Dispatch Thread에서 실행)
                        SwingUtilities.invokeLater(() -> {
                            if (authentication != null && !authentication.startsWith("Login Failed")) {
                                try {
                                    new MainFrame(id, din, dout); // InfoManager 제거
                                } catch (IOException e) {
                                    e.printStackTrace();
                                    JOptionPane.showMessageDialog(
                                            LoginFrame.this,
                                            "메인 화면을 여는 중 오류가 발생했습니다.",
                                            "Error",
                                            JOptionPane.ERROR_MESSAGE
                                    );
                                }
                                dispose();
                            } else {
                                JOptionPane.showMessageDialog(
                                        LoginFrame.this,
                                        "아이디 또는 비밀번호가 잘못되었습니다.",
                                        "로그인 실패",
                                        JOptionPane.ERROR_MESSAGE
                                );
                            }
                        });
                    } catch (IOException e) {
                        e.printStackTrace();
                        SwingUtilities.invokeLater(() -> {
                            JOptionPane.showMessageDialog(
                                    LoginFrame.this,
                                    "서버와의 연결에 문제가 발생했습니다. 다시 시도해주세요.",
                                    "Error",
                                    JOptionPane.ERROR_MESSAGE
                            );
                        });
                    }
                });

                responseThread.start();
            } catch (IOException ex) {
                ex.printStackTrace();
                JOptionPane.showMessageDialog(
                        this,
                        "서버로 요청을 보내는 중 문제가 발생했습니다. 다시 시도해주세요.",
                        "Error",
                        JOptionPane.ERROR_MESSAGE
                );
            }
        } else {
            JOptionPane.showMessageDialog(
                    this,
                    "모든 필드를 입력하세요!",
                    "Error",
                    JOptionPane.ERROR_MESSAGE
            );
        }
    }


}
