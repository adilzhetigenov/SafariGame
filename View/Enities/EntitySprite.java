package View.Enities;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.Stroke;

import javax.swing.JComponent;

import Controller.AnimalController;
import Controller.EntityController;
import Controller.MapController;
import View.Scenes.MapPanel;

public class EntitySprite extends JComponent {
    private MapPanel map;
    private EntityController controller;
    private boolean visible;

    public EntitySprite(MapPanel map, EntityController controller) {
        this.map = map;
        this.controller = controller;
        this.visible = true;
        setBounds(0, 0, map.getTitleSize(), map.getTitleSize());
    }

    public void update() {
        controller.update();
    }

    public void draw(Graphics2D g2) {
        if (visible) {
            // Skip drawing if entity is not visible
            if (!controller.isVisible()) {
                return;
            }

            MapController mapController = map.getMapController();
            double cameraX = mapController.getCameraX();
            double cameraY = mapController.getCameraY();
            
            int screenX = (int)(controller.getEntityX() - cameraX);
            int screenY = (int)(controller.getEntityY() - cameraY);
            
            // Update component bounds for mouse interaction
            setBounds(screenX, screenY, map.getTitleSize(), map.getTitleSize());
            
            // Draw the entity image
            g2.drawImage(controller.getCurrentImage(), screenX, screenY, map.getTitleSize(), map.getTitleSize(), null);
            
            // Draw selection indicator if entity is selected
            if (controller.isSelected()) {
                // Save the current stroke
                Stroke originalStroke = g2.getStroke();
                // Set a thicker stroke for the selection indicator
                g2.setStroke(new BasicStroke(3));
                // Draw a yellow rectangle around the entity
                g2.setColor(new Color(255, 255, 0, 180));
                g2.drawRect(screenX - 2, screenY - 2, map.getTitleSize() + 4, map.getTitleSize() + 4);
                // Restore the original stroke
                g2.setStroke(originalStroke);
            }
            
            // Draw state bars for animals
            if (controller instanceof AnimalController) {
                AnimalController animalController = (AnimalController) controller;
                
                if (animalController.isSteak()) {
                    // Only draw expiration bar for steaks
                    drawExpirationBar(g2, screenX, screenY);
                } else {
                    // Draw all bars for living animals
                    drawHealthBar(g2, screenX, screenY);
                    drawThirstBar(g2, screenX, screenY);
                    drawHungerBar(g2, screenX, screenY);
                }
            }
        }
    }

    private void drawHealthBar(Graphics2D g2, int screenX, int screenY) {
        AnimalController animalController = (AnimalController) controller;
        int healthPercent = (int)((animalController.getCurrentHealth() * 100.0) / animalController.getMaxHealth());
        
        g2.setColor(Color.RED);
        g2.fillRect(screenX, screenY - 10, map.getTitleSize(), 5);
        g2.setColor(Color.YELLOW);
        g2.fillRect(screenX, screenY - 10, (int)(map.getTitleSize() * animalController.getCurrentHealth() / animalController.getMaxHealth()), 5);
        g2.setColor(Color.WHITE);
        g2.setFont(new Font("Arial", Font.BOLD, 10));
        g2.drawString(healthPercent + "%", screenX + map.getTitleSize() + 5, screenY - 5);
    }

    private void drawThirstBar(Graphics2D g2, int screenX, int screenY) {
        AnimalController animalController = (AnimalController) controller;
        int thirstPercent = (int)((animalController.getCurrentThirst() * 100.0) / animalController.getMaxThirst());
        
        g2.setColor(Color.RED);
        g2.fillRect(screenX, screenY - 15, map.getTitleSize(), 5);
        g2.setColor(Color.BLUE);
        g2.fillRect(screenX, screenY - 15, (int)(map.getTitleSize() * animalController.getCurrentThirst() / animalController.getMaxThirst()), 5);
        g2.setColor(Color.WHITE);
        g2.drawString(thirstPercent + "%", screenX + map.getTitleSize() + 5, screenY - 10);
    }

    private void drawHungerBar(Graphics2D g2, int screenX, int screenY) {
        AnimalController animalController = (AnimalController) controller;
        int hungerPercent = (int)((animalController.getCurrentHunger() * 100.0) / animalController.getMaxHunger());
        
        g2.setColor(Color.RED);
        g2.fillRect(screenX, screenY - 20, map.getTitleSize(), 5);
        g2.setColor(Color.GREEN);
        g2.fillRect(screenX, screenY - 20, (int)(map.getTitleSize() * animalController.getCurrentHunger() / animalController.getMaxHunger()), 5);
        g2.setColor(Color.WHITE);
        g2.drawString(hungerPercent + "%", screenX + map.getTitleSize() + 5, screenY - 15);
    }

    private void drawExpirationBar(Graphics2D g2, int screenX, int screenY) {
        AnimalController animalController = (AnimalController) controller;
        int expirationPercent = (int)((animalController.getSteakExpirationCounter() * 100.0) / animalController.getSteakExpirationTime());
        
        g2.setColor(Color.RED);
        g2.fillRect(screenX, screenY - 20, map.getTitleSize(), 5);
        g2.setColor(Color.ORANGE);
        g2.fillRect(screenX, screenY - 20, (int)(map.getTitleSize() * (1 - expirationPercent / 100.0)), 5);
        g2.setColor(Color.WHITE);
        g2.drawString(expirationPercent + "%", screenX + map.getTitleSize() + 5, screenY - 15);
    }

    public EntityController getController() {
        return controller;
    }

    public MapPanel getMap() {
        return map;
    }

    public boolean isVisible() {
        return visible;
    }

    public void setVisible(boolean visible) {
        this.visible = visible;
    }

    public double getWorldX() {
        return controller.getEntityX();
    }

    public double getWorldY() {
        return controller.getEntityY();
    }
} 