package client.ui;

import javax.swing.*;
import java.awt.*;
import java.util.List;

public class RightPanel extends JPanel {
    private DefaultListModel<String> memberListModel;
    private JList<String> memberList;

    public RightPanel() {
        setLayout(new BorderLayout());
        setBackground(new Color(47, 49, 54));
        setPreferredSize(new Dimension(200, 0)); // 오른쪽 패널의 너비 설정

        JLabel membersLabel = new JLabel("접속 중인 멤버");
        membersLabel.setForeground(new Color(220, 221, 222));
        membersLabel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        add(membersLabel, BorderLayout.NORTH);

        memberListModel = new DefaultListModel<>();
        memberList = new JList<>(memberListModel);
        memberList.setBackground(new Color(47, 49, 54));
        memberList.setForeground(new Color(220, 221, 222));
        memberList.setSelectionForeground(Color.WHITE);

        JScrollPane memberScrollPane = new JScrollPane(memberList);
        memberScrollPane.setBorder(BorderFactory.createEmptyBorder());
        memberScrollPane.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);
        add(memberScrollPane, BorderLayout.CENTER);
    }

    // 멤버 목록 업데이트 메서드
    public void updateMembers(List<String> members) {
        memberListModel.clear();
        for (String member : members) {
            memberListModel.addElement(member);
        }
    }
}
