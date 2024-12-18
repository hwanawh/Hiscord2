package client.ui;

import javax.swing.*;
import java.awt.*;
import java.io.File;
import java.io.PrintWriter;

public class ChannelPanel extends JPanel {
    private DefaultListModel<String> channelListModel;
    private JList<String> channelList;
    private RightPanel rightPanel; // RightPanel 인스턴스 추가

    public ChannelPanel(PrintWriter out, RightPanel rightPanel) { // RightPanel 인수 추가
        this.rightPanel = rightPanel; // 인스턴스 초기화
        setLayout(new BorderLayout());
        setBackground(new Color(47, 49, 54));

        // UI 초기화
        initializeUI(out);
        // 채널 목록 불러오기
        addChannels();
    }

    private void initializeUI(PrintWriter out) {
        // 상단 채널 라벨
        add(createChannelLabel(), BorderLayout.NORTH);
        // 중앙 채널 리스트
        add(createChannelList(out), BorderLayout.CENTER);
        // 하단 채널 추가 패널
        add(createAddChannelPanel(out), BorderLayout.SOUTH);
    }

    private JLabel createChannelLabel() {
        JLabel channelLabel = new JLabel("채널");
        channelLabel.setForeground(new Color(220, 221, 222));
        channelLabel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        return channelLabel;
    }

    private JScrollPane createChannelList(PrintWriter out) {
        channelListModel = new DefaultListModel<>();
        channelList = new JList<>(channelListModel);
        channelList.setBackground(new Color(47, 49, 54));
        channelList.setForeground(new Color(220, 221, 222));
        channelList.setSelectionForeground(Color.WHITE);
        channelList.setCellRenderer(new CircleCellRenderer());

        // 채널 전환 이벤트
        channelList.addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                String selectedChannel = channelList.getSelectedValue();
                if (selectedChannel != null) {
                    out.println("/join " + selectedChannel);
                    // InfoPanel 업데이트
                    boolean isChannel1 = selectedChannel.equals("channel1");
                    rightPanel.updateInfoPanel(isChannel1);
                }
            }
        });

        JScrollPane channelScrollPane = new JScrollPane(channelList);
        channelScrollPane.setBorder(BorderFactory.createEmptyBorder());
        channelScrollPane.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);
        channelScrollPane.getVerticalScrollBar().setOpaque(false);
        channelScrollPane.getVerticalScrollBar().setPreferredSize(new Dimension(0, 0)); // 스크롤바 숨기기

        return channelScrollPane;
    }

    private JPanel createAddChannelPanel(PrintWriter out) {
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

        // 새 채널 추가 이벤트
        addChannelButton.addActionListener(e -> {
            String newChannel = newChannelField.getText().trim();
            if (!newChannel.isEmpty() && !channelListModel.contains(newChannel)) {
                out.println("/addchannel " + newChannel);
                newChannelField.setText("");
            }
        });

        return addChannelPanel;
    }

    private void addChannels() {
        String projectDir = System.getProperty("user.dir");
        String path = projectDir + "/resources/channel";

        File channelFolder = new File(path);
        File[] directories = channelFolder.listFiles(File::isDirectory);

        if (directories != null) {
            for (File dir : directories) {
                channelListModel.addElement(dir.getName());
                System.out.println(dir.getName());
            }
        } else {
            System.out.println("No directories found or the path is incorrect.");
        }
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

            // 채널 이름 라벨
            JLabel label = new JLabel(value.toString(), SwingConstants.CENTER);
            label.setForeground(Color.WHITE);
            label.setFont(new Font("맑은 고딕", Font.BOLD, 16));

            panel.add(label, BorderLayout.CENTER);
            panel.setPreferredSize(new Dimension(100, 100)); // 동그라미 크기 설정
            panel.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5)); // 여백 설정
            panel.setOpaque(false); // 투명하게 설정

            return panel;
        }
    }
}
