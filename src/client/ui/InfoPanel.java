package client.ui;

import javax.swing.*;
import javax.swing.plaf.basic.BasicScrollBarUI;
import java.awt.*;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

public class InfoPanel extends JPanel {
    private static final String CHANNEL1_PATH = "resources/channel/channel1/info/info1.txt";
    private static final String CHANNEL2_PATH = "resources/channel/channel2/info/info1.txt";
    private JTextArea infoTextArea;

    public InfoPanel() {
        setLayout(new BorderLayout());
        setBackground(new Color(47, 49, 54));
        setPreferredSize(new Dimension(200, 50));

        JLabel infoLabel = new JLabel("정보 패널");
        infoLabel.setForeground(new Color(220, 221, 222));
        infoLabel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        add(infoLabel, BorderLayout.NORTH);

        infoTextArea = new JTextArea();
        infoTextArea.setBackground(new Color(47, 49, 54));
        infoTextArea.setForeground(new Color(220, 221, 222));
        infoTextArea.setEditable(false);
        infoTextArea.setText(""); // 초기에는 아무 내용도 표시하지 않음

        JScrollPane infoScrollPane = createTransparentScrollPane(infoTextArea);
        add(infoScrollPane, BorderLayout.CENTER);
    }

    public void updateInfo(boolean isChannel1) {
        infoTextArea.setText(loadInfo(isChannel1));
    }

    private String loadInfo(boolean isChannel1) {
        String path = isChannel1 ? CHANNEL1_PATH : CHANNEL2_PATH;
        StringBuilder content = new StringBuilder();

        try (BufferedReader br = new BufferedReader(new FileReader(path))) {
            String line;
            while ((line = br.readLine()) != null) {
                content.append(line).append("\n");
            }
        } catch (IOException e) {
            content.append("정보를 불러오는 데 오류가 발생했습니다.");
        }

        return content.toString();
    }

    private JScrollPane createTransparentScrollPane(JTextArea textArea) {
        JScrollPane scrollPane = new JScrollPane(textArea);

        // 스크롤바 설정
        scrollPane.setOpaque(false);
        scrollPane.getViewport().setOpaque(false);
        scrollPane.setBorder(BorderFactory.createEmptyBorder());
        scrollPane.getVerticalScrollBar().setUI(new TransparentScrollBarUI());
        scrollPane.getHorizontalScrollBar().setUI(new TransparentScrollBarUI());
        return scrollPane;
    }

    // 투명 스크롤바 UI 내부 클래스
    private static class TransparentScrollBarUI extends BasicScrollBarUI {
        @Override
        protected void configureScrollBarColors() {
            this.thumbColor = new Color(200, 200, 200, 50); // 스크롤 thumb 투명도 설정
        }

        @Override
        protected JButton createDecreaseButton(int orientation) {
            return createZeroButton();
        }

        @Override
        protected JButton createIncreaseButton(int orientation) {
            return createZeroButton();
        }

        private JButton createZeroButton() {
            JButton button = new JButton();
            button.setPreferredSize(new Dimension(0, 0));
            button.setMinimumSize(new Dimension(0, 0));
            button.setMaximumSize(new Dimension(0, 0));
            return button;
        }

        @Override
        protected void paintTrack(Graphics g, JComponent c, Rectangle trackBounds) {
            // 트랙을 비워둠 (투명)
        }

        @Override
        protected void paintThumb(Graphics g, JComponent c, Rectangle thumbBounds) {
            if (thumbBounds.isEmpty() || !scrollbar.isEnabled()) {
                return;
            }
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setPaint(thumbColor);
            g2.fillRoundRect(thumbBounds.x, thumbBounds.y, thumbBounds.width, thumbBounds.height, 10, 10);
            g2.dispose();
        }
    }
}
