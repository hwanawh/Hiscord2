package client.ui;

import javax.swing.*;
import java.awt.*;
import server.InfoManager;  // InfoManager를 임포트

public class RightPanel extends JPanel {
    private InfoPanel infoPanel;
    private MemberPanel memberPanel;

    public RightPanel(InfoManager infoManager) {
        setLayout(new BorderLayout());
        setBackground(new Color(47, 49, 54));
        setPreferredSize(new Dimension(250, 0)); // 오른쪽 패널의 너비 설정

        // MemberPanel을 추가
        this.memberPanel = new MemberPanel();
        add(memberPanel, BorderLayout.NORTH);



        // InfoPanel을 추가, InfoManager를 전달하여 초기화
        infoPanel = new InfoPanel(infoManager);
        add(infoPanel, BorderLayout.CENTER);
    }

    public void updateInfoPanel(boolean isChannel1) {
        infoPanel.updateInfo(isChannel1 ? "channel1" : "channel2");
    }
    public void loadMemberPanel(String profileUrl,String name){
        memberPanel.addMemberLabel(profileUrl,name);
    }
}
