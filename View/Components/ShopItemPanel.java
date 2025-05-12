package View.Components;

import javax.swing.*;

import Controller.ShopItemController;

import java.awt.*;
import java.awt.event.*;

/**
 * Represents the UI component for an item in the shop that can be purchased.
 * Responsible for displaying item information and visual elements.
 */
public class ShopItemPanel extends JPanel {
    private String name;
    private double price;
    private String imagePath;
    private JLabel imageLabel;
    private JLabel nameLabel;
    private JLabel priceLabel;
    private JButton buyButton;
    private ImageIcon icon;
    private ShopPanel parentShop;
    private ShopItemController controller;
    
    /**
     * Creates a new shop item panel with the specified properties.
     * 
     * @param name The name of the item
     * @param price The price of the item
     * @param imagePath Path to the item's image
     * @param parentShop Reference to the parent shop panel
     */
    public ShopItemPanel(String name, double price, String imagePath, ShopPanel parentShop) {
        this.name = name;
        this.price = price;
        this.imagePath = imagePath;
        this.parentShop = parentShop;
        
        // Configure panel properties
        setLayout(new BorderLayout());
        setBackground(Color.WHITE);
        setBorder(BorderFactory.createLineBorder(new Color(85, 89, 67), 1));
        
        // Initialize UI components
        initComponents();
        
        // Create controller and connect it to this view
        this.controller = new ShopItemController(this, parentShop);
    }
    
    /**
     * Initialize and arrange all UI components for this shop item.
     */
    private void initComponents() {
        // Create image
        icon = new ImageIcon(imagePath);
        Image img = icon.getImage().getScaledInstance(64, 64, Image.SCALE_SMOOTH);
        imageLabel = new JLabel(new ImageIcon(img));
        imageLabel.setHorizontalAlignment(JLabel.CENTER);
        add(imageLabel, BorderLayout.CENTER);
        
        // Item info panel (name and price)
        JPanel infoPanel = new JPanel(new GridLayout(2, 1));
        infoPanel.setOpaque(false);
        
        nameLabel = new JLabel(name);
        nameLabel.setHorizontalAlignment(JLabel.CENTER);
        nameLabel.setFont(new Font("Arial", Font.BOLD, 12));
        
        priceLabel = new JLabel("$" + price);
        priceLabel.setHorizontalAlignment(JLabel.CENTER);
        priceLabel.setFont(new Font("Arial", Font.PLAIN, 12));
        
        infoPanel.add(nameLabel);
        infoPanel.add(priceLabel);
        add(infoPanel, BorderLayout.SOUTH);
        
        // Add buy button
        buyButton = new JButton("Buy");
        buyButton.setFocusPainted(false);
        buyButton.addActionListener(e -> controller.purchase());
        add(buyButton, BorderLayout.NORTH);
    }
    
    /**
     * Setup drag-and-drop functionality for this shop item.
     */
    public void makeDraggable() {
        controller.setupDragAndDrop();
    }
    
    // Getters and setters
    public String getName() {
        return name;
    }
    
    public double getPrice() {
        return price;
    }
    
    public String getImagePath() {
        return imagePath;
    }
    
    public ImageIcon getIcon() {
        return icon;
    }
    
    public JLabel getImageLabel() {
        return imageLabel;
    }
    
    public ShopPanel getParentShop() {
        return parentShop;
    }
    
    public JLabel getGhostImage(boolean create) {
        JLabel ghostImage = new JLabel();
        ghostImage.setIcon(imageLabel.getIcon());
        ghostImage.setSize(imageLabel.getSize());
        return ghostImage;
    }
    
    public JPanel getMessagePanel(boolean create) {
        JPanel messagePanel = new JPanel();
        messagePanel.setBackground(new Color(0, 0, 0, 180));
        messagePanel.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20));
        JLabel messageLabel = new JLabel("Place the Entity");
        messageLabel.setForeground(Color.WHITE);
        messageLabel.setFont(new Font("Arial", Font.BOLD, 18));
        messagePanel.add(messageLabel);
        return messagePanel;
    }
}