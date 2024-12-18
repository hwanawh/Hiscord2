package client.ui;

import javax.swing.*;
import java.awt.*;

public class RightPanel extends JPanel {
    private InfoPanel infoPanel;

    public RightPanel() {
        setLayout(new BorderLayout());
        setBackground(new Color(47, 49, 54));
        setPreferredSize(new Dimension(250, 0)); // 오른쪽 패널의 너비 설정

        // MemberPanel을 추가
        MemberPanel memberPanel = new MemberPanel();
        add(memberPanel, BorderLayout.NORTH);

        // InfoPanel을 추가
        infoPanel = new InfoPanel();
        add(infoPanel, BorderLayout.CENTER);
    }

    public void updateInfoPanel(boolean isChannel1) {
        infoPanel.updateInfo(isChannel1);
    }
}