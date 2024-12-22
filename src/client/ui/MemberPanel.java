package client.ui;

import javax.swing.*;
import java.awt.*;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

public class MemberPanel extends JPanel {
    private DefaultListModel<Member> memberListModel;
    private JList<Member> memberList;
    private Map<String, Member> memberMap; // 중복 체크를 위한 맵

    public MemberPanel() {
        setLayout(new BorderLayout());
        setBackground(new Color(47, 49, 54));

        JLabel membersLabel = new JLabel("접속 중인 멤버");
        membersLabel.setForeground(new Color(220, 221, 222));
        membersLabel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        add(membersLabel, BorderLayout.NORTH);

        memberListModel = new DefaultListModel<>();
        memberMap = new HashMap<>();
        memberList = new JList<>(memberListModel);
        memberList.setBackground(new Color(47, 49, 54));
        memberList.setForeground(new Color(220, 221, 222));
        memberList.setSelectionForeground(Color.WHITE);

        // 커스텀 렌더러 설정
        memberList.setCellRenderer(new MemberCellRenderer());



        JScrollPane memberScrollPane = new JScrollPane(memberList);
        memberScrollPane.setBorder(BorderFactory.createEmptyBorder());
        memberScrollPane.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);
        memberScrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER); // 수평 스크롤바 숨기기
        memberScrollPane.getVerticalScrollBar().setPreferredSize(new Dimension(0, 0)); // 수직 스크롤바 숨기기
        add(memberScrollPane, BorderLayout.CENTER);

        // 마우스 휠로 스크롤 가능하게 하기
        memberScrollPane.addMouseWheelListener(e -> {
            JScrollBar verticalScrollBar = memberScrollPane.getVerticalScrollBar();
            int scrollAmount = e.getUnitsToScroll();
            verticalScrollBar.setValue(verticalScrollBar.getValue() + (scrollAmount * 3)); // 스크롤 속도 조절
        });
    }

    public void addMemberLabel(String profileUrl, String name) {
        if (memberMap.containsKey(name)) {
            System.out.println("이미 추가된 멤버: " + name);
            return; // 이미 추가된 멤버라면 무시
        }

        try {
            // 프로필 아이콘 생성
            ImageIcon profileIcon = new ImageIcon(profileUrl);
            Image scaledImage = profileIcon.getImage().getScaledInstance(32, 32, Image.SCALE_SMOOTH);
            profileIcon = new ImageIcon(scaledImage);

            // 멤버 추가
            Member newMember = new Member(profileIcon, name);
            memberListModel.addElement(newMember);
            memberMap.put(name, newMember);

            System.out.println("멤버 추가: " + name);
        } catch (Exception e) {
            System.err.println("프로필 URL 처리 중 오류: " + e.getMessage());
        }
    }

    // 멤버 객체
    private static class Member {
        private final ImageIcon icon;
        private final String name;

        public Member(ImageIcon icon, String name) {
            this.icon = icon;
            this.name = name;
        }

        public ImageIcon getIcon() {
            return icon;
        }

        public String getName() {
            return name;
        }
    }

    // 커스텀 셀 렌더러
    private static class MemberCellRenderer extends JLabel implements ListCellRenderer<Member> {
        @Override
        public Component getListCellRendererComponent(JList<? extends Member> list, Member value, int index,
                                                      boolean isSelected, boolean cellHasFocus) {
            setIcon(value.getIcon());
            setText(value.getName());
            setForeground(list.getForeground());
            setBackground(list.getBackground());
            setOpaque(true);

            if (isSelected) {
                setBackground(new Color(63, 65, 70));
            }

            return this;
        }
    }
}
