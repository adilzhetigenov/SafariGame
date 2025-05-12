package View.Scenes;

import java.awt.*;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import javax.imageio.ImageIO;
import javax.swing.*;
import javax.swing.border.EmptyBorder;

public class SettingsPanel extends JPanel {
    private JCheckBox fullscreenCheckBox;
    private JSlider volumeSlider;
    private JComboBox<String> difficultyComboBox;
    private JComboBox<String> mapComboBox;
    private JButton backButton;
    private BufferedImage backgroundImage;

    private final Color baseColor = new Color(243, 201, 105);
    private final Color hoverColor = new Color(230, 180, 80);
    private final Color pressedColor = new Color(200, 150, 60);
    private final Color textColor = new Color(93, 58, 0);

    public SettingsPanel() {
        try {
            backgroundImage = ImageIO.read(new File("View/Assets/BackgroundBlurred.jpg"));
        } catch (IOException e) {
            e.printStackTrace();
        }

        setLayout(new GridBagLayout());
        setOpaque(false);

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);

        JPanel settingsContainer = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2 = (Graphics2D) g;
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(new Color(255, 235, 200, 200));
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 30, 30);
            }
        };
        settingsContainer.setLayout(new BoxLayout(settingsContainer, BoxLayout.Y_AXIS));
        settingsContainer.setOpaque(false);
        settingsContainer.setPreferredSize(new Dimension(500, 500));
        settingsContainer.setBorder(BorderFactory.createEmptyBorder(30, 50, 30, 50));

        settingsContainer.add(Box.createVerticalGlue());

        settingsContainer.add(createLabel("Fullscreen"));
        fullscreenCheckBox = new JCheckBox();
        fullscreenCheckBox.setAlignmentX(Component.CENTER_ALIGNMENT);
        fullscreenCheckBox.setOpaque(false);
        fullscreenCheckBox.setForeground(textColor);
        fullscreenCheckBox.setBackground(new Color(255, 255, 255, 100));
        fullscreenCheckBox.setBorder(BorderFactory.createLineBorder(baseColor, 2, true));
        settingsContainer.add(fullscreenCheckBox);

        settingsContainer.add(Box.createRigidArea(new Dimension(0, 15)));

        settingsContainer.add(createLabel("Volume"));
        volumeSlider = new JSlider(0, 100, 50);
        volumeSlider.setOpaque(false);
        volumeSlider.setAlignmentX(Component.CENTER_ALIGNMENT);
        volumeSlider.setPreferredSize(new Dimension(200, 40));
        volumeSlider.setPaintTicks(true);
        volumeSlider.setPaintLabels(true);
        volumeSlider.setMajorTickSpacing(25);
        settingsContainer.add(volumeSlider);

        settingsContainer.add(Box.createRigidArea(new Dimension(0, 15)));

        settingsContainer.add(createLabel("Difficulty"));
        difficultyComboBox = new JComboBox<>(new String[]{"Easy", "Medium", "Hard"});
        styleComboBox(difficultyComboBox);
        settingsContainer.add(difficultyComboBox);

        settingsContainer.add(Box.createRigidArea(new Dimension(0, 15)));

        settingsContainer.add(createLabel("Map"));
        mapComboBox = new JComboBox<>(new String[]{"Map 1", "Map 2", "Map 3"});
        styleComboBox(mapComboBox);
        settingsContainer.add(mapComboBox);

        settingsContainer.add(Box.createRigidArea(new Dimension(0, 30)));

        backButton = new JButton("Back to Main Menu") {
            @Override
            protected void paintComponent(Graphics g) {
                if (!isOpaque() && getBorder() instanceof EmptyBorder) {
                    Graphics2D g2 = (Graphics2D) g.create();
                    g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                    g2.setColor(getBackground());
                    g2.fillRoundRect(0, 0, getWidth(), getHeight(), 20, 20);
                    g2.dispose();
                }
                super.paintComponent(g);
            }
        };

        backButton.setAlignmentX(Component.CENTER_ALIGNMENT);
        backButton.setFont(new Font("Impact", Font.PLAIN, 18));
        backButton.setBackground(baseColor);
        backButton.setForeground(textColor);
        backButton.setFocusPainted(false);
        backButton.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20));
        backButton.setOpaque(false);
        backButton.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));

        backButton.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                backButton.setBackground(hoverColor);
            }

            public void mouseExited(java.awt.event.MouseEvent evt) {
                backButton.setBackground(baseColor);
            }

            public void mousePressed(java.awt.event.MouseEvent evt) {
                backButton.setBackground(pressedColor);
            }

            public void mouseReleased(java.awt.event.MouseEvent evt) {
                backButton.setBackground(hoverColor);
            }
        });

        settingsContainer.add(backButton);
        settingsContainer.add(Box.createVerticalGlue());

        add(settingsContainer, gbc);
    }

    private JLabel createLabel(String text) {
        JLabel label = new JLabel(text);
        label.setAlignmentX(Component.CENTER_ALIGNMENT);
        label.setFont(new Font("Impact", Font.PLAIN, 20));
        label.setForeground(textColor);
        return label;
    }

    private void styleComboBox(JComboBox<String> comboBox) {
        comboBox.setMaximumSize(new Dimension(200, 30));
        comboBox.setFont(new Font("Impact", Font.PLAIN, 16));
        comboBox.setBackground(Color.WHITE);
        comboBox.setForeground(textColor);
        comboBox.setBorder(BorderFactory.createLineBorder(baseColor, 2, true));
        comboBox.setAlignmentX(Component.CENTER_ALIGNMENT);
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        if (backgroundImage != null) {
            g.drawImage(backgroundImage, 0, 0, getWidth(), getHeight(), this);
        }
    }

    public boolean isFullscreen() {
        return fullscreenCheckBox.isSelected();
    }

    public int getVolume() {
        return volumeSlider.getValue();
    }

    public String getSelectedDifficulty() {
        return (String) difficultyComboBox.getSelectedItem();
    }

    public String getSelectedMap() {
        return (String) mapComboBox.getSelectedItem();
    }

    public void addBackButtonListener(ActionListener listener) {
        backButton.addActionListener(listener);
    }

}
