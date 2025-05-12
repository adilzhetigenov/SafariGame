package View.Scenes;

import Controller.LoadController;
import java.awt.*;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.List;
import javax.imageio.ImageIO;
import javax.swing.*;

public class LoadPanel extends JPanel {

    private final String saveDirectory = "View/Maps/";
    private final LoadController loadController;
    private final JButton backButton;
    private BufferedImage backgroundImage;

    // Colors and styles
    private final Color baseColor = new Color(243, 201, 105);
    private final Color hoverColor = new Color(230, 180, 80);
    private final Color pressedColor = new Color(200, 150, 60);
    private final Color textColor = new Color(93, 58, 0);

    public LoadPanel() {
        this.loadController = new LoadController(saveDirectory);

        try {
            backgroundImage = ImageIO.read(new File("View/Assets/BackgroundBlurred.jpg"));
        } catch (IOException e) {
            e.printStackTrace();
        }

        setLayout(new GridBagLayout());
        setOpaque(false);

        // Translucent rounded container
        JPanel roundedContainer = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(new Color(255, 235, 200, 220));
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 30, 30);
                g2.dispose();
            }
        };
        roundedContainer.setPreferredSize(new Dimension(450, 500));
        roundedContainer.setOpaque(false);
        roundedContainer.setLayout(new BorderLayout(10, 10));
        roundedContainer.setBorder(BorderFactory.createEmptyBorder(20, 30, 20, 30));

        // Content panel
        JPanel contentPanel = new JPanel();
        contentPanel.setLayout(new BoxLayout(contentPanel, BoxLayout.Y_AXIS));
        contentPanel.setOpaque(false);

        // Title
        JLabel titleLabel = new JLabel("Load Game");
        titleLabel.setFont(new Font("Impact", Font.BOLD, 28));
        titleLabel.setForeground(textColor);
        titleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        contentPanel.add(titleLabel);
        contentPanel.add(Box.createRigidArea(new Dimension(0, 20)));

        // Game list
        List<String> games = loadController.getAvailableGames();
        if (games.isEmpty()) {
            JLabel noGamesLabel = new JLabel("No games found.");
            noGamesLabel.setFont(new Font("Arial", Font.BOLD, 16));
            noGamesLabel.setForeground(Color.RED);
            noGamesLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
            contentPanel.add(noGamesLabel);
        } else {
            for (String gameName : games) {
                JButton gameButton = createStyledButton(gameName);
                gameButton.addActionListener(e -> loadGame(gameName));
                contentPanel.add(gameButton);
                contentPanel.add(Box.createRigidArea(new Dimension(0, 10)));
            }
        }

        // Back Button
        backButton = createStyledButton("Back to Main Menu");
        contentPanel.add(Box.createRigidArea(new Dimension(0, 20)));
        contentPanel.add(backButton);

        // Scrollable content
        JScrollPane scrollPane = new JScrollPane(contentPanel);
        scrollPane.setOpaque(false);
        scrollPane.getViewport().setOpaque(false);
        scrollPane.setBorder(null);
        scrollPane.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);

        roundedContainer.add(scrollPane, BorderLayout.CENTER);

        add(roundedContainer, new GridBagConstraints());
    }

    private JButton createStyledButton(String text) {
        JButton button = new JButton(text) {
            @Override
            protected void paintComponent(Graphics g) {
                if (!isOpaque()) {
                    Graphics2D g2 = (Graphics2D) g.create();
                    g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                    g2.setColor(getBackground());
                    g2.fillRoundRect(0, 0, getWidth(), getHeight(), 20, 20);
                    g2.dispose();
                }
                super.paintComponent(g);
            }
        };

        button.setAlignmentX(Component.CENTER_ALIGNMENT);
        button.setMaximumSize(new Dimension(250, 45));
        button.setFont(new Font("Impact", Font.PLAIN, 18));
        button.setFocusPainted(false);
        button.setContentAreaFilled(false);
        button.setOpaque(false);
        button.setBackground(baseColor);
        button.setForeground(textColor);
        button.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20));
        button.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));

        // Hover Effects
        button.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                button.setBackground(hoverColor);
            }

            public void mouseExited(java.awt.event.MouseEvent evt) {
                button.setBackground(baseColor);
            }

            public void mousePressed(java.awt.event.MouseEvent evt) {
                button.setBackground(pressedColor);
            }

            public void mouseReleased(java.awt.event.MouseEvent evt) {
                button.setBackground(hoverColor);
            }
        });

        return button;
    }

    private void loadGame(String gameName) {
        System.out.println("Loading game: " + gameName);
        // TODO: Add your actual loading logic here
    }

    public void addBackButtonListener(ActionListener listener) {
        backButton.addActionListener(listener);
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        if (backgroundImage != null) {
            g.drawImage(backgroundImage, 0, 0, getWidth(), getHeight(), this);
        }
    }
}
