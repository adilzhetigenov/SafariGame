package View.Scenes;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Toolkit;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionAdapter;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

import javax.swing.JPanel;

import Controller.ColissionController;
import Controller.EntityController;
import Controller.EnvironmentController;
import Controller.GameManagerController;
import Controller.KeyInputController;
import Controller.MapController;
import Controller.MiniMapController;
import Model.GameManager;
import Model.ShopManager;
import View.Components.MapBottomPanel;
import View.Components.MapTopPanel;
import View.Components.PauseMenuPanel;
import View.Enities.EntitySprite;
import View.Enities.LionSprite;
import View.Enities.PoacherSprite;
import View.Enities.RangerSprite;
import View.GameFrame;
import View.MapAssets.Tile;


public class MapPanel extends JPanel implements Runnable, MouseListener {

    private final int originalTitleSize = 16; // 16x16 per sprite
    private final int scale = 3;
    private final int titleSize = originalTitleSize * scale; // 48x48
    private int maxScreenCol = 44; // Max number of tiles on camera
    private int maxScreenRow = 22; // Max number of tiles on camera
    private final int maxWorldCol = 88; // Max number of tiles on the map
    private final int maxWorldRow = 56; // Max number of tiles on the map
    private final int screenWidth = titleSize * maxScreenCol;
    private final int screenHeight = titleSize * maxScreenRow;
    private final int FPS = 60;
    private final String mapPath = "/View/Maps/Default.txt";
    private final int edgeThreshold = 70;
    private final double cameraSpeed = 10.0;
    public final int maxMap = 10;
    public final int currentMap = 0;

    private Thread gameThread;
    private KeyInputController keyController = new KeyInputController(this);
    private MapController mapController = new MapController(this, mapPath);
    private MiniMapController miniMapController = new MiniMapController(this, mapController);
    private GameManagerController ui = new GameManagerController(this);
    private ShopManager shopManager = new ShopManager();
    private GameManager gameManager = new GameManager(null,10, 1000, null);
    private MapBottomPanel bottomPanelUI = new MapBottomPanel(this, ui, gameManager, shopManager, keyController);
    private ColissionController colissionCont = new ColissionController(this);
    private PauseMenuPanel pauseMenu = new PauseMenuPanel(ui);
    private MapTopPanel topPanelUI = new MapTopPanel(this, ui, gameManager, pauseMenu, keyController);

    private boolean nearLeftEdge = false;
    private boolean nearRightEdge = false;
    private boolean nearTopEdge = false;
    private boolean nearBottomEdge = false;
    private GameFrame frame;

    // List to store all entities
    private List<EntitySprite> entities = Collections.synchronizedList(new ArrayList<>());
    private Queue<Runnable> pendingOperations = new ConcurrentLinkedQueue<>();
    
    // Player entities (kept separate for easy access)
    
    
    private final int TILE_SIZE = titleSize; // Use the existing titleSize constant
    private BufferedImage[] tileImages; // Array to store tile images
    private EnvironmentController environmentController;

    public MapPanel(GameFrame frame) {
        this.frame = frame;
        this.setPreferredSize(new Dimension(screenWidth, screenHeight));
        // better rendering performance
        this.setDoubleBuffered(true);
        addKeyListener(keyController);
        addBottomPanel();
    
        // Initialize top panel UI
        addTopPannel();

        // Initialize environment controller
        environmentController = new EnvironmentController(screenWidth, screenHeight);

        // Remove redundant tile image initialization since it's handled by MapController
        addInitialEntities();
        
        miniMapController.createMiniMap();
        
        // Add mouse listener for entity selection
        addMouseListener(this);

        // Initialize pause menu position
        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        int x = (int) (screenSize.getWidth() - 550) / 2; 
        int y = (int) (screenSize.getHeight() - 500) / 2; 
        pauseMenu.setBounds(x, y, 550, 500);
        this.add(pauseMenu);
    }

    public void addBottomPanel() {
        this.setLayout(null);
        bottomPanelUI.setBounds(770, 900, 320, 100);
        this.add(bottomPanelUI);
    }
    public void addTopPannel() {
        this.setLayout(null);
        topPanelUI.setBounds(25,25,400,80);
        this.add(topPanelUI);
    
    }
    //for the shop
    public ColissionController getColissionCont() {
        return colissionCont;
    }
    
    public KeyInputController getKeyController() {
        return keyController;
    }

    public void zoomInOut() {
    }

    public void StartGameThread() {
        gameThread = new Thread(this);
        gameThread.start();
        this.requestFocusInWindow();
        this.requestFocus();
        addMouseMotionListener(new MouseMotionAdapter() {
            @Override
            public void mouseMoved(MouseEvent e) {
                mouseMovementHandler(e.getX(), e.getY());
            }
        });
        setFocusable(true);
    }

    // This is the game loop itself, most importat method
    // Updates and repaints everything evey 0.01666 seconds
    @Override
    public void run() {
        double drawInterval = 1_000_000_000.0 / FPS; // 0.01666... seconds
        double nextDrawTime = System.nanoTime() + drawInterval;

        long timer = System.currentTimeMillis();
        int frameCount = 0;

        while (gameThread != null) {
            update();
            repaint();
            frameCount++;

            try {
                double remainingTime = nextDrawTime - System.nanoTime();
                remainingTime = remainingTime / 1_000_000;

                if (remainingTime < 0) {
                    remainingTime = 0;
                }

                Thread.sleep((long) remainingTime);
                nextDrawTime += drawInterval;

            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            // Print FPS every second
            if (System.currentTimeMillis() - timer >= 1000) {
                System.out.println("FPS: " + frameCount);
                frameCount = 0;
                timer += 1000;
            }
        }

        System.out.println("Loop exited");
    }

    // This method detecs if the mouse is hovering around the edges so it can move
    // to a certain direction
    private void mouseMovementHandler(int mouseX, int mouseY) {
        // Reset edge flags
        nearLeftEdge = false;
        nearRightEdge = false;
        nearTopEdge = false;
        nearBottomEdge = false;

        // This getters obtain the width and the height of the current panel
        int currentWidth = getWidth();
        int currentHeight = getHeight();

        if (mouseX < edgeThreshold) {
            nearLeftEdge = true;
        } else if (mouseX > currentWidth - edgeThreshold) {
            nearRightEdge = true;
        }
        if (mouseY < edgeThreshold) {
            nearTopEdge = true;
        } else if (mouseY > currentHeight - edgeThreshold) {
            nearBottomEdge = true;
        }
    }

public void update() {
    if (ui.isPaused()) return;

    // Check if game is over (capital <= 0)
    if (gameManager.getCurrentCapital() <= 0) {
        handleGameOver();
        return;
    }

    // Process any pending operations first
    while (!pendingOperations.isEmpty()) {
        pendingOperations.poll().run();
    }

    // Create a list to store entities that need to be removed
    List<EntitySprite> entitiesToRemove = new ArrayList<>();

    synchronized (entities) {
        for (EntitySprite entity : entities) {
            entity.update();
            // Check if the entity has been marked as removed
            if (entity.getController().getEntity().isRemoved()) {
                entitiesToRemove.add(entity);
            }
        }
    }

    // Remove all entities that have been marked as removed
    for (EntitySprite entity : entitiesToRemove) {
        removeEntity(entity);
    }

    double deltaX = 0, deltaY = 0;

    if (nearLeftEdge) deltaX = -cameraSpeed;
    else if (nearRightEdge) deltaX = cameraSpeed;

    if (nearTopEdge) deltaY = -cameraSpeed;
    else if (nearBottomEdge) deltaY = cameraSpeed;

    // Update camera position
    mapController.updateCamera(deltaX, deltaY);

    environmentController.updateCycle();
}

    // Handle game over condition
    public void handleGameOver() {
        // Stop the game thread
        if (gameThread != null) {
            gameThread.interrupt();
            gameThread = null;
        }
        
        // Show game over message
        javax.swing.JOptionPane.showMessageDialog(this, 
            "Game Over! You've run out of capital.", 
            "Game Over", 
            javax.swing.JOptionPane.INFORMATION_MESSAGE);
        
        // Reset game state
        resetGameState();
        
        // Return to main menu
        frame.changePanel("MainMenu");
    }

    // Getter and setter for game thread
    public Thread getGameThread() {
        return gameThread;
    }

    public void setGameThread(Thread thread) {
        this.gameThread = thread;
    }

    // Reset the game state
    public void resetGameState() {
        // Clear all entities
        clearEntities();
        
        // Reset the map
        mapController.resetMap();
        
        // Reset the game manager
        gameManager.resetGame();
        
        // Reset the camera position
        mapController.resetCamera();
        
        // Reset the UI
        updateDisplay();
        
        // Reset the game state
        ui.resumeGame();
    }

    // MAIN METHOD TO DRAW THINGS DRURING THE GAME
    @Override
    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2 = (Graphics2D) g;

        // Draw map
        mapController.draw(g2);

        // Draw all entities
        for (EntitySprite entity : entities) {
            entity.draw(g2);
        }

        // Draw environment effects (darkness and lighting)
        environmentController.drawEnvironment(g2, entities);

        if (topPanelUI != null) {
            topPanelUI.paintComponent(g2);
        }

        // Draw the minimap
        miniMapController.drawMiniMap(g2);
        
        // diposing graphics that are not longer being used anymore
        super.paintChildren(g);
        g2.dispose();

        if (ui.isPaused()) {
            g2.setColor(new Color(0, 0, 0, 250)); // Semi-transparent black overlay
            g2.fillRect(0, 0, getWidth(), getHeight());
    
            g2.setColor(Color.WHITE);
            g2.setFont(new Font(ui.getFont(), Font.BOLD, ui.getFontSize()));
            g2.drawString("Game Paused", getWidth() / 2 - 100, getHeight() / 2);
        }
    }

    public int getTitleSize() {
        return titleSize;
    }

    public int getMaxWorldColumns() {
        return maxWorldCol;
    }

    public int getMaxWorldRows() {
        return maxWorldRow;
    }

    public int getTileSize() {
        return TILE_SIZE;
    }

    public MapController getMapController() {
        return mapController;
    }

    public void addInitialEntities() {
        addEntity(new RangerSprite(this, "Ranger", keyController, colissionCont, 1700, 1500));
        addEntity(new PoacherSprite(this, "Poacher", colissionCont, 1500, 1200));
        addEntity(new LionSprite(this, colissionCont, 1800, 1500));
        addEntity(new LionSprite(this, colissionCont, 1900, 1200));
        addEntity(new LionSprite(this, colissionCont, 2000, 1500));
        addEntity(new LionSprite(this, colissionCont, 2100, 1500));
    }

    public void addEntity(EntitySprite entity) {
        pendingOperations.add(() -> {
            entities.add(entity);
            add(entity); // Add the entity as a component to the panel
        });
    }

    public void removeEntity(EntitySprite entity) {
        pendingOperations.add(() -> {
            entities.remove(entity);
            remove(entity); // Remove the entity component from the panel
        });
    }

    public List<EntitySprite> getEntities() {
        return new ArrayList<>(entities); // Return a copy to prevent concurrent modification
    }

    public void clearEntities() {
        entities.clear();
    }

    public PauseMenuPanel getPauseMenu() {
        return pauseMenu;
    }
    
    public GameManagerController getGameManagerController() {
        return ui;
    }
    
    public GameManager getGameManager() {
        return gameManager;
    }
    
    public MiniMapController getMiniMapController() {
        return miniMapController;
    }

    public void updateDisplay() {
        repaint();
    }

    // MouseListener implementation
    @Override
    public void mouseClicked(MouseEvent e) {
        // Not used
    }
    
    @Override
    public void mousePressed(MouseEvent e) {
        // Not used
    }
    
    @Override
    public void mouseReleased(MouseEvent e) {
        // Not used
    }
    
    @Override
    public void mouseEntered(MouseEvent e) {
        // Not used
    }
    
    @Override
    public void mouseExited(MouseEvent e) {
        // Not used
    }

    public int getMapTileNum(int col, int row) {
        return mapController.getMapTile(col, row);
    }

    public Tile[] getTilesType() {
        return mapController.getTilesType();
    }

    public void setTileImage(int col, int row, int tileType) {
        mapController.setMapTile(col, row, tileType);
    }

    public MapTopPanel getTopPanel() {
        return topPanelUI;
    }

    public void deselectAllEntitiesExcept(EntityController controller) {
        synchronized (entities) {
            for (EntitySprite entity : entities) {
                if (entity.getController() != controller) {
                    entity.getController().setSelected(false);
                }
            }
        }
    }

}
