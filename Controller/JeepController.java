package Controller;

import Model.Entity.Vehicle.Jeep;
import Model.Entity.Person.Ranger;
import View.Enities.JeepSprite;
import View.Scenes.MapPanel;
import View.Enities.EntitySprite;
import View.Enities.RangerSprite;
import java.awt.Point;
import java.util.List;
import java.util.ArrayList;

public class JeepController extends EntityController {
    private Jeep jeep;
    private boolean isRented;
    private Ranger driver;
    private static final double ENTRY_DISTANCE = 50.0; // Maximum distance for entering the jeep
    private List<Ranger> rangerPassengers = new ArrayList<>();
    private static final int MAX_PASSENGERS = 4; // Maximum number of passengers (including rangers)
    private boolean wasEnterPressed = false; // Track previous enter state
    private static final double REVENUE_PER_PASSENGER = 50.0; // Revenue per passenger
    private static final int REVENUE_INTERVAL = 60; // Generate revenue every 60 frames (1 second at 60 FPS)
    private int revenueCounter = 0;
    private static final int BASE_SPEED = 2; // Base speed of the jeep
    private static final int FULL_SPEED_MULTIPLIER = 2; // Speed multiplier when full

    public JeepController(MapPanel map, ColissionController colCont, int x, int y) {
        super(new Jeep(x, y), map, getJeepImages(), false, colCont, map.getKeyController());
        this.jeep = (Jeep)getEntity();
        this.isRented = false;
        this.driver = null;
        this.jeep.setSpeed(BASE_SPEED); // Set initial speed
    }

    private static String[] getJeepImages() {
        String[] images = new String[8];
        images[0] = "/View/Assets/Jeep/jeep_up.png";
        images[1] = "/View/Assets/Jeep/jeep_up.png";
        images[2] = "/View/Assets/Jeep/jeep_down.png";
        images[3] = "/View/Assets/Jeep/jeep_down.png";
        images[4] = "/View/Assets/Jeep/jeep_left.png";
        images[5] = "/View/Assets/Jeep/jeep_left.png";
        images[6] = "/View/Assets/Jeep/jeep_right.png";
        images[7] = "/View/Assets/Jeep/jeep_right.png";
        return images;
    }

    @Override
    public void update() {
        // Only move and generate revenue when full
        if (getTotalPassengerCount() == MAX_PASSENGERS) {
            // Increase speed when full
            jeep.setSpeed(BASE_SPEED * FULL_SPEED_MULTIPLIER);
            // Auto-move when full
            handleAutoMovement();
            // Generate revenue when full
            generateRevenue();
        } else {
            // Reset to base speed when not full
            jeep.setSpeed(BASE_SPEED);
        }
        
        handlePassengerEntry();
        updateRangerVisibility();
    }

    private void handlePassengerEntry() {
        if (keyInputController != null) {
            boolean isEnterPressed = keyInputController.isEnterPressed();
            
            // Only trigger on the rising edge of the enter key (when it's first pressed)
            if (isEnterPressed && !wasEnterPressed) {
                System.out.println("Enter key pressed - attempting passenger entry");
                
                // Find nearby rangers
                List<Ranger> nearbyRangers = findNearbyRangers();
                System.out.println("Found " + nearbyRangers.size() + " nearby rangers");

                // First try to add a ranger as driver if no driver
                if (!isRented) {
                    System.out.println("Jeep not rented - looking for driver");
                    for (Ranger ranger : nearbyRangers) {
                        if (!rangerPassengers.contains(ranger)) {
                            // If this is the controlled ranger, make sure it's selected
                            EntityController rangerController = null;
                            for (EntitySprite sprite : mapPanel.getEntities()) {
                                if (sprite.getController().getEntity() == ranger) {
                                    rangerController = sprite.getController();
                                    break;
                                }
                            }
                            
                            if (rangerController != null && rangerController.isSelected()) {
                                System.out.println("Selected ranger found - attempting to make driver");
                                // This is the controlled ranger, make them the driver
                                if (rentJeep(ranger)) {
                                    System.out.println("Successfully made ranger the driver");
                                    // Deselect the ranger since they're now driving
                                    rangerController.setSelected(false);
                                    break;
                                }
                            }
                        }
                    }
                } else {
                    // Try to add rangers as passengers
                    System.out.println("Jeep is rented - looking for passengers");
                    for (Ranger ranger : nearbyRangers) {
                        if (!rangerPassengers.contains(ranger) && ranger != driver) {
                            // If this is the controlled ranger, make sure it's selected
                            EntityController rangerController = null;
                            for (EntitySprite sprite : mapPanel.getEntities()) {
                                if (sprite.getController().getEntity() == ranger) {
                                    rangerController = sprite.getController();
                                    break;
                                }
                            }
                            
                            if (rangerController != null && rangerController.isSelected()) {
                                System.out.println("Selected ranger found - attempting to add as passenger");
                                // This is the controlled ranger, add it to the jeep
                                if (addRangerPassenger(ranger)) {
                                    System.out.println("Successfully added ranger as passenger");
                                    // Deselect the ranger since it's now in the jeep
                                    rangerController.setSelected(false);
                                    break;
                                }
                            } else if (rangerController != null && !rangerController.isSelected()) {
                                // This is an NPC ranger, add it if there's space
                                if (addRangerPassenger(ranger)) {
                                    System.out.println("Successfully added NPC ranger as passenger");
                                    break;
                                }
                            }
                        }
                    }
                }
            }
            wasEnterPressed = isEnterPressed;
        }
    }

    private List<Ranger> findNearbyRangers() {
        List<Ranger> nearbyRangers = new ArrayList<>();
        for (EntitySprite sprite : mapPanel.getEntities()) {
            EntityController controller = sprite.getController();
            if (controller.getEntity() instanceof Ranger) {
                Ranger ranger = (Ranger) controller.getEntity();
                if (isNearJeep(ranger)) {
                    nearbyRangers.add(ranger);
                }
            }
        }
        return nearbyRangers;
    }

    private boolean isNearJeep(Ranger ranger) {
        double dx = ranger.getX() - jeep.getX();
        double dy = ranger.getY() - jeep.getY();
        double distance = Math.sqrt(dx * dx + dy * dy);
        System.out.println("Distance to ranger: " + distance + " (max: " + ENTRY_DISTANCE + ")");
        return distance <= ENTRY_DISTANCE;
    }

    private void updateRangerVisibility() {
        // Update visibility of ranger sprites
        for (EntitySprite sprite : mapPanel.getEntities()) {
            if (sprite instanceof RangerSprite) {
                EntityController controller = sprite.getController();
                if (controller.getEntity() instanceof Ranger) {
                    Ranger ranger = (Ranger) controller.getEntity();
                    // Hide the sprite if the ranger is in this jeep
                    controller.setVisible(!rangerPassengers.contains(ranger) && ranger != driver);
                }
            }
        }
    }

    public boolean addRangerPassenger(Ranger ranger) {
        if (getTotalPassengerCount() < MAX_PASSENGERS) {
            rangerPassengers.add(ranger);
            // Update ranger's position to match jeep
            ranger.changeX(jeep.getX() - ranger.getX());
            ranger.changeY(jeep.getY() - ranger.getY());
            return true;
        }
        return false;
    }

    public void removeRangerPassenger(Ranger ranger) {
        if (rangerPassengers.contains(ranger)) {
            rangerPassengers.remove(ranger);
            // Place ranger next to jeep when exiting
            ranger.changeX(jeep.getX() + 50 - ranger.getX());
            ranger.changeY(jeep.getY() + 50 - ranger.getY());
        }
    }

    public int getRangerPassengerCount() {
        return rangerPassengers.size();
    }

    public int getTotalPassengerCount() {
        return rangerPassengers.size() + (driver != null ? 1 : 0);
    }

    public int getMaxPassengers() {
        return MAX_PASSENGERS;
    }

    private void handleRentedMovement() {
        // Only allow movement on road tiles (tile type 5)
        if (keyInputController.isUpPressed() && canMoveInDirection(0, -1)) {
            jeep.setY(jeep.getY() - jeep.getSpeed());
            updatePassengerPositions();
        }
        if (keyInputController.isDownPressed() && canMoveInDirection(0, 1)) {
            jeep.setY(jeep.getY() + jeep.getSpeed());
            updatePassengerPositions();
        }
        if (keyInputController.isLeftPressed() && canMoveInDirection(-1, 0)) {
            jeep.setX(jeep.getX() - jeep.getSpeed());
            updatePassengerPositions();
        }
        if (keyInputController.isRightPressed() && canMoveInDirection(1, 0)) {
            jeep.setX(jeep.getX() + jeep.getSpeed());
            updatePassengerPositions();
        }
    }

    private void updatePassengerPositions() {
        // Update positions of all passengers to match jeep
        for (Ranger ranger : rangerPassengers) {
            ranger.changeX(jeep.getX() - ranger.getX());
            ranger.changeY(jeep.getY() - ranger.getY());
        }
        if (driver != null) {
            driver.changeX(jeep.getX() - driver.getX());
            driver.changeY(jeep.getY() - driver.getY());
        }
    }

    private boolean canMoveInDirection(int dx, int dy) {
        double newX = jeep.getX() + dx * jeep.getSpeed();
        double newY = jeep.getY() + dy * jeep.getSpeed();
        
        // Check if the new position is within map boundaries
        int tileSize = mapPanel.getTitleSize();
        int maxWorldCol = mapPanel.getMaxWorldColumns();
        int maxWorldRow = mapPanel.getMaxWorldRows();
        
        int tileX = (int)(newX / tileSize);
        int tileY = (int)(newY / tileSize);
        
        // Check if the new position is within map boundaries
        if (tileX < 0 || tileX >= maxWorldCol || tileY < 0 || tileY >= maxWorldRow) {
            return false;
        }
        
        return isRoadTile(tileX, tileY);
    }

    private boolean isRoadTile(int x, int y) {
        // Get the tile type at the given coordinates
        int tileType = mapPanel.getMapTileNum(x, y);
        return tileType == 5; // 5 represents road tiles
    }

    public boolean rentJeep(Ranger ranger) {
        if (!isRented && !isFull()) {
            isRented = true;
            driver = ranger;
            jeep.setRented(true);
            jeep.addRevenue(jeep.getRentalPrice());
            return true;
        }
        return false;
    }

    public void returnJeep() {
        isRented = false;
        driver = null;
        jeep.setRented(false);
        // Remove all ranger passengers
        for (Ranger ranger : new ArrayList<>(rangerPassengers)) {
            removeRangerPassenger(ranger);
        }
        rangerPassengers.clear();
    }

    public boolean isFull() {
        return getTotalPassengerCount() >= MAX_PASSENGERS;
    }

    public boolean isRented() {
        return isRented;
    }

    public Ranger getDriver() {
        return driver;
    }

    public List<Ranger> getRangerPassengers() {
        return rangerPassengers;
    }

    private void handleAutoMovement() {
        // Get current position
        double currentX = jeep.getX();
        double currentY = jeep.getY();
        
        // Try to move in a direction
        boolean moved = false;
        
        // Try right first
        if (canMoveInDirection(1, 0)) {
            jeep.setX(currentX + jeep.getSpeed());
            moved = true;
        }
        // Then try down
        else if (canMoveInDirection(0, 1)) {
            jeep.setY(currentY + jeep.getSpeed());
            moved = true;
        }
        // Then try left
        else if (canMoveInDirection(-1, 0)) {
            jeep.setX(currentX - jeep.getSpeed());
            moved = true;
        }
        // Finally try up
        else if (canMoveInDirection(0, -1)) {
            jeep.setY(currentY - jeep.getSpeed());
            moved = true;
        }
        
        if (moved) {
            updatePassengerPositions();
        }
    }

    private void generateRevenue() {
        revenueCounter++;
        if (revenueCounter >= REVENUE_INTERVAL) {
            double revenue = getTotalPassengerCount() * REVENUE_PER_PASSENGER;
            jeep.addRevenue(revenue);
            // Update game manager's capital
            mapPanel.getGameManager().increaseCapital((int)revenue);
            // Update the top panel's balance display
            mapPanel.getTopPanel().updateBalanceDisplay();
            // Force update the display
            mapPanel.updateDisplay();
            revenueCounter = 0;
        }
    }
} 