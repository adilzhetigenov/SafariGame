package View.Components;
import Controller.GameManagerController;
import Controller.KeyInputController;
import Model.GameManager;
import Model.ShopManager;
import View.Scenes.MapPanel;
import java.awt.BasicStroke;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.RenderingHints;
import java.awt.geom.RoundRectangle2D;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.ImageIcon;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;
import Controller.BuildingController;

public class MapBottomPanel extends JPanel {
    private MapPanel map;
    private GameManagerController ui;
    
    // Updated colors to match the game's aesthetic
    private Color backgroundColor = new Color(112, 117, 90); // Muted khaki-grey
    private Color borderColor = new Color(85, 89, 67); // Darker edge
    
    private JPanel contentPanel = new JPanel();
    private int width = 350;
    private int height = 100;
    private int postionX = 380;
    private int positionY = 750;
    private int buttonWidth = 96;
    private int buttonHeight = 96;
    private int cornerRadius = 20; // Rounded corners
    private int buttonSpacing = 20; // Increased spacing between buttons
    
    private ImageIcon ShopImage = new ImageIcon("View/Assets/storefront.png");
    private ImageIcon InfoImage = new ImageIcon("View/Assets/info.png");
    private ImageIcon BuildImage = new ImageIcon("View/Assets/hammer.png");
    private ImageIcon ExitBuildImage = new ImageIcon("View/Assets/exit.png");
    
    private boolean isInBuildMode = false;
    private BuildModePanel buildModePanel;
    private BuildingController buildingController;
    
    //for the shop
    private ShopPanel shopPanel;
    private InfoPanel infoPanel;
    private GameManager gameManager;
    private ShopManager shopManager;
    private KeyInputController keyInputController;

    public MapBottomPanel(MapPanel map, GameManagerController ui, GameManager gameManager, ShopManager shopManager, KeyInputController keyInputController) {
        this.map = map;
        this.ui = ui;
        this.gameManager = gameManager;
        this.shopManager = shopManager;
        this.keyInputController = keyInputController;
        setLayout(new BorderLayout());
        setOpaque(false);
        
        setBounds(380, 750, 700, 100);
        
        // Initialize building controller
        buildingController = new BuildingController(map);
        
        // Initialize build mode panel
        buildModePanel = new BuildModePanel();
        buildModePanel.setBounds(map.getWidth()/2 - 100, map.getHeight() - 200, 200, 40);
        buildModePanel.setVisible(false);
        map.add(buildModePanel);
        
        intializePanel();
        add(contentPanel, BorderLayout.CENTER);
        
        // Create shop panel but don't show it yet
        shopPanel = new ShopPanel(map, ui, shopManager, gameManager, keyInputController);
        shopPanel.setVisible(false);
        map.add(shopPanel);

        // Create info panel but don't show it yet
        infoPanel = new InfoPanel(map, ui, gameManager, keyInputController);
        infoPanel.setVisible(false);
        map.add(infoPanel);

        map.addComponentListener(new java.awt.event.ComponentAdapter() {
            @Override
            public void componentResized(java.awt.event.ComponentEvent evt) {
                int newX = map.getWidth()/3 + 80;
                int newY = map.getHeight() - height - 50;
                setBounds(newX, newY, width, height);
                
                // Update build mode panel position
                if (buildModePanel != null) {
                    buildModePanel.setBounds(
                        map.getWidth()/2 - 100,
                        map.getHeight() - 200,
                        200,
                        40
                    );
                }
                
                map.revalidate();
                map.repaint();
            }
        });
    }
    public void intializePanel() {
        contentPanel.setLayout(new BoxLayout(contentPanel, BoxLayout.X_AXIS));
        contentPanel.setOpaque(false);
        // Add padding inside the panel
        contentPanel.setBorder(new EmptyBorder(10, 15, 10, 15));
        buttonsCreation();
    }
    
    public void buttonsCreation() {
        CustomAnimatedIcon[] customButtons = new CustomAnimatedIcon[]{
            new CustomAnimatedIcon(ShopImage, buttonWidth, buttonHeight, () -> toggleShopPanel()),
            new CustomAnimatedIcon(BuildImage, buttonWidth, buttonHeight, () -> toggleBuildMode()),
            new CustomAnimatedIcon(InfoImage, buttonWidth, buttonHeight, () -> toggleInfoPanel()),
        };
        
        for (CustomAnimatedIcon button : customButtons) {
            button.setFocusable(false);
            contentPanel.add(button);
            contentPanel.add(Box.createHorizontalStrut(buttonSpacing));
        }
    }
    
    private void toggleShopPanel() {
        if (shopPanel != null) {
            shopPanel.setVisible(!shopPanel.isVisible());
            shopPanel.refreshShop();
        }
    }

    private void toggleInfoPanel() {
        if (infoPanel != null) {
            infoPanel.setVisible(!infoPanel.isVisible());
            infoPanel.refreshStats();
        }
    }
    
    private void toggleBuildMode() {
        buildingController.toggleBuildMode();
        buildModePanel.setVisible(buildingController.isInBuildMode());
        
        // Hide other panels when entering build mode
        if (buildingController.isInBuildMode()) {
            shopPanel.setVisible(false);
            infoPanel.setVisible(false);
        }
    }
    
    @Override
    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2 = (Graphics2D) g.create();
        
        // Enable antialiasing for smoother edges
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        
        // Draw rounded rectangle background
        g2.setColor(backgroundColor);
        g2.fill(new RoundRectangle2D.Float(0, 0, getWidth(), getHeight(), cornerRadius, cornerRadius));
        
        // Add border
        g2.setColor(borderColor);
        g2.setStroke(new BasicStroke(2.0f));
        g2.draw(new RoundRectangle2D.Float(1, 1, getWidth() - 2, getHeight() - 2, cornerRadius, cornerRadius));
        
        // Add subtle highlight at top for 3D effect
        g2.setColor(new Color(255, 255, 255, 40));
        g2.setStroke(new BasicStroke(1.0f));
        g2.drawLine(cornerRadius, 3, getWidth() - cornerRadius, 3);
        
        g2.dispose();
    }
    
    // Setter methods for customization if needed
    public void setBackgroundColor(Color color) {
        this.backgroundColor = color;
        repaint();
    }
    
    public void setBorderColor(Color color) {
        this.borderColor = color;
        repaint();
    }
    
    public void setButtonSpacing(int spacing) {
        this.buttonSpacing = spacing;
        // Recreate the panel with new spacing
        contentPanel.removeAll();
        buttonsCreation();
        contentPanel.revalidate();
        contentPanel.repaint();
    }
}