package Controller;

import View.Scenes.MapPanel;
import View.MapAssets.Tile;
import View.Components.CustomAnimatedButton;
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.image.BufferedImage;

public class BuildingController {
    private MapPanel mapPanel;
    private MapController mapController;
    private boolean isInBuildMode = false;
    private Point dragOrigin = null;
    private Point currentTileLocation = null;
    private int originalTileType;
    
    private JLabel ghostImage;
    private JPanel messagePanel;

    private JButton deleteButton;
    private JPanel buttonPanel;
    private Point selectedTileLocation;
    private JPanel selectedTileHighlight;
    private boolean isDeleting = false;

    private boolean isDragging = false;
    private static final int DRAG_THRESHOLD = 5; // pixels
    private Point mouseDownLocation = null;

    public BuildingController(MapPanel mapPanel) {
        this.mapPanel = mapPanel;
        this.mapController = mapPanel.getMapController();
        setupDragAndDrop();
        setupDeleteButton();
    }

    private void setupDeleteButton() {
        // Create delete button using CustomAnimatedButton with adjusted size
        deleteButton = new CustomAnimatedButton("Delete", 100, 35, () -> {
            if (selectedTileLocation != null) {
                isDeleting = true;
                mapController.setMapTile(selectedTileLocation.x, selectedTileLocation.y, 0);
                mapPanel.repaint();
                hideDeleteButton();
                isDeleting = false;
            }
        });
        
        // Adjust font size for better visibility
        deleteButton.setFont(new Font("Comic Sans MS", Font.BOLD, 14));
        deleteButton.setVisible(false);
        
        // Create highlight panel
        selectedTileHighlight = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                g.setColor(new Color(255, 255, 0, 100)); // Semi-transparent yellow
                g.fillRect(0, 0, getWidth(), getHeight());
                g.setColor(Color.YELLOW);
                g.drawRect(0, 0, getWidth() - 1, getHeight() - 1);
            }
        };
        selectedTileHighlight.setOpaque(false);
        selectedTileHighlight.setVisible(false);
        
        // Add components to layered pane when panel is ready
        mapPanel.addHierarchyListener(e -> {
            if ((e.getChangeFlags() & HierarchyEvent.SHOWING_CHANGED) != 0 && mapPanel.isShowing()) {
                if (SwingUtilities.getWindowAncestor(mapPanel) != null) {
                    JLayeredPane layeredPane = SwingUtilities.getRootPane(mapPanel).getLayeredPane();
                    layeredPane.add(deleteButton, JLayeredPane.POPUP_LAYER);
                    layeredPane.add(selectedTileHighlight, JLayeredPane.PALETTE_LAYER);
                }
            }
        });
    }

    private void showDeleteButton(int tileX, int tileY) {
        if (deleteButton != null && !isDeleting) {
            int tileSize = mapController.getTileSize();
            Point mapPoint = mapPanel.getLocationOnScreen();
            
            // Calculate tile screen position
            int tileScreenX = tileX * tileSize - (int)mapController.getCameraX();
            int tileScreenY = tileY * tileSize - (int)mapController.getCameraY();
            
            deleteButton.setSize(deleteButton.getPreferredSize());
            deleteButton.setLocation(
                (int)(mapPoint.getX() + tileScreenX + tileSize/2 - deleteButton.getWidth()/2),
                (int)(mapPoint.getY() + tileScreenY - deleteButton.getHeight() - 20) // Increased distance from tile
            );
            deleteButton.setVisible(true);
            
            // Show highlight
            selectedTileHighlight.setBounds(
                tileScreenX,
                tileScreenY,
                tileSize,
                tileSize
            );
            selectedTileHighlight.setVisible(true);
            
            deleteButton.repaint();
            selectedTileHighlight.repaint();
        }
    }

    private void hideDeleteButton() {
        if (deleteButton != null) {
            deleteButton.setVisible(false);
            selectedTileHighlight.setVisible(false);
        }
    }

    private void setupDragAndDrop() {
        mapPanel.addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                if (!isInBuildMode) {
                    return;
                }
                
                // Convert screen coordinates to tile coordinates
                Point worldCoords = mapController.screenToWorldCoordinates(e.getX(), e.getY());
                int tileX = worldCoords.x / mapController.getTileSize();
                int tileY = worldCoords.y / mapController.getTileSize();
                
                int tileType = mapController.getMapTile(tileX, tileY);
                
                // Only handle non-sand tiles
                if (tileType > 0) {
                    mouseDownLocation = e.getPoint();
                    selectedTileLocation = new Point(tileX, tileY);
                    originalTileType = tileType;
                    showDeleteButton(tileX, tileY);
                } else {
                    hideDeleteButton();
                    selectedTileLocation = null;
                    mouseDownLocation = null;
                }
            }
            
            @Override
            public void mouseReleased(MouseEvent e) {
                if (!isInBuildMode || isDeleting) {
                    return;
                }
                
                if (isDragging) {
                    Point screenPoint = MouseInfo.getPointerInfo().getLocation();
                    Point mapPoint = mapPanel.getLocationOnScreen();
                    
                    int x = (int) (screenPoint.getX() - mapPoint.getX());
                    int y = (int) (screenPoint.getY() - mapPoint.getY());
                    
                    Point worldCoords = mapController.screenToWorldCoordinates(x, y);
                    int targetX = worldCoords.x / mapController.getTileSize();
                    int targetY = worldCoords.y / mapController.getTileSize();
                    
                    if (isValidDropLocation(targetX, targetY)) {
                        // Move tile to new location
                        mapController.setMapTile(targetX, targetY, originalTileType);
                        // Set original location to sand (type 0)
                        mapController.setMapTile(selectedTileLocation.x, selectedTileLocation.y, 0);
                        mapPanel.repaint();
                    }
                    
                    // Cleanup
                    if (SwingUtilities.getWindowAncestor(mapPanel) != null) {
                        JLayeredPane layeredPane = SwingUtilities.getRootPane(mapPanel).getLayeredPane();
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
                }
                
                isDragging = false;
                mouseDownLocation = null;
                dragOrigin = null;
            }
        });
        
        mapPanel.addMouseMotionListener(new MouseMotionAdapter() {
            @Override
            public void mouseDragged(MouseEvent e) {
                if (!isInBuildMode || mouseDownLocation == null || isDeleting) {
                    return;
                }
                
                // Calculate distance moved
                int deltaX = Math.abs(e.getX() - mouseDownLocation.x);
                int deltaY = Math.abs(e.getY() - mouseDownLocation.y);
                double distance = Math.sqrt(deltaX * deltaX + deltaY * deltaY);
                
                // If we haven't started dragging yet, check if we should start
                if (!isDragging && distance > DRAG_THRESHOLD) {
                    isDragging = true;
                    hideDeleteButton();
                    dragOrigin = mouseDownLocation;
                    
                    if (SwingUtilities.getWindowAncestor(mapPanel) != null) {
                        JLayeredPane layeredPane = SwingUtilities.getRootPane(mapPanel).getLayeredPane();
                        
                        // Create ghost image
                        if (ghostImage == null) {
                            ghostImage = createGhostImage(mapController.getTilesType()[originalTileType]);
                        }
                        
                        // Create message panel
                        if (messagePanel == null) {
                            messagePanel = createMessagePanel("Drag to move tile");
                        }
                        
                        layeredPane.add(ghostImage, JLayeredPane.DRAG_LAYER);
                        layeredPane.add(messagePanel, JLayeredPane.POPUP_LAYER);
                        messagePanel.setVisible(true);
                    }
                }
                
                // Update ghost image position if we're dragging
                if (isDragging && ghostImage != null) {
                    Point screenPoint = MouseInfo.getPointerInfo().getLocation();
                    
                    ghostImage.setLocation(
                        screenPoint.x - ghostImage.getWidth() / 2,
                        screenPoint.y - ghostImage.getHeight() / 2
                    );
                    ghostImage.setVisible(true);
                    
                    // Update message based on drop location validity
                    Point mapPoint = mapPanel.getLocationOnScreen();
                    int x = (int) (screenPoint.getX() - mapPoint.getX());
                    int y = (int) (screenPoint.getY() - mapPoint.getY());
                    
                    Point worldCoords = mapController.screenToWorldCoordinates(x, y);
                    int targetX = worldCoords.x / mapController.getTileSize();
                    int targetY = worldCoords.y / mapController.getTileSize();
                    
                    if (isValidDropLocation(targetX, targetY)) {
                        updateMessagePanel("Release to place tile", Color.GREEN);
                    } else {
                        updateMessagePanel("Cannot place here", Color.RED);
                    }
                }
            }
        });
    }

    private JLabel createGhostImage(Tile tile) {
        BufferedImage tileImage = tile.getImage();
        if (tileImage == null) return null;

        // Create a larger version of the image (1.5x size)
        int newWidth = (int)(tileImage.getWidth() * 1.5);
        int newHeight = (int)(tileImage.getHeight() * 1.5);
        
        // Create semi-transparent version at larger size
        BufferedImage ghost = new BufferedImage(
            newWidth, 
            newHeight, 
            BufferedImage.TYPE_INT_ARGB
        );
        
        Graphics2D g2d = ghost.createGraphics();
        g2d.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.6f));
        g2d.drawImage(tileImage, 0, 0, newWidth, newHeight, null);
        g2d.dispose();

        JLabel label = new JLabel(new ImageIcon(ghost));
        label.setSize(newWidth, newHeight);
        return label;
    }

    private JPanel createMessagePanel(String message) {
        JPanel panel = new JPanel();
        panel.setBackground(new Color(0, 0, 0, 180));
        panel.setBorder(BorderFactory.createEmptyBorder(5, 10, 5, 10));
        
        JLabel label = new JLabel(message);
        label.setForeground(Color.WHITE);
        label.setFont(new Font("Arial", Font.BOLD, 12));
        
        panel.add(label);
        return panel;
    }

    private void updateMessagePanel(String message, Color textColor) {
        if (messagePanel != null) {
            JLabel label = (JLabel) messagePanel.getComponent(0);
            label.setText(message);
            label.setForeground(textColor);
            
            // Get current mouse position
            Point screenPoint = MouseInfo.getPointerInfo().getLocation();
            Point mapPoint = mapPanel.getLocationOnScreen();
            
            // Position message panel above the cursor
            messagePanel.setSize(messagePanel.getPreferredSize());
            messagePanel.setLocation(
                (int)(screenPoint.getX() - mapPoint.getX() - messagePanel.getWidth() / 2),
                (int)(screenPoint.getY() - mapPoint.getY() - messagePanel.getHeight() - 20)
            );
            
            // Get tile coordinates
            int x = (int)(screenPoint.getX() - mapPoint.getX());
            int y = (int)(screenPoint.getY() - mapPoint.getY());
            Point worldCoords = mapController.screenToWorldCoordinates(x, y);
            int targetX = worldCoords.x / mapController.getTileSize();
            int targetY = worldCoords.y / mapController.getTileSize();
            
            // Create highlight overlay
            if (isValidDropLocation(targetX, targetY)) {
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
        int tileSize = mapController.getTileSize();
        int tileX = (screenX / tileSize) * tileSize;
        int tileY = (screenY / tileSize) * tileSize;
        
        // Position the highlight panel
        highlightPanel.setBounds(tileX, tileY, tileSize, tileSize);
        
        // Add to layered pane
        if (SwingUtilities.getWindowAncestor(mapPanel) != null) {
            JLayeredPane layeredPane = SwingUtilities.getRootPane(mapPanel).getLayeredPane();
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

    private boolean isValidDropLocation(int x, int y) {
        // Check if coordinates are within map bounds
        if (x < 0 || y < 0 || x >= mapController.getMaxWorldColumns() || y >= mapController.getMaxWorldRows()) {
            return false;
        }
        
        // Get the tile at the target location
        int tileType = mapController.getMapTile(x, y);
        
        // Check if target is sand (type 0)
        return tileType == 0;
    }

    public void toggleBuildMode() {
        isInBuildMode = !isInBuildMode;
        System.out.println("Build mode: " + (isInBuildMode ? "ON" : "OFF"));
    }

    public boolean isInBuildMode() {
        return isInBuildMode;
    }
} 