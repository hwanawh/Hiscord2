package client.ui;

import models.User;

import javax.swing.*;
import java.awt.*;
import java.io.DataOutputStream;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class TopPanel extends JPanel {
    public TopPanel(User loggedUser, DataOutputStream dout, JFrame parentFrame) {
        // Layout 설정
        super(new FlowLayout(FlowLayout.RIGHT));  // 버튼을 우측 정렬

        // 배경색 설정
        setBackground(new Color(47, 49, 54));  // 어두운 회색 배경
        setOpaque(true);  // 불투명하게 설정하여 배경색이 보이도록 함

        // MyPage 버튼 생성
        JButton myPageButton = new JButton("설정");
        myPageButton.setPreferredSize(new Dimension(80, 30));  // 버튼 크기 설정
        myPageButton.setBackground(new Color(72, 103, 204));  // 디스코드 스타일의 파란색
        myPageButton.setForeground(Color.WHITE);  // 버튼 텍스트 색상
        myPageButton.setFocusPainted(false);  // 포커스 효과 제거
        myPageButton.setBorderPainted(false);  // 테두리 제거
        myPageButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                MyPage myPage = new MyPage(loggedUser, dout, parentFrame);
                myPage.setVisible(true);  // JDialog로 MyPage를 표시
            }
        });

        // 버튼을 TopPanel에 추가
        add(myPageButton);
    }
}
