package Controller;

import Model.Entity.Entity;
import View.Scenes.MapPanel;
import java.awt.image.BufferedImage;
import java.io.IOException;
import javax.imageio.ImageIO;

public class EntityController {
    protected Entity entity;
    protected MapPanel mapPanel;
    protected String[] images;
    protected BufferedImage currentImage;
    protected ColissionController colissionController;
    protected boolean isPlayable;
    protected KeyInputController keyInputController;
    protected int moveCounter = 0;
    protected int moveInterval = 60; // Frames between NPC movements
    protected String[] directions = {"up", "down", "left", "right"};
    protected int currentDirectionIndex = 0;
    protected boolean selected = false; // New property to track if entity is selected
    protected boolean visible = true; // New property to track visibility

    public EntityController(Entity entity, MapPanel mapPanel, String[] images, boolean isPlayable, 
                           ColissionController colissionController, KeyInputController keyInputController) {
        this.entity = entity;
        this.mapPanel = mapPanel;
        this.images = images;
        this.isPlayable = isPlayable;
        this.colissionController = colissionController;
        this.keyInputController = keyInputController;
        getEntityImage();
        currentImage = entity.getDown1();
    }

    public void update() {
        if (isPlayable) {
            if (selected) {
                updatePlayableEntity();
            } else {
                // When not selected but playable, update as NPC
                updateNPC();
            }
        } else {
            updateNPC();
        }
    }

    protected void updatePlayableEntity() {
        boolean keyPressed = false;

        if (keyInputController.isUpPressed()) {
            if (entity.getSpriteNum() == 1) {
                currentImage = entity.getUp1();
            }
            if (entity.getSpriteNum() == 2) {
                currentImage = entity.getUp2();
            }
            entity.setDirection("up");
            keyPressed = true;
        } else if (keyInputController.isDownPressed()) {
            if (entity.getSpriteNum() == 1) {
                currentImage = entity.getDown1();
            }
            if (entity.getSpriteNum() == 2) {
                currentImage = entity.getDown2();
            }
            entity.setDirection("down");
            keyPressed = true;
        } else if (keyInputController.isLeftPressed()) {
            if (entity.getSpriteNum() == 1) {
                currentImage = entity.getLeft1();
            }
            if (entity.getSpriteNum() == 2) {
                currentImage = entity.getLeft2();
            }
            keyPressed = true;
            entity.setDirection("left");
        } else if (keyInputController.isRightPressed()) {
            if (entity.getSpriteNum() == 1) {
                currentImage = entity.getRight1();
            }
            if (entity.getSpriteNum() == 2) {
                currentImage = entity.getRight2();
            }
            entity.setDirection("right");
            keyPressed = true;
        }

        entity.setColission(false);
        this.colissionController.checkTile(this);
        
        // Check for entity collisions
        int entityIndex = this.colissionController.checkEntity(this, mapPanel.getEntities());
        if (entityIndex != 999) {
            entity.setColission(true);
        }

        if (keyPressed) {
            if (entity.getColission() == false) {
                switch (entity.getDirection()) {
                    case "up":
                        entity.changeY(-entity.getSpeed());
                        break;
                    case "down":
                        entity.changeY(entity.getSpeed());
                        break;
                    case "right":
                        entity.changeX(entity.getSpeed());
                        break;
                    case "left":
                        entity.changeX(-entity.getSpeed());
                        break;
                }
            }

            updateSpriteAnimation();
        }
    }

    protected void updateNPC() {
        moveCounter++;
        
        if (moveCounter >= moveInterval) {
            // Change direction
            currentDirectionIndex = (currentDirectionIndex + 1) % directions.length;
            String newDirection = directions[currentDirectionIndex];
            entity.setDirection(newDirection);
            
            // Update sprite based on direction
            updateNPCSprite(newDirection);
            
            moveCounter = 0;
        }
        
        // Move in current direction
        entity.setColission(false);
        this.colissionController.checkTile(this);
        
        // Check for entity collisions
        int entityIndex = this.colissionController.checkEntity(this, mapPanel.getEntities());
        if (entityIndex != 999) {
            entity.setColission(true);
        }
        
        if (entity.getColission() == false) {
            switch (entity.getDirection()) {
                case "up":
                    entity.changeY(-entity.getSpeed());
                    break;
                case "down":
                    entity.changeY(entity.getSpeed());
                    break;
                case "right":
                    entity.changeX(entity.getSpeed());
                    break;
                case "left":
                    entity.changeX(-entity.getSpeed());
                    break;
            }
            
            // Update sprite animation
            updateSpriteAnimation();
            // Update the current sprite based on the new sprite number
            updateNPCSprite(entity.getDirection());
        } else {
            // If collision, change direction immediately
            currentDirectionIndex = (currentDirectionIndex + 1) % directions.length;
            entity.setDirection(directions[currentDirectionIndex]);
            updateNPCSprite(entity.getDirection());
        }
    }
    
    protected  void setVisibility(boolean visible) {
        this.visible = visible;
    }

    protected void updateNPCSprite(String direction) {
        switch (direction) {
            case "up":
                if (entity.getSpriteNum() == 1) {
                    currentImage = entity.getUp1();
                } else {
                    currentImage = entity.getUp2();
                }
                break;
            case "down":
                if (entity.getSpriteNum() == 1) {
                    currentImage = entity.getDown1();
                } else {
                    currentImage = entity.getDown2();
                }
                break;
            case "left":
                if (entity.getSpriteNum() == 1) {
                    currentImage = entity.getLeft1();
                } else {
                    currentImage = entity.getLeft2();
                }
                break;
            case "right":
                if (entity.getSpriteNum() == 1) {
                    currentImage = entity.getRight1();
                } else {
                    currentImage = entity.getRight2();
                }
                break;
        }
    }
    
    protected void updateSpriteAnimation() {
        entity.increaseSpriteCounter();
        if (entity.getSpriteCounter() > 10) {
            if (entity.getSpriteNum() == 1) {
                entity.setSpriteNum(2);
            } else if (entity.getSpriteNum() == 2) {
                entity.setSpriteNum(1);
            }
            entity.setSpriteCount(0);
        }
    }

    public void getEntityImage() {
        try {
            entity.setUp1(ImageIO.read(getClass().getResourceAsStream(images[0])));
            entity.setUp2(ImageIO.read(getClass().getResourceAsStream(images[1])));

            entity.setDown1(ImageIO.read(getClass().getResourceAsStream(images[2])));
            entity.setDown2(ImageIO.read(getClass().getResourceAsStream(images[3])));

            entity.setLeft1(ImageIO.read(getClass().getResourceAsStream(images[4])));
            entity.setLeft2(ImageIO.read(getClass().getResourceAsStream(images[5])));

            entity.setRight1(ImageIO.read(getClass().getResourceAsStream(images[6])));
            entity.setRight2(ImageIO.read(getClass().getResourceAsStream(images[7])));

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    
    // Getters and setters
    public Entity getEntity() {
        return entity;
    }
    
    public double getEntityX() {
        return entity.getX();
    }
    
    public double getEntityY() {
        return entity.getY();
    }
    
    public String getDirection() {
        return entity.getDirection();
    }
    
    public BufferedImage getCurrentImage() {
        return currentImage;
    }
    
    public int getSolidAreaX() {
        return entity.getSolidAreaX();
    }
    
    public int getSolidAreaY() {
        return entity.getSolidAreaY();
    }
    
    public void setSolidAreaX(int x) {
        entity.setSolidAreaX(x);
    }
    public void setSolidAreaY(int y) {
        entity.setSolidAreaY(y);
    }
    public int getSolidAreaWidth() {
        return entity.getSoliAreaWidth();
    }
    
    public int getSolidAreaHeight() {
        return entity.getSoliAreaHeight();
    }
    
    public int getSpeed() {
        return entity.getSpeed();
    }
    
    public void setColission(boolean col) {
        entity.setColission(col);
    }

    // New methods for selection
    public boolean isSelected() {
        return selected;
    }
    
    public void setSelected(boolean selected) {
        this.selected = selected;
    }

    public void setVisible(boolean visible) {
        this.visible = visible;
    }

    public boolean isVisible() {
        return visible;
    }
    
    public boolean isPlayable() {
        return isPlayable;
    }
}
