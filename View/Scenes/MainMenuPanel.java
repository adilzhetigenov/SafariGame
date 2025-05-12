package View.Scenes;

import Controller.MainMenuController;
import View.Components.CustomAnimatedButton;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Image;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;

public class MainMenuPanel extends JPanel {
    private Image backgroundImage = new ImageIcon("View/Assets/background.jpg").getImage();
    private ImageIcon logoIcon = new ImageIcon("View/Assets/GameLogo.png");
    private String labelTitle = "JUNGLE JUGGLE";
    //margin from the menu
    private static final int margin = 50;
    //buttons attributes
    private static final int buttonWidth = 200;
    private static final int buttonHeight = 60;
    //Existing buttons
    private JButton[] buttons = new JButton[4];

    //SubPanel containing all of of the objects
    private JPanel contentPanel = new JPanel();
    private MainMenuController menuController;
    public MainMenuPanel(MainMenuController menuController) {
        this.menuController = menuController;
        intializePanel();
    }

    private void intializePanel() {
        setLayout(new BorderLayout());
        setBorder(new EmptyBorder((margin * 3), margin, margin, margin));
        // Main content panel layout based on a vertical arragement
        contentPanel.setLayout(new BoxLayout(contentPanel, BoxLayout.Y_AXIS));
        // No background
        contentPanel.setOpaque(false);
        // Adding panel plus a vertical margin between them
        logoCreation();
        contentPanel.add(Box.createVerticalStrut(20));
        titleCreation();
        contentPanel.add(Box.createVerticalStrut(50));
        buttonsCreation();
        add(contentPanel, BorderLayout.CENTER);
    }

    private void logoCreation() {
        Image scaledLogo = logoIcon.getImage().getScaledInstance(300, 300, Image.SCALE_SMOOTH);
        JLabel logoLabel = new JLabel(new ImageIcon(scaledLogo));
        logoLabel.setAlignmentX(CENTER_ALIGNMENT);
        contentPanel.add(logoLabel);
    }

    private void titleCreation() {
        JLabel titleLabel = new JLabel(labelTitle);
        titleLabel.setAlignmentX(CENTER_ALIGNMENT);
        titleLabel.setFont(new Font("Impact", Font.BOLD, 70));
        titleLabel.setForeground(Color.white);
        contentPanel.add(Box.createVerticalStrut(50));
        contentPanel.add(titleLabel);
    }

    private void buttonsCreation() {
        CustomAnimatedButton[] customButtons = new CustomAnimatedButton[]{
            new CustomAnimatedButton("Start Game", buttonWidth, buttonHeight, () -> menuController.changePanelToMap()),
            new CustomAnimatedButton("Load Game", buttonWidth, buttonHeight, () -> menuController.changePanelToLoadGame()),
            new CustomAnimatedButton("Settings", buttonWidth, buttonHeight, () -> menuController.changePanelToSettings()),
            new CustomAnimatedButton("Exit Game", buttonWidth, buttonHeight, () -> System.exit(0))
        };
    
        for (CustomAnimatedButton button : customButtons) {
            contentPanel.add(button);
            contentPanel.add(Box.createVerticalStrut(10));
        }
    }


    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        g.drawImage(backgroundImage, 0, 0, getWidth(), getHeight(), this);
    }
}