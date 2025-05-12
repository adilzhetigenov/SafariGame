package View.Components;

import Controller.GameManagerController;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;

public class PauseMenuPanel extends JPanel {

    private final GameManagerController controller;
    private final CardLayout cardLayout;

    private static final String MAIN_MENU = "MainMenu";
    private static final String SETTINGS_MENU = "SettingsMenu";
    
    // Safari-themed color palette
    private static final Color SAFARI_BEIGE = new Color(243, 233, 210, 230);  // Semi-transparent beige
    private static final Color SAFARI_BROWN = new Color(133, 94, 66);         // Safari brown
    private static final Color SAFARI_GREEN = new Color(113, 148, 48);        // Safari grass green
    private static final Color SAFARI_DARK = new Color(74, 53, 36);           // Dark brown
    private static final Color SAFARI_SAND = new Color(223, 205, 164);        // Sand color
    
    // Button dimensions
    private static final int BUTTON_WIDTH = 400;
    private static final int BUTTON_HEIGHT = 80;
    
    public PauseMenuPanel(GameManagerController controller) {
        this.controller = controller;
        this.cardLayout = new CardLayout();
        setLayout(cardLayout);
        setOpaque(false);

        setBounds(500, 300, 200, 550); // Slightly taller to accommodate new buttons

        add(createPauseButtonsPanel(), MAIN_MENU);
        add(createSettingsPanel(), SETTINGS_MENU);

        setVisible(false); // initially hidden
    }

    private JPanel createPauseButtonsPanel() {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setOpaque(true);
        panel.setBackground(SAFARI_BEIGE);
        panel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(SAFARI_BROWN, 3, true),
            BorderFactory.createEmptyBorder(20, 20, 20, 20)
        ));

        // Title
        JLabel titleLabel = new JLabel("Game Paused");
        titleLabel.setFont(new Font("Comic Sans MS", Font.BOLD, 24));
        titleLabel.setForeground(SAFARI_DARK);
        titleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        panel.add(titleLabel);
        panel.add(Box.createVerticalStrut(15));
        
        // Buttons
        panel.add(createAnimatedButton("Resume Game", e -> {
            controller.resumeGame();
            setVisible(false);
        }));
        panel.add(Box.createVerticalStrut(10));
        
        panel.add(createAnimatedButton("Save Game", e -> {
            // TODO: Implement save game logic
        }));
        panel.add(Box.createVerticalStrut(10));
        
        panel.add(createAnimatedButton("Load Game", e -> {
            // TODO: Implement load game logic
        }));
        panel.add(Box.createVerticalStrut(10));
        
        panel.add(createAnimatedButton("Settings", e -> switchTo(SETTINGS_MENU)));
        panel.add(Box.createVerticalStrut(10));
        
        panel.add(createAnimatedButton("Exit to Main Menu", e -> controller.goToMainMenu()));
        panel.add(Box.createVerticalStrut(10));

        return panel;
    }

    private Component createAnimatedButton(String text, java.awt.event.ActionListener listener) {
        CustomAnimatedButton button = new CustomAnimatedButton(text, BUTTON_WIDTH, BUTTON_HEIGHT, () -> {
            listener.actionPerformed(null);
        });
        
        // Match the safari theme by setting foreground text color only
        button.setForeground(new Color(74, 53, 36)); // SAFARI_DARK
        
        return button;
    }

    private JPanel createSettingsPanel() {
        JPanel mainPanel = new JPanel(new BorderLayout(10, 10));
        mainPanel.setBackground(SAFARI_BEIGE);
        mainPanel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(SAFARI_BROWN, 3, true),
            BorderFactory.createEmptyBorder(15, 20, 15, 20)
        ));
        
        // Title panel at top
        JPanel titlePanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        titlePanel.setBackground(SAFARI_BEIGE);
        JLabel titleLabel = new JLabel("Game Settings");
        titleLabel.setFont(new Font("Comic Sans MS", Font.BOLD, 24));
        titleLabel.setForeground(SAFARI_DARK);
        titlePanel.add(titleLabel);
        mainPanel.add(titlePanel, BorderLayout.NORTH);
        
        // Settings content panel
        JPanel settingsPanel = new JPanel();
        settingsPanel.setLayout(new BoxLayout(settingsPanel, BoxLayout.Y_AXIS));
        settingsPanel.setBackground(SAFARI_BEIGE);
        settingsPanel.setBorder(new EmptyBorder(10, 10, 10, 10));
        
        // Font size panel
        JPanel fontPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 10));
        fontPanel.setBackground(SAFARI_BEIGE);
        JLabel fontLabel = new JLabel("Font Size:");
        fontLabel.setFont(new Font("Comic Sans MS", Font.BOLD, 16));
        fontLabel.setForeground(SAFARI_DARK);
        fontPanel.add(fontLabel);
        
        JComboBox<Integer> fontSizeSelector = new JComboBox<>(new Integer[]{12, 14, 16, 18, 20, 24});
        fontSizeSelector.setSelectedItem(controller.getFontSize());
        fontSizeSelector.setBackground(SAFARI_SAND);
        fontSizeSelector.setForeground(SAFARI_DARK);
        fontSizeSelector.setFont(new Font("Comic Sans MS", Font.PLAIN, 14));
        fontSizeSelector.setBorder(BorderFactory.createLineBorder(SAFARI_BROWN, 1));
        fontSizeSelector.addActionListener(e -> {
            int selectedSize = (Integer) fontSizeSelector.getSelectedItem();
            controller.setFontSize(selectedSize);
        });
        fontPanel.add(fontSizeSelector);
        fontPanel.setAlignmentX(Component.LEFT_ALIGNMENT);
        settingsPanel.add(fontPanel);
        
        // Add separator
        JSeparator separator = new JSeparator(SwingConstants.HORIZONTAL);
        separator.setBackground(SAFARI_BROWN);
        separator.setForeground(SAFARI_BROWN);
        separator.setMaximumSize(new Dimension(Short.MAX_VALUE, 2));
        settingsPanel.add(separator);
        settingsPanel.add(Box.createVerticalStrut(10));
        
        // Sound toggle panel
        JPanel soundPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 10));
        soundPanel.setBackground(SAFARI_BEIGE);
        JCheckBox soundToggle = new JCheckBox("Enable Game Sound");
        soundToggle.setFont(new Font("Comic Sans MS", Font.BOLD, 16));
        soundToggle.setForeground(SAFARI_DARK);
        soundToggle.setBackground(SAFARI_BEIGE);
        soundToggle.setSelected(controller.isSoundOn());
        soundToggle.addActionListener(e -> controller.toggleSound(soundToggle.isSelected()));
        soundPanel.add(soundToggle);
        soundPanel.setAlignmentX(Component.LEFT_ALIGNMENT);
        settingsPanel.add(soundPanel);
        
        // Add volume slider
        JPanel volumePanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 10));
        volumePanel.setBackground(SAFARI_BEIGE);
        JLabel volumeLabel = new JLabel("Volume:");
        volumeLabel.setFont(new Font("Comic Sans MS", Font.BOLD, 16));
        volumeLabel.setForeground(SAFARI_DARK);
        volumePanel.add(volumeLabel);
        
        JSlider volumeSlider = new JSlider(0, 100, 75);
        volumeSlider.setBackground(SAFARI_BEIGE);
        volumeSlider.setForeground(SAFARI_DARK);
        volumeSlider.setPaintTicks(true);
        volumeSlider.setMajorTickSpacing(25);
        volumeSlider.setPaintLabels(true);
        volumePanel.add(volumeSlider);
        volumePanel.setAlignmentX(Component.LEFT_ALIGNMENT);
        settingsPanel.add(volumePanel);
        
        settingsPanel.add(Box.createVerticalStrut(20));
        
        mainPanel.add(settingsPanel, BorderLayout.CENTER);
        
        // Back button panel at bottom
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        buttonPanel.setBackground(SAFARI_BEIGE);
        
        // Use CustomAnimatedButton for the back button
        CustomAnimatedButton backButton = new CustomAnimatedButton("Back to Menu", BUTTON_WIDTH, BUTTON_HEIGHT, 
            () -> switchTo(MAIN_MENU));
        backButton.setForeground(SAFARI_DARK);
        buttonPanel.add(backButton);
        
        mainPanel.add(buttonPanel, BorderLayout.SOUTH);
        
        return mainPanel;
    }

    private void switchTo(String panelName) {
        cardLayout.show(this, panelName);
    }

    public void showMenu() {
        switchTo(MAIN_MENU);
        setVisible(true);
    }

    public void hideMenu() {
        setVisible(false);
    }
}