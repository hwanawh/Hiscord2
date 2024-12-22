package client.ui;

import javax.swing.*;
import java.awt.*;
import java.io.DataOutputStream;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class ChannelPanel extends JPanel {
    private DefaultListModel<String> channelListModel;
    private JList<String> channelList;
    private RightPanel rightPanel; // RightPanel 인스턴스 추가

    public ChannelPanel(DataOutputStream dout, RightPanel rightPanel) { // RightPanel 인수 추가
        this.rightPanel = rightPanel; // 인스턴스 초기화
        setLayout(new BorderLayout());
        setBackground(new Color(47, 49, 54));

        // UI 초기화
        initializeUI(dout);
        // 채널 목록 불러오기
        addChannels();
    }

    private void initializeUI(DataOutputStream dout) {
        // 상단 채널 라벨
        add(createChannelLabel(), BorderLayout.NORTH);
        // 중앙 채널 리스트
        add(createChannelList(dout), BorderLayout.CENTER);
        // 하단 채널 추가 패널
        add(createAddChannelPanel(dout), BorderLayout.SOUTH);
    }

    private JLabel createChannelLabel() {
        JLabel channelLabel = new JLabel("채널");
        channelLabel.setForeground(new Color(220, 221, 222));
        channelLabel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        return channelLabel;
    }

    private JScrollPane createChannelList(DataOutputStream dout) {
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
                    try {
                        dout.writeUTF("/join " + selectedChannel);
                    } catch (IOException ex) {
                        throw new RuntimeException(ex);
                    }
                    // InfoPanel 업데이트
                    boolean isChannel1 = selectedChannel.equals("channel1");
                    rightPanel.updateInfoPanel(isChannel1);
                }
            }
        });

        // 우클릭 메뉴 추가
        JPopupMenu popupMenu = new JPopupMenu();
        JMenuItem leaveChannelItem = new JMenuItem("채널 나가기");
        leaveChannelItem.addActionListener(e -> leaveChannel(dout));
        popupMenu.add(leaveChannelItem);

        channelList.setComponentPopupMenu(popupMenu);

        JScrollPane channelScrollPane = new JScrollPane(channelList);
        channelScrollPane.setBorder(BorderFactory.createEmptyBorder());
        channelScrollPane.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);
        channelScrollPane.getVerticalScrollBar().setOpaque(false);
        channelScrollPane.getVerticalScrollBar().setPreferredSize(new Dimension(0, 0)); // 스크롤바 숨기기

        return channelScrollPane;
    }

    private void leaveChannel(DataOutputStream dout) {
        String selectedChannel = channelList.getSelectedValue();
        if (selectedChannel != null) {
            // 확인 대화상자 띄우기
            int response = JOptionPane.showConfirmDialog(
                    this,
                    "정말 채널을 나가시겠습니까?",
                    "채널 나가기",
                    JOptionPane.YES_NO_OPTION,
                    JOptionPane.QUESTION_MESSAGE
            );

            // 사용자가 '예'를 클릭한 경우
            if (response == JOptionPane.YES_OPTION) {
                try {
                    // 채널 나가기 명령 서버로 전송
                    dout.writeUTF("/leave " + selectedChannel);
                    // 채널 목록에서 삭제
                    channelListModel.removeElement(selectedChannel);
                } catch (IOException ex) {
                    throw new RuntimeException(ex);
                }
            }
            // '아니오'를 클릭하면 아무 작업도 하지 않음
        }
    }



    private JPanel createAddChannelPanel(DataOutputStream dout) {
        JPanel addChannelPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        addChannelPanel.setBackground(new Color(47, 49, 54));

        // 동그라미 모양의 "+" 버튼 패널
        JPanel circleButtonPanel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                g.setColor(new Color(88, 101, 242)); // 파란색 동그라미
                g.fillOval(0, 0, getWidth(), getHeight()); // 동그라미 그리기
            }
        };
        circleButtonPanel.setPreferredSize(new Dimension(60, 60)); // 동그라미 크기 조정
        circleButtonPanel.setLayout(new BorderLayout());
        circleButtonPanel.setOpaque(false);

        // "+" 버튼 라벨 (크고 굵게)
        JLabel addChannelLabel = new JLabel("+", SwingConstants.CENTER);
        addChannelLabel.setForeground(Color.WHITE);
        addChannelLabel.setFont(new Font("맑은 고딕", Font.BOLD, 30)); // 크고 굵은 폰트

        circleButtonPanel.add(addChannelLabel, BorderLayout.CENTER);

        // "+" 버튼 클릭 시 새 채널 추가
        circleButtonPanel.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseClicked(java.awt.event.MouseEvent e) {
                JTextField newChannelField = new JTextField();
                String newChannel = JOptionPane.showInputDialog("추가할 채널 이름을 입력하세요:");
                if (newChannel != null && !newChannel.trim().isEmpty() && !channelListModel.contains(newChannel)) {
                    try {
                        dout.writeUTF("/addchannel " + newChannel);
                        channelListModel.addElement(newChannel);
                    } catch (IOException ex) {
                        throw new RuntimeException(ex);
                    }
                }
            }
        });

        addChannelPanel.add(circleButtonPanel);

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
