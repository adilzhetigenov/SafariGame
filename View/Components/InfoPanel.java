package View.Components;

import Controller.GameManagerController;
import Controller.KeyInputController;
import Model.GameManager;
import Model.ShopManager;
import View.Scenes.MapPanel;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.geom.RoundRectangle2D;
import javax.swing.*;
import javax.swing.border.EmptyBorder;

public class InfoPanel extends JPanel {
    private MapPanel mapPanel;
    private GameManagerController uiController;
    private GameManager gameManager;
    private KeyInputController keyInputController;
    
    // Panel visual properties
    private Color backgroundColor = new Color(243, 201, 105, 240);
    private Color headerColor = new Color(153, 76, 0);
    private Color borderColor = new Color(85, 89, 67);
    private int cornerRadius = 20;
    
    // UI components
    private JPanel contentPanel;
    private JButton closeButton;
    private JLabel titleLabel;
    
    public InfoPanel(MapPanel mapPanel, GameManagerController uiController, 
                    GameManager gameManager, KeyInputController keyInputController) {
        this.mapPanel = mapPanel;
        this.uiController = uiController;
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
        
        // Create content panel for statistics
        contentPanel = new JPanel();
        contentPanel.setLayout(new BoxLayout(contentPanel, BoxLayout.Y_AXIS));
        contentPanel.setOpaque(false);
        contentPanel.setBorder(new EmptyBorder(20, 20, 20, 20));
        
        // Add statistics sections
        addFinancialSection();
        addAnimalSection();
        addTouristSection();
        addTimeSection();
        
        JScrollPane scrollPane = new JScrollPane(contentPanel);
        scrollPane.setOpaque(false);
        scrollPane.getViewport().setOpaque(false);
        scrollPane.setBorder(BorderFactory.createEmptyBorder());
        
        add(scrollPane, BorderLayout.CENTER);
    }
    
    private JPanel createHeaderPanel() {
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setOpaque(false);
        headerPanel.setBorder(BorderFactory.createEmptyBorder(10, 15, 10, 15));
        
        titleLabel = new JLabel("Game Statistics");
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
    
    private void addFinancialSection() {
        JPanel section = createSectionPanel("Financial Statistics");
        
        addStatistic(section, "Current Balance", String.format("$%.2f", gameManager.getCurrentCapital()));
        addStatistic(section, "Money Spent", "$1,250.00");
        addStatistic(section, "Money Earned", "$2,500.00");
        addStatistic(section, "Income Speed", "$15.00/sec");
        addStatistic(section, "Spending Speed", "$8.00/sec");
        addStatistic(section, "Total Speed", "$23.00/sec");
            
        contentPanel.add(section);
        contentPanel.add(Box.createVerticalStrut(20));
    }
    
    private void addAnimalSection() {
        JPanel section = createSectionPanel("Animal Statistics");
        
        addStatistic(section, "Total Animals", "12");
        addStatistic(section, "Herbivores", "8");
        addStatistic(section, "Carnivores", "4");
        addStatistic(section, "Birth Rate", "5.2%");
        addStatistic(section, "Death Rate", "2.1%");
        
        contentPanel.add(section);
        contentPanel.add(Box.createVerticalStrut(20));
    }
    
    private void addTouristSection() {
        JPanel section = createSectionPanel("Tourist Statistics");
        
        addStatistic(section, "Total Tourists", "25");
        addStatistic(section, "Average Mood", "85%");
        
        contentPanel.add(section);
        contentPanel.add(Box.createVerticalStrut(20));
    }
    
    private void addTimeSection() {
        JPanel section = createSectionPanel("Time Statistics");
        
        String formattedDate = String.format("%02d/%02d/%04d", 
            gameManager.getDay(), 
            gameManager.getMonth(), 
            gameManager.getYear());
            
        addStatistic(section, "Current Date", formattedDate);
        addStatistic(section, "Game Duration", "2 days, 5 hours");
        
        contentPanel.add(section);
    }
    
    private JPanel createSectionPanel(String title) {
        JPanel section = new JPanel();
        section.setLayout(new BoxLayout(section, BoxLayout.Y_AXIS));
        section.setOpaque(false);
        section.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(153, 76, 0, 100), 1),
            BorderFactory.createEmptyBorder(10, 10, 10, 10)
        ));
        
        // Add a semi-transparent background panel
        JPanel backgroundPanel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2d = (Graphics2D) g;
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2d.setColor(new Color(255, 255, 255, 30));
                g2d.fillRoundRect(0, 0, getWidth(), getHeight(), 10, 10);
            }
        };
        backgroundPanel.setLayout(new BoxLayout(backgroundPanel, BoxLayout.Y_AXIS));
        backgroundPanel.setOpaque(false);
        
        // Create a container panel for the title to ensure consistent width
        JPanel titleContainer = new JPanel(new BorderLayout());
        titleContainer.setOpaque(false);
        titleContainer.setMaximumSize(new Dimension(Integer.MAX_VALUE, 30));
        
        JLabel titleLabel = new JLabel(title);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 16));
        titleLabel.setForeground(headerColor);
        titleLabel.setBorder(BorderFactory.createEmptyBorder(0, 0, 5, 0));
        
        titleContainer.add(titleLabel, BorderLayout.WEST);
        backgroundPanel.add(titleContainer);
        backgroundPanel.add(Box.createVerticalStrut(5));
        
        section.add(backgroundPanel);
        return section;
    }
    
    private void addStatistic(JPanel section, String label, String value) {
        JPanel statPanel = new JPanel(new BorderLayout());
        statPanel.setOpaque(false);
        statPanel.setMaximumSize(new Dimension(Integer.MAX_VALUE, 30));
        statPanel.setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, new Color(153, 76, 0, 50)));
        
        JLabel nameLabel = new JLabel(label);
        nameLabel.setFont(new Font("Arial", Font.PLAIN, 14));
        nameLabel.setForeground(new Color(85, 89, 67));
        
        JLabel valueLabel = new JLabel(value);
        valueLabel.setFont(new Font("Arial", Font.BOLD, 14));
        valueLabel.setForeground(headerColor);
        valueLabel.setHorizontalAlignment(JLabel.RIGHT);
        
        statPanel.add(nameLabel, BorderLayout.WEST);
        statPanel.add(valueLabel, BorderLayout.EAST);
        
        section.add(statPanel);
        section.add(Box.createVerticalStrut(5));
    }
    
    private void makeDraggable() {
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
    
    public void refreshStats() {
        // Update all statistics
        contentPanel.removeAll();
        addFinancialSection();
        addAnimalSection();
        addTouristSection();
        addTimeSection();
        contentPanel.revalidate();
        contentPanel.repaint();
    }
} 