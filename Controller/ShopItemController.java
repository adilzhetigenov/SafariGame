package Controller;

import View.Components.ShopItemPanel;
import View.Components.ShopPanel;
import java.awt.*;
import java.awt.event.*;
import javax.swing.*;


public class ShopItemController {
    private ShopItemPanel view;
    private ShopPanel parentShop;
    
   
    private JLabel ghostImage;
    private JPanel messagePanel;
    private Point dragOrigin;
    
    
     

    public ShopItemController(ShopItemPanel view, ShopPanel parentShop) {
        this.view = view;
        this.parentShop = parentShop;
    }
    


    private void updateMessagePanel(String message, Color textColor) {
        if (messagePanel != null) {
            JLabel label = (JLabel) messagePanel.getComponent(0);
            label.setText(message);
            label.setForeground(textColor);
            
            // Get current mouse position
            Point screenPoint = MouseInfo.getPointerInfo().getLocation();
            Point mapPoint = parentShop.getMapPanel().getLocationOnScreen();
            
            // Position message panel above the cursor
            messagePanel.setSize(messagePanel.getPreferredSize());
            messagePanel.setLocation(
                (int)(screenPoint.getX() - mapPoint.getX() - messagePanel.getWidth() / 2),
                (int)(screenPoint.getY() - mapPoint.getY() - messagePanel.getHeight() - 20)
            );
            
            // Get tile coordinates
            int x = (int)(screenPoint.getX() - mapPoint.getX());
            int y = (int)(screenPoint.getY() - mapPoint.getY());
            
            // Draw tile highlight
            if (isDropLocationValid(x, y)) {
                drawTileHighlight(x, y, new Color(0, 255, 0, 100)); // Green for valid
            } else {
                drawTileHighlight(x, y, new Color(255, 0, 0, 100)); // Red for invalid
            }
        }
    }

    private void drawTileHighlight(int screenX, int screenY, Color color) {
        // Create a temporary panel for the highlight
        JPanel highlightPanel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                g.setColor(color);
                g.fillRect(0, 0, getWidth(), getHeight());
            }
        };
        highlightPanel.setOpaque(false);
        
        // Calculate tile position in screen coordinates
        int tileSize = parentShop.getMapPanel().getTitleSize();
        int tileX = (screenX / tileSize) * tileSize;
        int tileY = (screenY / tileSize) * tileSize;
        
        // Position the highlight panel
        highlightPanel.setBounds(tileX, tileY, tileSize, tileSize);
        
        // Add to layered pane
        if (SwingUtilities.getWindowAncestor(view) != null) {
            JLayeredPane layeredPane = SwingUtilities.getRootPane(view).getLayeredPane();
            layeredPane.add(highlightPanel, JLayeredPane.DRAG_LAYER);
            
            // Remove the highlight after a short delay
            Timer timer = new Timer(100, e -> {
                layeredPane.remove(highlightPanel);
                layeredPane.repaint();
            });
            timer.setRepeats(false);
            timer.start();
        }
    }

    public void setupDragAndDrop() {
        view.addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                if (hasEnoughFunds()) {
                    dragOrigin = e.getPoint();
                    
                    if (SwingUtilities.getWindowAncestor(view) != null) {
                        JLayeredPane layeredPane = SwingUtilities.getRootPane(view).getLayeredPane();
                        
                        if (ghostImage == null) {
                            ghostImage = view.getGhostImage(true);
                            // Make ghost image larger (1.5x size)
                            Dimension size = ghostImage.getSize();
                            ghostImage.setSize((int)(size.width * 1.5), (int)(size.height * 1.5));
                        }
                        
                        if (messagePanel == null) {
                            messagePanel = view.getMessagePanel(true);
                        }
                        
                        layeredPane.add(ghostImage, JLayeredPane.DRAG_LAYER);
                        layeredPane.add(messagePanel, JLayeredPane.POPUP_LAYER);
                        
                        parentShop.setVisible(false);
                        messagePanel.setVisible(true);
                        
                        Point screenPoint = MouseInfo.getPointerInfo().getLocation();
                        ghostImage.setLocation(
                            screenPoint.x - ghostImage.getWidth() / 2,
                            screenPoint.y - ghostImage.getHeight() / 2
                        );
                        ghostImage.setVisible(true);
                        
                        // Initial message and highlight
                        updateMessagePanel("Drag to place " + view.getName(), Color.WHITE);
                    }
                }
            }
            
            @Override
            public void mouseReleased(MouseEvent e) {
                if (dragOrigin != null) {
                    JLayeredPane layeredPane = null;
                    if (SwingUtilities.getWindowAncestor(view) != null) {
                        layeredPane = SwingUtilities.getRootPane(view).getLayeredPane();
                    }
                    
                    boolean itemPlaced = false;
                    
                    try {
                       
                        Point screenPoint = MouseInfo.getPointerInfo().getLocation();
                        Point mapPoint = parentShop.getMapPanel().getLocationOnScreen();
                        
                       
                        int x = (int) (screenPoint.getX() - mapPoint.getX());
                        int y = (int) (screenPoint.getY() - mapPoint.getY());
                        
                       
                        if (isDropLocationValid(x, y)) {
                       
                            Point worldCoords = parentShop.getMapPanel().getMapController()
                                .screenToWorldCoordinates(x, y);
                            
                       
                            if (hasEnoughFunds()) {
                                completePurchase();
                                
                       
                                parentShop.addItemToMap(view.getName(), worldCoords);
                                itemPlaced = true;
                            }
                        }
                    } catch (Exception ex) {
                        System.err.println("Error during drag and drop: " + ex.getMessage());
                        ex.printStackTrace();
                    } finally {
                        
                        if (layeredPane != null) {
                            if (ghostImage != null) {
                                ghostImage.setVisible(false);
                                layeredPane.remove(ghostImage);
                                ghostImage = null; 
                            }
                            if (messagePanel != null) {
                                messagePanel.setVisible(false);
                                layeredPane.remove(messagePanel);
                                messagePanel = null; 
                            }
                            layeredPane.repaint();
                        }
                        
                        parentShop.setVisible(true);
                        dragOrigin = null;
                        
                       
                        if (!itemPlaced && hasEnoughFunds()) {
                            if (view.getName().equals("Jeep")) {
                                JOptionPane.showMessageDialog(view, 
                                    "Cars can only be placed on road tiles.", 
                                    "Invalid Placement", 
                                    JOptionPane.WARNING_MESSAGE);
                            } else {
                                JOptionPane.showMessageDialog(view, 
                                    "Items can only be placed on sand tiles.", 
                                    "Invalid Placement", 
                                    JOptionPane.WARNING_MESSAGE);
                            }
                        }
                    }
                }
            }});
        
        view.addMouseMotionListener(new MouseMotionAdapter() {
            @Override
            public void mouseDragged(MouseEvent e) {
                if (dragOrigin != null && ghostImage != null && ghostImage.isVisible()) {
                    Point screenPoint = MouseInfo.getPointerInfo().getLocation();
                    Point mapPoint = parentShop.getMapPanel().getLocationOnScreen();
                    
                    ghostImage.setLocation(
                        screenPoint.x - ghostImage.getWidth() / 2,
                        screenPoint.y - ghostImage.getHeight() / 2
                    );
                    
                    // Update message and highlight based on drop location validity
                    int x = (int)(screenPoint.getX() - mapPoint.getX());
                    int y = (int)(screenPoint.getY() - mapPoint.getY());
                    
                    if (isDropLocationValid(x, y)) {
                        updateMessagePanel("Release to place " + view.getName(), Color.GREEN);
                    } else {
                        String message = view.getName().equals("Jeep") ? 
                            "Must place on road" : "Must place on sand";
                        updateMessagePanel(message, Color.RED);
                    }
                }
            }
        });
    }
    

    private boolean isDropLocationValid(int x, int y) {
        // Check if coordinates are within map boundaries
        if (x < 0 || x >= parentShop.getMapPanel().getWidth() || 
            y < 0 || y >= parentShop.getMapPanel().getHeight()) {
            return false;
        }
        
        // Convert screen coordinates to world coordinates
        Point worldCoords = parentShop.getMapPanel().getMapController()
            .screenToWorldCoordinates(x, y);
        
        // Calculate tile coordinates
        int tileSize = parentShop.getMapPanel().getTitleSize();
        int tileX = (int)(worldCoords.x / tileSize);
        int tileY = (int)(worldCoords.y / tileSize);
        
        // Check if tile coordinates are valid
        if (tileX < 0 || tileX >= parentShop.getMapPanel().getMaxWorldColumns() ||
            tileY < 0 || tileY >= parentShop.getMapPanel().getMaxWorldRows()) {
            return false;
        }
        
        // Get the type of the current tile
        int currentTileType = parentShop.getMapPanel().getMapController().getMapTile(tileX, tileY);
        
        // Get item name
        String itemName = view.getName();
        
        // Check if item is a car
        if (itemName.equals("Jeep")) {
            // Cars can only be placed on roads (assuming road tile type is something like 1)
            // You'll need to replace '1' with your actual road tile type
            return currentTileType == 5; // Road tile type
        }
        // Check if item is terrain
        else if (itemName.equals("Tree") || itemName.equals("Bush") || 
                 itemName.equals("Pond") || itemName.equals("Road") ||
                 itemName.equals("Fence")) {
            // Terrain items can only be placed on sand
            return currentTileType == 0;
        } 
        // For all other items
        else {
            // All other items can only be placed on sand
            return currentTileType == 0;
        }
    }
    
    
    
    public void purchase() {
        if (hasEnoughFunds()) {
            completePurchase();
            
            String itemName = view.getName();
            boolean isTerrain = itemName.equals("Tree") || itemName.equals("Bush") || 
                               itemName.equals("Pond") || itemName.equals("Road") ||
                               itemName.equals("Fence");
            
            if (isTerrain) {
    
                Point validLocation = parentShop.getRandomValidLocation();
                if (validLocation != null) {
                    parentShop.addItemToMap(itemName, validLocation);
                    
                    JOptionPane.showMessageDialog(view, 
                        itemName + " placed successfully!", 
                        "Purchase Successful", 
                        JOptionPane.INFORMATION_MESSAGE);
                } else {
    
                    JOptionPane.showMessageDialog(view, 
                        "No suitable location found for " + itemName + ".", 
                        "Placement Error", 
                        JOptionPane.WARNING_MESSAGE);
                }
            } else {
    
                parentShop.addItemToMap(itemName, parentShop.getRandomValidLocation());
                
                JOptionPane.showMessageDialog(view, 
                    itemName + " purchased successfully!", 
                    "Purchase Successful", 
                    JOptionPane.INFORMATION_MESSAGE);
            }
        } else {
            JOptionPane.showMessageDialog(view, 
                "Not enough funds to purchase " + view.getName() + ".", 
                "Insufficient Funds", 
                JOptionPane.WARNING_MESSAGE);
        }
    }

    
    
    private boolean hasEnoughFunds() {
        return parentShop.getGameManager().getCurrentCapital() >= view.getPrice();
    }

    private void completePurchase() {
        // Decrease capital in game manager
        parentShop.getGameManager().decreaseCapital(view.getPrice());
        
        // Update shop panel balance display
        parentShop.updateBalanceDisplay();
        
        // Update top panel display
        parentShop.getMapPanel().getTopPanel().updateBalanceDisplay();
        
        // Check if game is over (capital <= 0)
        if (parentShop.getGameManager().getCurrentCapital() <= 0) {
            parentShop.getMapPanel().handleGameOver();
        }
    }
}