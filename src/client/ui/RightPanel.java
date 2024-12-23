package client.ui;

import javax.swing.*;
import java.awt.*;
import java.io.DataInputStream;
import java.io.DataOutputStream;


public class RightPanel extends JPanel {

    private InfoPanel infoPanel;
    private MemberPanel memberPanel;
    private DataInputStream din;
    private DataOutputStream dout;
    private String currentChannel;
    public RightPanel(DataInputStream din,DataOutputStream dout,String currentChannel) {
        this.din=din;
        this.dout=dout;
        this.currentChannel=currentChannel;
        setLayout(new BorderLayout());
        setBackground(new Color(47, 49, 54));
        setPreferredSize(new Dimension(250, 0)); // 오른쪽 패널의 너비 설정

        // MemberPanel을 추가
        this.memberPanel = new MemberPanel();
        add(memberPanel, BorderLayout.NORTH);

        // InfoPanel을 추가
        this.infoPanel = new InfoPanel(dout); // InfoManager는 InfoPanel 내부에서 관리
        add(infoPanel, BorderLayout.CENTER);
    }

    public void loadInfoPanel(String content) {
        infoPanel.loadInfo(content);
    }

    public void setCurrentChannel(String currentChannel){
        infoPanel.setCurrentChannel(currentChannel);
    }

    public void loadMemberPanel(String profileUrl, String name) {
        memberPanel.addMemberLabel(profileUrl, name);
    }
}
