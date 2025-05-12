package View.Components;

import Controller.GameManagerController;
import Controller.KeyInputController;
import Controller.MapController;
import Model.GameManager;
import Model.ShopManager;
import View.Scenes.MapPanel;
import java.awt.*;
import java.awt.event.*;
import java.awt.geom.RoundRectangle2D;
import java.util.HashMap;
import java.util.Map;
import javax.swing.*;

public class ShopPanel extends JPanel {
    private MapPanel mapPanel;
    private GameManagerController uiController;
    private ShopManager shopManager;
    private GameManager gameManager;
    
    // Shop UI components
    private JPanel shopItemsContainer;
    private JTabbedPane categoryTabs;
    private JLabel balanceLabel;
    private JButton closeButton;
    private KeyInputController keyInputController;
    
    // Shop visual properties
    private Color backgroundColor = new Color(243, 201, 105, 240);
    private Color headerColor = new Color(153, 76, 0);
    private Color borderColor = new Color(85, 89, 67);
    private int cornerRadius = 20;
    
    // Shop item tracking
    private Map<String, Double> shopItems = new HashMap<>();
    
    public ShopPanel(MapPanel mapPanel, GameManagerController uiController, 
                      ShopManager shopManager, GameManager gameManager, KeyInputController keyInputController) {
        this.mapPanel = mapPanel;
        this.uiController = uiController;
        this.shopManager = shopManager;
        this.gameManager = gameManager;
        this.keyInputController = keyInputController;
        // Configure panel properties
        setSize(600, 500);
        setLocation(300, 300);
        setLayout(new BorderLayout());
        setOpaque(false);
        
        // Initialize UI components
        initComponents();
        
        // Make panel draggable
        makeDraggable();
    }
    
    private void initComponents() {
        // Create header panel with title and close button
        JPanel headerPanel = createHeaderPanel();
        add(headerPanel, BorderLayout.NORTH);
        
        // Create tabbed pane for different shop categories
        categoryTabs = new JTabbedPane();
        categoryTabs.setFont(new Font("Arial", Font.BOLD, 14));
        categoryTabs.setBackground(new Color(255, 240, 210));
        
        // Add shop category tabs
        addCategoryTab("Animals", createAnimalShopItems());
        addCategoryTab("Plants", createPlantShopItems());
        addCategoryTab("Infrastructure", createInfrastructureShopItems());
        addCategoryTab("Staff", createStaffShopItems());
        
        add(categoryTabs, BorderLayout.CENTER);
        
        // Create footer with balance info
        JPanel footerPanel = createFooterPanel();
        add(footerPanel, BorderLayout.SOUTH);
    }
    
    private JPanel createHeaderPanel() {
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setOpaque(false);
        headerPanel.setBorder(BorderFactory.createEmptyBorder(10, 15, 10, 15));
        
        JLabel titleLabel = new JLabel("Safari Shop");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 20));
        titleLabel.setForeground(headerColor);
        
        closeButton = new JButton("×");
        closeButton.setFont(new Font("Arial", Font.BOLD, 20));
        closeButton.setForeground(headerColor);
        closeButton.setBackground(new Color(0, 0, 0, 0));
        closeButton.setBorder(BorderFactory.createEmptyBorder());
        closeButton.setFocusPainted(false);
        closeButton.addActionListener(e -> setVisible(false));
        
        headerPanel.add(titleLabel, BorderLayout.WEST);
        headerPanel.add(closeButton, BorderLayout.EAST);
        
        return headerPanel;
    }
    
    private JPanel createFooterPanel() {
        JPanel footerPanel = new JPanel(new BorderLayout());
        footerPanel.setOpaque(false);
        footerPanel.setBorder(BorderFactory.createEmptyBorder(10, 15, 10, 15));
        
        balanceLabel = new JLabel("Balance: $" + gameManager.getCurrentCapital());
        balanceLabel.setFont(new Font("Arial", Font.BOLD, 16));
        balanceLabel.setForeground(headerColor);
        
        footerPanel.add(balanceLabel, BorderLayout.WEST);
        
        return footerPanel;
    }
    
    private void addCategoryTab(String name, JScrollPane contentPanel) {
        categoryTabs.addTab(name, contentPanel);
    }
    
    private JScrollPane createAnimalShopItems() {
        JPanel panel = new JPanel();
        panel.setLayout(new GridLayout(0, 3, 10, 10));
        panel.setBackground(new Color(255, 240, 210));
        panel.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));
        
        // Add animal shop items
        addShopItem(panel, "Lion", 500.0, "View/Assets/Lion/lion_down_1.png");
        addShopItem(panel, "Zebra", 250.0, "View/Assets/Zebra/zebra_down_1.png");
    
        return new JScrollPane(panel);
    }
    
    private JScrollPane createPlantShopItems() {
        JPanel panel = new JPanel();
        panel.setLayout(new GridLayout(0, 3, 10, 10));
        panel.setBackground(new Color(255, 240, 210));
        panel.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));
        
        // Add plant shop items
        addShopItem(panel, "Tree", 100.0, "View/Assets/real_tree.png");
        addShopItem(panel, "Bush", 50.0, "View/Assets/real_bush.png");

        
        return new JScrollPane(panel);
    }
    
    private JScrollPane createInfrastructureShopItems() {
        JPanel panel = new JPanel();
        panel.setLayout(new GridLayout(0, 3, 10, 10));
        panel.setBackground(new Color(255, 240, 210));
        panel.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));
        
        // Add infrastructure shop items
        addShopItem(panel, "Pond", 100.0, "View/Assets/water.png");
        addShopItem(panel, "Road", 75.0, "View/Assets/road.png");
        
        return new JScrollPane(panel);
    }
    
    private JScrollPane createStaffShopItems() {
        JPanel panel = new JPanel();
        panel.setLayout(new GridLayout(0, 3, 10, 10));
        panel.setBackground(new Color(255, 240, 210));
        panel.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));
        
        // Add staff shop items
        addShopItem(panel, "Ranger", 200.0, "View/Assets/Ranger/boy_down_1.png");
        addShopItem(panel, "Jeep", 500.0, "View/Assets/Jeep/jeep_down.png");
        
        return new JScrollPane(panel);
    }
    
    private void addShopItem(JPanel panel, String name, double price, String imagePath) {
        // Store item price for purchase logic
        shopItems.put(name, price);
        
        // Create a new ShopItem and add it to the panel
        ShopItemPanel item = new ShopItemPanel(name, price, imagePath, this);
        item.makeDraggable();
        panel.add(item);
    }
    
    // This method is used by ShopItem class
    public void updateBalanceDisplay() {
        balanceLabel.setText("Balance: $" + gameManager.getCurrentCapital());
    }
    
/**
 * Gets a random valid location (sand tile) on the map for item placement.
 * @return Point with valid world coordinates
 */
public Point getRandomValidLocation() {
    MapController mapController = getMapPanel().getMapController();
    int maxWorldCols = getMapPanel().getMaxWorldColumns();
    int maxWorldRows = getMapPanel().getMaxWorldRows();
    int tileSize = getMapPanel().getTitleSize();
    
    // Maximum attempts to find a valid location
    int maxAttempts = 100;
    int attempts = 0;
    
    while (attempts < maxAttempts) {
        // Generate random tile coordinates
        int tileX = (int)(Math.random() * maxWorldCols);
        int tileY = (int)(Math.random() * maxWorldRows);
        
        // Check if it's a sand tile
        if (mapController.getMapTile(tileX, tileY) == 0) {
            // Convert to world coordinates (center of tile)
            return new Point(tileX * tileSize + tileSize/2, tileY * tileSize + tileSize/2);
        }
        
        attempts++;
    }
    
    // If no valid location found after maximum attempts, return null or a default location
    return null; // Or handle this case as appropriate for your game
}
    
// Update this method in ShopPanel class
public void addItemToMap(String itemName, Point location) {
    // Based on the item name, add the appropriate entity or change the tile
    int x = (int) location.getX();
    int y = (int) location.getY();
    
    // Convert world coordinates to tile coordinates
    int tileSize = mapPanel.getTitleSize();
    int tileX = x / tileSize;
    int tileY = y / tileSize;
    
    switch (itemName) {
        case "Lion":
            // Add a lion at the specified location
            System.out.println("Adding Lion at " + x + ", " + y);
            mapPanel.addEntity(new View.Enities.LionSprite(mapPanel, 
                mapPanel.getColissionCont(), x, y));
                //gameManager.decreaseCapital(300);
                // gameManager.increaseCustomerCount(1);
                mapPanel.updateDisplay();
            break;
        case "Zebra":
            // Add a lion at the specified location
            System.out.println("Adding Zebra at " + x + ", " + y);
            mapPanel.addEntity(new View.Enities.ZebraSprite(mapPanel, 
                mapPanel.getColissionCont(), x, y));
                mapPanel.updateDisplay();
            break;
        case "Ranger":
            // Add a ranger at the specified location
            System.out.println("Adding Ranger at " + x + ", " + y);
            mapPanel.addEntity(new View.Enities.RangerSprite(mapPanel, 
                "Ranger", keyInputController, mapPanel.getColissionCont(), x, y));
                mapPanel.updateDisplay();
            break;
        case "Jeep":
            // Add a jeep at the specified location
            System.out.println("Adding Jeep at " + x + ", " + y);
            mapPanel.addEntity(new View.Enities.JeepSprite(mapPanel, 
                mapPanel.getColissionCont(), x, y));
                mapPanel.updateDisplay();
            break;
        case "Tree":
            // Change the tile to a tree (tile type 2)
            System.out.println("Adding Tree at tile " + tileX + ", " + tileY);
            mapPanel.getMapController().setMapTile(tileX, tileY, 2);
            mapPanel.updateDisplay();
            break;
        case "Bush":
            // Change the tile to a bush (tile type 3)
            System.out.println("Adding Bush at tile " + tileX + ", " + tileY);
            mapPanel.getMapController().setMapTile(tileX, tileY, 3);
            mapPanel.updateDisplay();
            break;
        case "Pond":
            // Change the tile to water (tile type 1)
            System.out.println("Adding Pond at tile " + tileX + ", " + tileY);
            mapPanel.getMapController().setMapTile(tileX, tileY, 1);
            mapPanel.updateDisplay();
            break;
        case "Road":
            // Change the tile to road (tile type 5)
            System.out.println("Adding Road at tile " + tileX + ", " + tileY);
            mapPanel.getMapController().setMapTile(tileX, tileY, 5);
            mapPanel.updateDisplay();
            break;
        case "Fence":
            // Change the tile to fence (tile type 4)
            System.out.println("Adding Fence at tile " + tileX + ", " + tileY);
            mapPanel.getMapController().setMapTile(tileX, tileY, 4);
            mapPanel.updateDisplay();
            break;
        default:
            System.out.println("Unknown item type: " + itemName);
            break;
    }
}
    private void makeDraggable() {
        // Make the whole panel draggable
        MouseAdapter dragAdapter = new MouseAdapter() {
            private Point dragStart = null;
            
            @Override
            public void mousePressed(MouseEvent e) {
                dragStart = e.getPoint();
            }
            
            @Override
            public void mouseReleased(MouseEvent e) {
                dragStart = null;
            }
            
            @Override
            public void mouseDragged(MouseEvent e) {
                if (dragStart != null) {
                    Point location = getLocation();
                    setLocation(
                        location.x + e.getX() - dragStart.x,
                        location.y + e.getY() - dragStart.y
                    );
                }
            }
        };
        
        addMouseListener(dragAdapter);
        addMouseMotionListener(dragAdapter);
    }
    
    @Override
    protected void paintComponent(Graphics g) {
        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        
        // Draw rounded rectangle background
        g2.setColor(backgroundColor);
        g2.fill(new RoundRectangle2D.Float(0, 0, getWidth(), getHeight(), cornerRadius, cornerRadius));
        
        // Draw border
        g2.setColor(borderColor);
        g2.setStroke(new BasicStroke(2.0f));
        g2.draw(new RoundRectangle2D.Float(1, 1, getWidth() - 2, getHeight() - 2, cornerRadius, cornerRadius));
        
        g2.dispose();
        super.paintComponent(g);
    }
    
    public void refreshShop() {
        updateBalanceDisplay();
    }
    
    // Getter methods needed by ShopItem
    public MapPanel getMapPanel() {
        return mapPanel;
    }
    
    public GameManager getGameManager() {
        return gameManager;
    }

    public GameManagerController getGameManagerController() {
        return uiController;
    }
}