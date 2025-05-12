package View;

import java.awt.Dimension;
import java.awt.Image;
import java.awt.Point;
import java.awt.Toolkit;
import java.awt.Cursor;
import java.awt.CardLayout;

import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JPanel;

import View.Scenes.MapPanel;
import View.Scenes.LoadPanel;
import View.Scenes.SettingsPanel;
import View.Scenes.MainMenuPanel;
import Controller.MainMenuController;

public class GameFrame extends JFrame {

    private String gameTitle = "Jungle Juggle";
    private ImageIcon icon = new ImageIcon("View/Assets/GameLogo.png");
    private Image cursorImage = Toolkit.getDefaultToolkit().getImage("View/Assets/cursor.png");
    private Point cursorPoint = new Point(0, 0);

    private CardLayout cardLayout = new CardLayout();
    private JPanel mainPanel = new JPanel(cardLayout);

    private MainMenuPanel mainMenu;
    private MapPanel mapPanel;
    private LoadPanel loadPanel;
    private SettingsPanel settingsPanel;
    private MainMenuController mainMenuController;

    public GameFrame() {
        mapPanel = new MapPanel(this);
        loadPanel = new LoadPanel();
        settingsPanel = new SettingsPanel();
        mainMenuController = new MainMenuController(this, mapPanel);
        mainMenu = new MainMenuPanel(mainMenuController);
        
        // Initialize the frame
        intializeFrame();
        
        // Initialize back button action in settings
        settingsPanel.addBackButtonListener(e -> changePanel("MainMenu"));
        loadPanel.addBackButtonListener(e -> changePanel("MainMenu"));
    }

    private void intializeFrame() {
        setTitle(gameTitle);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setIconImage(icon.getImage());

        // Set custom cursor
        Cursor customCursor = Toolkit.getDefaultToolkit().createCustomCursor(
            cursorImage, cursorPoint, "Custom Cursor"
        );
        this.setCursor(customCursor);

        // Add panels to the main panel with CardLayout
        mainPanel.add(mainMenu, "MainMenu");
        mainPanel.add(mapPanel, "Map");
        mainPanel.add(loadPanel, "Load");
        mainPanel.add(settingsPanel, "Settings");
        getContentPane().add(mainPanel);

        // Set screen size to full screen
        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        setSize(screenSize.width, screenSize.height);
        setLocationRelativeTo(null); // Center the window
        setVisible(true);
    }

    public void changePanel(String panel) {
        // This will switch between panels
        cardLayout.show(mainPanel, panel);
    }

    // Getter methods for panels (if needed)
    public MainMenuPanel getMainMenuPanel() {
        return mainMenu;
    }

    public MapPanel getMapPanel() {
        return mapPanel;
    }

    public LoadPanel getLoadPanel() {
        return loadPanel;
    }

    public SettingsPanel getSettingsPanel() {
        return settingsPanel;
    }
}
