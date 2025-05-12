package View.Components;

import Controller.MapController;
import Controller.MiniMapController;
import View.Enities.EntitySprite;
import View.Enities.LionSprite;
import View.Enities.PoacherSprite;
import View.Enities.RangerSprite;
import View.Scenes.MapPanel;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.image.BufferedImage;


public class MiniMapView {
    private MapPanel map;
    private MiniMapController controller;
    private BufferedImage[] worldMap;
    private int miniMapWidth = 300;
    private int miniMapHeight = 192;
    private int miniMapX;
    private int miniMapY;
    private float scaleX;
    private float scaleY;

    public MiniMapView(MapPanel map, MiniMapController controller) {
        this.map = map;
        this.controller = controller;
        calculateMiniMapPosition();
    }

    private void calculateMiniMapPosition() {
        miniMapX = map.getWidth() - miniMapWidth - 20;
        miniMapY = map.getHeight() - miniMapHeight - 20;
        
        scaleX = (float) miniMapWidth / (map.getMaxWorldColumns() * map.getTitleSize());
        scaleY = (float) miniMapHeight / (map.getMaxWorldRows() * map.getTitleSize());
    }

    public void createMiniMap() {
        worldMap = new BufferedImage[map.maxMap];
        int worldMapWidth = map.getTitleSize() * map.getMaxWorldColumns();
        int worldMapHeight = map.getTitleSize() * map.getMaxWorldRows();

        for(int i = 0; i < map.maxMap; i++) {
            worldMap[i] = new BufferedImage(miniMapWidth, miniMapHeight, BufferedImage.TYPE_INT_ARGB);
            Graphics2D g2 = (Graphics2D) worldMap[i].createGraphics();

            int col = 0;
            int row = 0;

            while(col < map.getMaxWorldColumns() && row < map.getMaxWorldRows()) {
                int tileNum = map.getMapTileNum(col, row);
                int tileX = (int)(col * map.getTitleSize() * scaleX);
                int tileY = (int)(row * map.getTitleSize() * scaleY);
                int tileWidth = (int)(map.getTitleSize() * scaleX);
                int tileHeight = (int)(map.getTitleSize() * scaleY);
                
                if (tileNum >= 0 && tileNum < map.getTilesType().length) {
                    Color tileColor = getTileColor(tileNum);
                    g2.setColor(tileColor);
                    g2.fillRect(tileX, tileY, tileWidth, tileHeight);
                }
                
                col++;
                if (col >= map.getMaxWorldColumns()) {
                    col = 0;
                    row++;
                }
            }
            
            g2.dispose();
        }
    }
    
    private Color getTileColor(int tileNum) {
        switch(tileNum) {
            case 0: return new Color(255, 209, 0); // Sand
            case 1: return new Color(0, 0, 139); // Water
            case 2: return new Color(34, 139, 34); // Grass
            case 3: return new Color(128, 128, 128); // Path
            case 4: return new Color(139, 69, 19); // Dirt
            default: return Color.BLACK;
        }
    }

    public void drawMiniMap(Graphics2D g2) {
        if (!controller.isMiniMapOn() || worldMap == null || worldMap.length == 0) {
            return;
        }
        
        calculateMiniMapPosition();
        
        // Draw background
        g2.setColor(new Color(0, 0, 0, 100)); 
        g2.fillRect(miniMapX, miniMapY, miniMapWidth, miniMapHeight);
        
        // Draw border
        g2.setColor(new Color(255, 255, 255, 180)); 
        g2.drawRect(miniMapX, miniMapY, miniMapWidth, miniMapHeight);
        
        // Draw map
        if (worldMap[map.currentMap] != null) {
            java.awt.AlphaComposite alphaComposite = java.awt.AlphaComposite.getInstance(java.awt.AlphaComposite.SRC_OVER, 0.7f);
            g2.setComposite(alphaComposite);
            g2.drawImage(worldMap[map.currentMap], miniMapX, miniMapY, null);
            g2.setComposite(java.awt.AlphaComposite.getInstance(java.awt.AlphaComposite.SRC_OVER, 1.0f));
        }
        
        // Draw visible area
        MapController mapController = controller.getMapController();
        int visibleX = (int)(mapController.getCameraX() * scaleX);
        int visibleY = (int)(mapController.getCameraY() * scaleY);
        int visibleWidth = (int)(map.getWidth() * scaleX);
        int visibleHeight = (int)(map.getHeight() * scaleY);
        
        g2.setColor(new Color(255, 255, 255, 200)); 
        g2.drawRect(miniMapX + visibleX, miniMapY + visibleY, visibleWidth, visibleHeight);
        
        // Draw entities
        for (EntitySprite entity : map.getEntities()) {
            int entityX = (int)(entity.getWorldX() * scaleX);
            int entityY = (int)(entity.getWorldY() * scaleY);
            
            if (entity instanceof RangerSprite) {
                g2.setColor(new Color(0, 0, 255, 200)); // Blue for rangers
            } else if (entity instanceof PoacherSprite) {
                g2.setColor(new Color(255, 0, 0, 200)); // Red for poachers
            } else if (entity instanceof LionSprite) {
                g2.setColor(new Color(255, 165, 0, 200)); // Orange for lions
            } else {
                g2.setColor(new Color(255, 255, 255, 200)); // White for other entities
            }
            
            g2.fillOval(miniMapX + entityX - 2, miniMapY + entityY - 2, 4, 4);
        }
    }
    
    public Rectangle getMiniMapBounds() {
        return new Rectangle(miniMapX, miniMapY, miniMapWidth, miniMapHeight);
    }
    
    public int getMiniMapX() {
        return miniMapX;
    }
    
    public int getMiniMapY() {
        return miniMapY;
    }
    
    public int getMiniMapWidth() {
        return miniMapWidth;
    }
    
    public int getMiniMapHeight() {
        return miniMapHeight;
    }
} 