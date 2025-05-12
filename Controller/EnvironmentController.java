package Controller;

import java.awt.AlphaComposite;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.util.List;

import View.Enities.EntitySprite;
import View.Enities.RangerSprite;

public class EnvironmentController {
    private final int screenWidth;
    private final int screenHeight;
    private boolean isNightMode = true; // We can add day/night cycle later
    private int lightRadius = 200;
    private int darknessLevel = 200;

    public enum DayNightState {
        DAY, SUNSET, NIGHT, DAWN
    }
    private DayNightState state = DayNightState.DAY;
    private float transitionProgress = 0f; // 0.0 to 1.0
    private int dayDarkness = 0;
    private int nightDarkness = 255;
    private int sunsetDarkness = 150;
    private int dawnDarkness = 150;
    
    // Duration constants (in frames at 60 FPS)
    private final int DAY_DURATION = 3600;       // 1 minute
    private final int NIGHT_DURATION = 18000;    // 5 minutes
    private final int TRANSITION_DURATION = 7200; // 2 minutes for sunset/dawn
    
    private int transitionCounter = 0;

    public EnvironmentController(int screenWidth, int screenHeight) {
        this.screenWidth = screenWidth;
        this.screenHeight = screenHeight;
    }

    public void drawEnvironment(Graphics2D g2, List<EntitySprite> entities) {
        if (!isNightMode) return; // Skip if not in night mode

        // Create a dark overlay with light circles around rangers
        BufferedImage overlay = new BufferedImage(screenWidth, screenHeight, BufferedImage.TYPE_INT_ARGB);
        Graphics2D overlayG2 = overlay.createGraphics();
        
        // Fill the entire overlay with dark color
        overlayG2.setColor(new Color(0, 0, 0, darknessLevel));
        overlayG2.fillRect(0, 0, screenWidth, screenHeight);
        
        // Set up the composite for creating transparent holes
        overlayG2.setComposite(AlphaComposite.getInstance(AlphaComposite.CLEAR));
        
        // First pass: Clear all light circles completely
        if(isNightMode)
        {
            for (EntitySprite entity : entities) {
                if (entity instanceof RangerSprite) {
                    int centerX = (int) entity.getX();
                int centerY = (int) entity.getY();
                
                // Clear the entire circle
                overlayG2.setColor(new Color(0, 0, 0, 0));
                overlayG2.fillOval(centerX - lightRadius, centerY - lightRadius, lightRadius * 2, lightRadius * 2);
            }}
        }
        

        
        // Draw the overlay onto the main graphics
        g2.drawImage(overlay, 0, 0, null);
        overlayG2.dispose();
    }

    public void setNightMode(boolean isNightMode) {
        this.isNightMode = isNightMode;
    }

    public boolean isNightMode() {
        return isNightMode;
    }

    public void setLightRadius(int radius) {
        this.lightRadius = radius;
    }

    public void setDarknessLevel(int level) {
        this.darknessLevel = level;
    }

    public void updateCycle() {
        switch (state) {
            case DAY -> {
                if (++transitionCounter > DAY_DURATION) {
                    state = DayNightState.SUNSET;
                    transitionCounter = 0;
                    transitionProgress = 0f;
                }
            }
            case SUNSET -> {
                transitionProgress += 1.0f / TRANSITION_DURATION;
                darknessLevel = (int)(dayDarkness + (sunsetDarkness - dayDarkness) * transitionProgress);
                if (transitionProgress >= 1.0f) {
                    state = DayNightState.NIGHT;
                    transitionCounter = 0;
                    transitionProgress = 0f;
                }
            }
            case NIGHT -> {
                if (++transitionCounter > NIGHT_DURATION) {
                    state = DayNightState.DAWN;
                    transitionCounter = 0;
                    transitionProgress = 0f;
                }
            }
            case DAWN -> {
                transitionProgress += 1.0f / TRANSITION_DURATION;
                darknessLevel = (int)(nightDarkness + (dayDarkness - nightDarkness) * transitionProgress);
                if (transitionProgress >= 1.0f) {
                    state = DayNightState.DAY;
                    transitionCounter = 0;
                    transitionProgress = 0f;
                }
            }
        }
        isNightMode = (state != DayNightState.DAY);
    }
} 