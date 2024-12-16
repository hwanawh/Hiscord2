package client.ui;

import javax.swing.*;
import java.awt.*;
import java.io.PrintWriter;

public class ChannelPanel extends JPanel {
    private DefaultListModel<String> channelListModel;
    private JList<String> channelList;

    public ChannelPanel(PrintWriter out) {
        setLayout(new BorderLayout());
        setBackground(new Color(47, 49, 54));

        JLabel channelLabel = new JLabel("채널");
        channelLabel.setForeground(new Color(220, 221, 222));
        channelLabel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        add(channelLabel, BorderLayout.NORTH);

        channelListModel = new DefaultListModel<>();
        channelList = new JList<>(channelListModel);
        channelList.setBackground(new Color(47, 49, 54));
        channelList.setForeground(new Color(220, 221, 222));
        channelList.setSelectionForeground(Color.WHITE);
        channelList.setCellRenderer(new CircleCellRenderer()); // 원형 셀 렌더러 적용

        JScrollPane channelScrollPane = new JScrollPane(channelList);
        channelScrollPane.setBorder(BorderFactory.createEmptyBorder());
        channelScrollPane.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);
        channelScrollPane.getVerticalScrollBar().setOpaque(false); // 스크롤바 투명하게 설정
        channelScrollPane.getVerticalScrollBar().setPreferredSize(new Dimension(0, 0)); // 스크롤바 숨기기
        
        add(channelScrollPane, BorderLayout.CENTER);

        JPanel addChannelPanel = new JPanel(new BorderLayout());
        addChannelPanel.setBackground(new Color(47, 49, 54));

        JTextField newChannelField = new JTextField();
        newChannelField.setBackground(new Color(64, 68, 75));
        newChannelField.setForeground(new Color(220, 221, 222));
        newChannelField.setCaretColor(new Color(220, 221, 222));
        addChannelPanel.add(newChannelField, BorderLayout.CENTER);

        JButton addChannelButton = new JButton("+");
        addChannelButton.setBackground(new Color(88, 101, 242));
        addChannelButton.setForeground(Color.WHITE);
        addChannelButton.setFocusPainted(false);
        addChannelPanel.add(addChannelButton, BorderLayout.EAST);

        add(addChannelPanel, BorderLayout.SOUTH);

        // 새 채널 추가 이벤트
        addChannelButton.addActionListener(e -> {
            String newChannel = newChannelField.getText().trim();
            if (!newChannel.isEmpty() && !channelListModel.contains(newChannel)) {
                out.println("/addchannel " + newChannel);
                newChannelField.setText("");
            }
        });

        // 채널 전환 이벤트
        channelList.addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                String selectedChannel = channelList.getSelectedValue();
                if (selectedChannel != null) {
                    out.println("/join " + selectedChannel);
                }
            }
        });

        addDefaultChannels();
    }

    private void addDefaultChannels() {
        channelListModel.addElement("네프 회의");
        channelListModel.addElement("채팅 공부");
        channelListModel.addElement("웹만들기");
        channelListModel.addElement("한성대학교");
        channelListModel.addElement("환수월드");
        channelListModel.addElement("준선월드");
    }

    private class CircleCellRenderer extends DefaultListCellRenderer {
        @Override
        public Component getListCellRendererComponent(JList<?> list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
            JPanel panel = new JPanel() {
                @Override
                protected void paintComponent(Graphics g) {
                    super.paintComponent(g);
                    // 기본 배경
                    g.setColor(new Color(78, 84, 92)); // 회색 동그라미
                    g.fillOval(0, 0, getWidth(), getHeight());

                    // 선택된 경우의 배경
                    if (isSelected) {
                        g.setColor(new Color(88, 101, 242)); // 파란색 동그라미
                        g.fillOval(0, 0, getWidth(), getHeight());
                    }
                }
            };

            panel.setLayout(new BorderLayout());

            // 채널 이름을 표시하는 라벨
            JLabel label = new JLabel(value.toString(), SwingConstants.CENTER);
            label.setForeground(Color.WHITE);
            label.setFont(new Font("맑은 고딕", Font.BOLD, 16)); // 글꼴은 맑은 고딕, 굵게, 크기는 16

            panel.add(label, BorderLayout.CENTER);
            panel.setPreferredSize(new Dimension(100, 100)); // 동그라미 크기 설정
            panel.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5)); // 여백 설정
            panel.setOpaque(false); // 투명하게 설정

            return panel;
        }
    }
}
