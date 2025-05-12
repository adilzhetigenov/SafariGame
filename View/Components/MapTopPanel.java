package View.Components;

import Controller.GameManagerController;
import Controller.KeyInputController;
import Model.GameManager;
import Model.ShopManager;
import View.Scenes.MapPanel;
import java.awt.*;
import java.text.SimpleDateFormat;
import java.util.Date;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
public class MapTopPanel extends JPanel {
    private MapPanel map;
    private GameManagerController ui;

    private Color backgroundColor = new Color(112, 117, 90);
    private Color borderColor = new Color(85, 89, 67);

    private JPanel contentPanel = new JPanel();
    private int width = 600;
    private int height = 80;
    private int buttonWidth = 48;
    private int buttonHeight = 48;
    private int cornerRadius = 15;
    private int buttonSpacing = 20;

    private int customerCount = 10;
    private double currentBalance = 100.0;
    private Date currentDate = new Date();

    private ImageIcon menuImage = new ImageIcon("View/Assets/menu.png");
    private ImageIcon customerImage = new ImageIcon("View/Assets/person.png");
    private ImageIcon capitalImage = new ImageIcon("View/Assets/money.png");
    private ImageIcon dateImage = new ImageIcon("View/Assets/calendar.png");

    public JLabel balanceLabel;
    public JLabel customerLabel;
    public JLabel dateLabel;

    private ShopPanel shopPanel;
    private GameManager gameManager;
    private ShopManager shopManager;

    private final PauseMenuPanel pauseMenuPanel;
    private KeyInputController keyInputController;

    public MapTopPanel(MapPanel map, GameManagerController ui, GameManager gameManager, PauseMenuPanel pauseMenuPanel, KeyInputController keyInputController) {
        this.map = map;
        this.ui = ui;
        this.gameManager = gameManager;
        this.pauseMenuPanel = pauseMenuPanel;
        this.keyInputController = keyInputController;
        setLayout(new BorderLayout());
        setOpaque(false);
        
        balanceLabel = new JLabel(String.valueOf(gameManager.getCurrentCapital()));
        customerLabel = new JLabel(String.valueOf(gameManager.getAllCustomers()));
        SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy");
        dateLabel = new JLabel(formatter.format(currentDate));
        
        setBounds(25, 25, width, height);
        initializePanel();
        add(contentPanel, BorderLayout.CENTER);

        shopPanel = new ShopPanel(map, ui, shopManager, gameManager, keyInputController);
        shopPanel.setVisible(false);
        map.add(shopPanel);

        // Add timer for date updates (5 minutes = 300000 milliseconds)
        Timer dateTimer = new Timer(150000, e -> {
            // Update the date in game manager
            gameManager.updateGameTime();
            
            // Update the date display
            SimpleDateFormat dateFormatter = new SimpleDateFormat("dd/MM/yyyy");
            dateLabel.setText(dateFormatter.format(new Date()));
            
            // Update the panel
            repaint();
        });
        dateTimer.start();

        map.addComponentListener(new java.awt.event.ComponentAdapter() {
            @Override
            public void componentResized(java.awt.event.ComponentEvent evt) {
                setBounds(25, 25, width, height);
                map.revalidate();
                map.repaint();
            }
        });
    }

    public void initializePanel() {
        contentPanel.setLayout(new BoxLayout(contentPanel, BoxLayout.X_AXIS));
        contentPanel.setOpaque(false);
        contentPanel.setBorder(new EmptyBorder(5, 5, 5, 5));
        buttonsCreation();
    }

    public void buttonsCreation() {
        contentPanel.removeAll();
    
        addButtonWithLabel(menuImage, "", () -> {
            ui.pauseGame();              // pause the game logic
            pauseMenuPanel.showMenu();          // show the pause panel
        });
        
        // Update balance label text before creating the button
        balanceLabel.setText(String.valueOf(gameManager.getCurrentCapital()));
        addButtonWithLabel(capitalImage, balanceLabel.getText(), () -> {
            String[] options = {"Add", "Remove", "Set"};
            int choice = JOptionPane.showOptionDialog(
                    this,
                    "Choose an action for Capital:",
                    "Capital Options",
                    JOptionPane.DEFAULT_OPTION,
                    JOptionPane.INFORMATION_MESSAGE,
                    null,
                    options,
                    options[0]
            );
    
            if (choice >= 0 && choice <= 2) {
                String input = JOptionPane.showInputDialog("Enter amount:");
                if (input != null && !input.isEmpty()) {
                    try {
                        double value = Double.parseDouble(input);
                        if (choice == 0) addCapital(value);
                        else if (choice == 1) removeCapital(value);
                        else setCapital(value);
                    } catch (NumberFormatException e) {
                        JOptionPane.showMessageDialog(this, "Invalid number!");
                    }
                }
            }
        }, balanceLabel);
    
        // Update customer label text before creating the button
        customerLabel.setText(String.valueOf(gameManager.getAllCustomers()));
        addButtonWithLabel(customerImage, customerLabel.getText(), () -> {
            String[] options = {"Add", "Remove", "Set"};
            int choice = JOptionPane.showOptionDialog(
                    this,
                    "Choose an action for Customers:",
                    "Customer Options",
                    JOptionPane.DEFAULT_OPTION,
                    JOptionPane.INFORMATION_MESSAGE,
                    null,
                    options,
                    options[0]
            );
    
            if (choice >= 0 && choice <= 2) {
                String input = JOptionPane.showInputDialog("Enter amount:");
                if (input != null && !input.isEmpty()) {
                    try {
                        int value = Integer.parseInt(input);
                        if (choice == 0) addCustomer(value);
                        else if (choice == 1) removeCustomer(value);
                        else setCustomer(value);
                    } catch (NumberFormatException e) {
                        JOptionPane.showMessageDialog(this, "Invalid number!");
                    }
                }
            }
        }, customerLabel);
    
        addButtonWithLabel(dateImage, dateLabel.getText(), () -> System.out.println("Date clicked"), dateLabel);
    
        contentPanel.revalidate();
        contentPanel.repaint();
    }
    
    private void addButtonWithLabel(ImageIcon icon, String labelText, Runnable action) {
        addButtonWithLabel(icon, labelText, action, new JLabel(labelText));
    }

    private void addButtonWithLabel(ImageIcon icon, String labelText, Runnable action, JLabel label) {
        CustomAnimatedIcon button = new CustomAnimatedIcon(icon, buttonWidth, buttonHeight, action);
        button.setFocusable(false);

        label.setForeground(Color.WHITE);
        label.setHorizontalAlignment(JLabel.LEFT);
        label.setAlignmentY(Component.CENTER_ALIGNMENT);

        JPanel panel = new JPanel();
        panel.setOpaque(false);
        panel.setLayout(new BoxLayout(panel, BoxLayout.X_AXIS));
        panel.setAlignmentY(Component.CENTER_ALIGNMENT);

        button.setAlignmentY(Component.CENTER_ALIGNMENT);
        panel.add(button);
        panel.add(Box.createHorizontalStrut(5));
        panel.add(label);

        contentPanel.add(panel);
        contentPanel.add(Box.createHorizontalStrut(buttonSpacing));
    }

    @Override
    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2 = (Graphics2D) g.create();

        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        int arc = cornerRadius;
        int width = getWidth() - 1;
        int height = getHeight() - 1;

        g2.setColor(backgroundColor);
        g2.fillRoundRect(26, 26, width + 1, height + 1, arc, arc);
    }

    // === CUSTOMIZATION METHODS ===
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
        buttonsCreation();
    }

    public void updateDate(Date newDate) {
        this.currentDate = newDate;
        SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy");
        dateLabel.setText(formatter.format(newDate));
    }

    public void updateBalanceDisplay() {
        balanceLabel.setText(String.valueOf(gameManager.getCurrentCapital()));
        repaint();
    }

    public void addCapital(double amount) {
        gameManager.increaseCapital(amount);
        updateBalanceDisplay();
        map.updateDisplay();
    }
    
    public void removeCapital(double amount) {
        gameManager.decreaseCapital(amount);
        updateBalanceDisplay();
        map.updateDisplay();
    }
    
    public void setCapital(double amount) {
        gameManager.setCapital(amount);
        updateBalanceDisplay();
        map.updateDisplay();
    }
    
    public void addCustomer(int amount) {
        gameManager.increaseCustomer(amount);
        customerLabel.setText(String.valueOf(gameManager.getAllCustomers()));
        map.updateDisplay();
    }
    
    public void removeCustomer(int amount) {
        gameManager.removeCustomer(amount);
        customerLabel.setText(String.valueOf(gameManager.getAllCustomers()));
        map.updateDisplay();
    }
    
    public void setCustomer(int amount) {
        gameManager.setCustomer(amount);
        customerLabel.setText(String.valueOf(gameManager.getAllCustomers()));
        map.updateDisplay();
    }



    
}
