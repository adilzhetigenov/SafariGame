package View.Enities;

import Controller.ColissionController;
import Controller.JeepController;
import View.Scenes.MapPanel;
import java.awt.Color;
import java.awt.Graphics2D;

public class JeepSprite extends EntitySprite {
    
    public JeepSprite(MapPanel map, ColissionController colCont, int x, int y) {
        super(map, new JeepController(map, colCont, x, y));
    }

    @Override
    public void draw(Graphics2D g2) {
        super.draw(g2);
        
        // Get the jeep controller
        JeepController jeepController = (JeepController) getController();
        
        // Calculate screen position
        int screenX = (int)(getController().getEntityX() - getMap().getMapController().getCameraX());
        int screenY = (int)(getController().getEntityY() - getMap().getMapController().getCameraY());
        
        // Draw passenger count
        int totalPassengers = jeepController.getTotalPassengerCount();
        String passengerText = totalPassengers + "/" + jeepController.getMaxPassengers();
        
        // Set up text properties
        g2.setColor(Color.WHITE);
        g2.setFont(g2.getFont().deriveFont(12f));
        
        // Draw background for better visibility
        g2.setColor(new Color(0, 0, 0, 150));
        g2.fillRect(screenX, screenY - 20, 40, 20);
        
        // Draw the text
        g2.setColor(Color.WHITE);
        g2.drawString(passengerText, screenX + 5, screenY - 5);
    }
} 