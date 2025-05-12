package Controller;

import java.awt.geom.Point2D;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.PriorityQueue;
import java.util.Set;

import javax.imageio.ImageIO;

import Model.Entity.Animal.Carnivore;
import Model.Entity.Animal.Herbivore;
import Model.Entity.Entity;
import Model.Scenery.Pond;
import View.Enities.EntitySprite;
import View.Scenes.MapPanel;

public class AnimalController extends EntityController {
    // Thirst mechanics
    private int maxThirst = 100;
    private int currentThirst = 100;
    private int thirstDecreaseRate = 1; // Decrease by 1 point every 60 frames (1 second)
    private int thirstDecreaseCounter = 0;
    
    // Hunger mechanics
    private int maxHunger = 100;
    private int currentHunger = 100;
    private int hungerDecreaseRate = 1; // Decrease by 1 point every 180 frames (3 seconds)
    private int hungerDecreaseCounter = 0;
    private boolean isEating = false;
    
    // Health mechanics
    private int maxHealth = 100;
    private int currentHealth = 100;
    private int healthDecreaseRate = 2; // Decrease by 2 points every 60 frames (1 second)
    private int healthDecreaseCounter = 0;
    
    // Attack mechanics
    private boolean isAttacking = false;
    private Entity targetPrey = null;
    private int attackRadius = 50; // Reduced from 200 to require closer distance
    private int attackDamage = 20;
    private int attackCooldown = 60;
    private int attackCooldownCounter = 0;
    private boolean isCarnivore = false;
    private boolean isEscaping = false;
    private Entity predator = null;
    private int escapeRadius = 300;
    private int escapeTime = 180;
    private int escapeCounter = 0;
    
    // Steak mechanics
    private boolean isSteak = false;
    private int steakExpirationTime = 900; // Reduced from 1800 to 15 seconds at 60 FPS
    private int steakExpirationCounter = 0;
    
    // Drinking mechanics
    private boolean isDrinking = false;
    private int drinkingTime = 900; // 15 seconds at 60 FPS (reduced from 30 seconds)
    private int drinkingCounter = 0;
    private int drinkingAmount = 20; // Amount of thirst restored per drinking session
    private int drinkingDistance = 40; // Distance from which animals can drink from a pond
    
    // Plant seeking
    private boolean seekingPlant = false;
    private int eatingTime = 900; // 15 seconds at 60 FPS
    private int eatingCounter = 0;
    private int eatingAmount = 20; // Amount of hunger restored per eating session
    private int plantSeekInterval = 300; // Check for plants every 5 seconds
    private int plantSeekCounter = 0;
    private int plantSearchRadius = 500; // Radius to search for plants
    
    // Pond seeking
    private boolean seekingPond = false;
    private Pond nearestPond = null;
    private int pondSeekInterval = 300; // Check for ponds every 5 seconds
    private int pondSeekCounter = 0;
    private List<Pond> accessiblePonds = new ArrayList<>(); // List of ponds that might be accessible
    private boolean hasReachedPond = false; // Flag to track if we've reached the pond
    private boolean isNearPond = false; // Flag to track if animal is near a pond
    private int nearPondThreshold = 150; // Distance threshold to consider "near a pond"
    
    // Pathfinding
    private List<Point2D.Double> currentPath = new ArrayList<>();
    private int pathUpdateInterval = 120; // Update path every 2 seconds
    private int pathUpdateCounter = 0;
    private int pathIndex = 0; // Current position in the path
    private boolean hasPath = false; // Whether we have a valid path to follow
    
    // Movement state
    private boolean isMoving = true;
    private int movementPauseTime = 180; // 3 seconds pause when changing direction
    private int movementPauseCounter = 0;
    private Point2D.Double targetPosition = null;
    private String currentMovementDirection = null; // For pond seeking movement
    private int stuckCounter = 0; // Counter to detect if animal is stuck
    private int maxStuckTime = 300; // Maximum time to be stuck before changing direction
    private int nearPondCounter = 0; // Counter to detect if animal is near a pond but not drinking
    private int pondSearchRadius = 500; // Radius to search for ponds (increased from default)
    private int directionChangeCounter = 0; // Counter to track when to change direction
    private int directionChangeInterval = 60; // Change direction every second when stuck
    private int directionChangeCooldown = 0; // Cooldown counter to prevent too frequent direction changes
    
    // Position tracking for detecting if animal is stuck in one place
    private double lastX = 0;
    private double lastY = 0;
    private int positionChangeCounter = 0;
    private int maxPositionChangeTime = 600; // 10 seconds at 60 FPS
    private boolean hasPositionChanged = false;
    
    // Movement speeds
    private int carnivoreSpeed = 2; // Normal speed for carnivores
    private int herbivoreSpeed = 1; // Slower speed for herbivores

    public AnimalController(Entity entity, MapPanel mapPanel, String[] images, boolean isPlayable,
            ColissionController colissionController, KeyInputController keyInputController) {
        super(entity, mapPanel, images, isPlayable, colissionController, keyInputController);
        isCarnivore = !(entity instanceof Herbivore);
        
        // Set appropriate speed based on animal type
        if (isCarnivore) {
            entity.setSpeed(carnivoreSpeed);
        } else {
            entity.setSpeed(herbivoreSpeed);
        }
    }
    
    private void transformIntoSteak() {
        isSteak = true;
        // Load the steak image
        try {
            BufferedImage steakImage = ImageIO.read(getClass().getResourceAsStream("/View/Assets/steak.png"));
            // Set all directions to the steak image since it's static
            entity.setUp1(steakImage);
            entity.setUp2(steakImage);
            entity.setDown1(steakImage);
            entity.setDown2(steakImage);
            entity.setLeft1(steakImage);
            entity.setLeft2(steakImage);
            entity.setRight1(steakImage);
            entity.setRight2(steakImage);
            // Set the current image to steak
            currentImage = steakImage;
        } catch (IOException e) {
            e.printStackTrace();
        }
        // Make the entity immobile by setting its speed to 0
        entity.setSpeed(0);
        // Disable collision by setting solid area to zero
        entity.setSolidAreaX(0);
        entity.setSolidAreaY(0);
        // Reset expiration counter
        steakExpirationCounter = 0;
    }
    
    private void updateSteak() {
        if (!isSteak) return;
        
        steakExpirationCounter++;
        if (steakExpirationCounter >= steakExpirationTime) {
            // Remove the steak from the game
            EntitySprite sprite = getEntitySprite();
            mapPanel.removeEntity(sprite);
            // Mark the entity as removed
            entity.setRemoved(true);
        }
    }
    
    public EntitySprite getEntitySprite() {
        return new EntitySprite(mapPanel, this);
    }
    
    @Override
    protected void updateNPC() {
        if (isSteak) {
            updateSteak();
            return;
        }
        
        // Update thirst level
        updateThirst();
        
        // Update hunger level
        updateHunger();
        
        // Update health level
        updateHealth();
        
        // Handle escaping if being attacked
        if (isEscaping) {
            handleEscape();
            return;
        }
        
        // Handle eating if currently eating
        if (isEating) {
            handleEating();
            return;
        }
        
        // Handle drinking if currently drinking
        if (isDrinking) {
            handleDrinking();
            return;
        }
        
        // For carnivores, check for prey and attack
        if (isCarnivore) {
            checkForPrey();
            if (isAttacking) {
                handleAttack();
                return;
            }
        }
        
        // Check needs and prioritize
        boolean needsWater = currentThirst < maxThirst * 0.5;
        boolean needsFood = (isHerbivore() && currentHunger < maxHunger * 0.7) ||
                          (isCarnivore() && currentHunger < maxHunger * 0.7);
        
        if (needsWater) {
            // Prioritize water if thirsty
            handlePondSeeking();
        } else if (needsFood) {
            // If not thirsty but hungry, look for food
            if (isHerbivore()) {
                handlePlantSeeking();
            } else if (isCarnivore()) {
                handleHunting();
            }
        } else {
            // If all needs are satisfied, normal movement
            handleNormalMovement();
        }
    }
    
    private boolean isAtPond() {
        // Check if the current tile is a water tile
        int tileSize = mapPanel.getTitleSize();
        int tileX = (int)(entity.getX() / tileSize);
        int tileY = (int)(entity.getY() / tileSize);
        
        return mapPanel.getMapTileNum(tileX, tileY) == 1; // 1 is water tile
    }
    
    private void updateThirst() {
        thirstDecreaseCounter++;
        if (thirstDecreaseCounter >= 60) { // Decrease thirst every second
            currentThirst = Math.max(0, currentThirst - thirstDecreaseRate);
            thirstDecreaseCounter = 0;
        }
    }
    
    private void updateHunger() {
        hungerDecreaseCounter++;
        if (hungerDecreaseCounter >= 180) { // Decrease hunger every 3 seconds
            currentHunger = Math.max(0, currentHunger - hungerDecreaseRate);
            hungerDecreaseCounter = 0;
        }
    }
    
    private void updateHealth() {
        healthDecreaseCounter++;
        if (healthDecreaseCounter >= 60) { // Decrease health every second
            // Only decrease health if either thirst or hunger is 0
            if (currentThirst <= 0 || currentHunger <= 0) {
                currentHealth = Math.max(0, currentHealth - healthDecreaseRate);
            }
            healthDecreaseCounter = 0;
        }
        
        // Check if animal has died and transform into steak
        if (currentHealth <= 0 && !isSteak) {
            transformIntoSteak();
        }
    }
    
    private void handleDrinking() {
        drinkingCounter++;
        
        // Update sprite to drinking animation if available
        // This would require adding drinking sprites to the entity
        
        // Gradually refill thirst while drinking - faster refill
        if (drinkingCounter % 10 == 0) { // Every 1/6 second (increased from every 1/4 second)
            currentThirst = Math.min(maxThirst, currentThirst + 3); // Increased from 2 to 3 points
        }
        
        if (drinkingCounter >= drinkingTime) {
            // Drinking session complete
            currentThirst = maxThirst; // Fully refill thirst
            isDrinking = false;
            drinkingCounter = 0;
            
            // If still thirsty, continue seeking pond
            if (currentThirst < maxThirst * 0.5) {
                seekingPond = true;
            } else {
                seekingPond = false;
            }
        }
    }
    
    private void handlePondSeeking() {
        // Check for ponds periodically
        pondSeekCounter++;
        if (pondSeekCounter >= pondSeekInterval || nearestPond == null) {
            findNearestPond();
            pondSeekCounter = 0;
            hasReachedPond = false; // Reset reached pond flag when finding a new pond
            isNearPond = false; // Reset near pond flag
            hasPath = false; // Reset path flag
            currentPath.clear(); // Clear current path
        }
        
        // Check if animal has moved from its last position
        checkPositionChange();
        
        // If we found a pond, move towards it
        if (nearestPond != null) {
            // Check if we're near a pond
            isNearPond = checkIfNearPond();
            
            // Check if we can drink from the current position
            if (canDrinkFromPosition()) {
                isDrinking = true;
                seekingPond = false;
                nearestPond = null;
                currentMovementDirection = null; // Reset movement direction
                stuckCounter = 0; // Reset stuck counter
                nearPondCounter = 0; // Reset near pond counter
                hasReachedPond = false; // Reset reached pond flag
                isNearPond = false; // Reset near pond flag
                hasPath = false; // Reset path flag
                currentPath.clear(); // Clear current path
                hasPositionChanged = false;
                positionChangeCounter = 0;
                return;
            }
            
            // Only check if we've reached the pond periodically to avoid stopping too often
            if (pondSeekCounter % 30 == 0) { // Check every half second
                if (hasReachedPond()) {
                    hasReachedPond = true;
                }
            }
            
            // If we've reached the pond, start drinking
            if (hasReachedPond) {
                isDrinking = true;
                seekingPond = false;
                nearestPond = null;
                currentMovementDirection = null; // Reset movement direction
                stuckCounter = 0; // Reset stuck counter
                nearPondCounter = 0; // Reset near pond counter
                hasReachedPond = false; // Reset reached pond flag
                isNearPond = false; // Reset near pond flag
                hasPath = false; // Reset path flag
                currentPath.clear(); // Clear current path
                hasPositionChanged = false;
                positionChangeCounter = 0;
                return;
            }
            
            // Update path periodically
            pathUpdateCounter++;
            if (pathUpdateCounter >= pathUpdateInterval || !hasPath) {
                updatePathToPond();
                pathUpdateCounter = 0;
            }
            
            // Move along the path if we have one
            if (hasPath && !currentPath.isEmpty()) {
                moveAlongPath();
            } else {
                // Fallback to direct movement if pathfinding fails
                moveTowardsPond();
            }
        } else {
            // If no pond found, resume normal movement temporarily
            handleNormalMovement();
        }
    }
    
    private void updatePathToPond() {
        if (nearestPond == null) return;
        
        // Get the pond location - using reflection to access the private field
        Point2D.Float pondLocation = null;
        try {
            java.lang.reflect.Field locationField = Pond.class.getDeclaredField("location");
            locationField.setAccessible(true);
            pondLocation = (Point2D.Float) locationField.get(nearestPond);
        } catch (Exception e) {
            e.printStackTrace();
            return;
        }
        
        // Get current position
        double startX = entity.getX();
        double startY = entity.getY();
        double endX = pondLocation.x;
        double endY = pondLocation.y;
        
        // Find path using A* algorithm
        List<Point2D.Double> path = findPath(startX, startY, endX, endY);
        
        if (path != null && !path.isEmpty()) {
            currentPath = path;
            pathIndex = 0;
            hasPath = true;
        } else {
            // If pathfinding fails, clear the path and fall back to direct movement
            currentPath.clear();
            hasPath = false;
        }
    }
    
    private List<Point2D.Double> findPath(double startX, double startY, double endX, double endY) {
        int tileSize = mapPanel.getTitleSize();
        int startTileX = (int)(startX / tileSize);
        int startTileY = (int)(startY / tileSize);
        int endTileX = (int)(endX / tileSize);
        int endTileY = (int)(endY / tileSize);
        
        // Create a priority queue for A* algorithm
        PriorityQueue<Node> openSet = new PriorityQueue<>(Comparator.comparingDouble(n -> n.fCost));
        Set<Node> closedSet = new HashSet<>();
        
        // Create start node
        Node startNode = new Node(startTileX, startTileY, null);
        startNode.gCost = 0;
        startNode.hCost = calculateHeuristic(startTileX, startTileY, endTileX, endTileY);
        startNode.fCost = startNode.gCost + startNode.hCost;
        
        openSet.add(startNode);
        
        // A* algorithm
        while (!openSet.isEmpty()) {
            Node currentNode = openSet.poll();
            
            // Check if we've reached the goal
            if (currentNode.x == endTileX && currentNode.y == endTileY) {
                // Reconstruct path
                List<Point2D.Double> path = new ArrayList<>();
                Node current = currentNode;
                while (current != null) {
                    // Convert tile coordinates to world coordinates (center of tile)
                    path.add(0, new Point2D.Double(
                        current.x * tileSize + tileSize / 2,
                        current.y * tileSize + tileSize / 2
                    ));
                    current = current.parent;
                }
                return path;
            }
            
            closedSet.add(currentNode);
            
            // Check neighbors
            int[][] directions = {{0, 1}, {1, 0}, {0, -1}, {-1, 0}}; // Up, Right, Down, Left
            for (int[] dir : directions) {
                int newX = currentNode.x + dir[0];
                int newY = currentNode.y + dir[1];
                
                // Check if the neighbor is valid
                if (newX < 0 || newY < 0 || newX >= mapPanel.getMaxWorldColumns() || newY >= mapPanel.getMaxWorldRows()) {
                    continue;
                }
                
                // Check if the tile is walkable
                if (mapPanel.getMapTileNum(newX, newY) != 0) { // 0 is walkable
                    continue;
                }
                
                // Create neighbor node
                Node neighbor = new Node(newX, newY, currentNode);
                neighbor.gCost = currentNode.gCost + 1;
                neighbor.hCost = calculateHeuristic(newX, newY, endTileX, endTileY);
                neighbor.fCost = neighbor.gCost + neighbor.hCost;
                
                // Skip if already in closed set
                if (closedSet.contains(neighbor)) {
                    continue;
                }
                
                // Add to open set if not already there or if this path is better
                if (!openSet.contains(neighbor)) {
                    openSet.add(neighbor);
                } else {
                    // Find the existing node in the open set
                    for (Node n : openSet) {
                        if (n.x == neighbor.x && n.y == neighbor.y) {
                            // If this path is better, update the node
                            if (neighbor.gCost < n.gCost) {
                                n.gCost = neighbor.gCost;
                                n.fCost = n.gCost + n.hCost;
                                n.parent = currentNode;
                            }
                            break;
                        }
                    }
                }
            }
        }
        
        // No path found
        return null;
    }
    
    private double calculateHeuristic(int x1, int y1, int x2, int y2) {
        // Manhattan distance heuristic
        return Math.abs(x2 - x1) + Math.abs(y2 - y1);
    }
    
    private void moveAlongPath() {
        if (currentPath.isEmpty() || pathIndex >= currentPath.size()) {
            hasPath = false;
            return;
        }
        
        // Get the next waypoint
        Point2D.Double target = currentPath.get(pathIndex);
        
        // Calculate direction to waypoint
        double dx = target.x - entity.getX();
        double dy = target.y - entity.getY();
        
        // Determine which direction to move - only allow horizontal or vertical movement
        if (currentMovementDirection == null) {
            // Choose the direction with the larger difference
            if (Math.abs(dx) > Math.abs(dy)) {
                // Move horizontally
                if (dx > 0) {
                    currentMovementDirection = "right";
                } else {
                    currentMovementDirection = "left";
                }
            } else {
                // Move vertically
                if (dy > 0) {
                    currentMovementDirection = "down";
                } else {
                    currentMovementDirection = "up";
                }
            }
        }
        
        // Set the direction and update the sprite
        entity.setDirection(currentMovementDirection);
        updateNPCSprite(currentMovementDirection);
        
        // Check for collisions before moving
        entity.setColission(false);
        colissionController.checkTile(this);
        
        // Check for entity collisions
        int entityIndex = colissionController.checkEntity(this, mapPanel.getEntities());
        if (entityIndex != 999) {
            entity.setColission(true);
        }
        
        // Move if no collision
        
        if (!entity.getColission()) {
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
            
            // Reset stuck counter if we're moving
            stuckCounter = 0;
            directionChangeCounter = 0;
            directionChangeCooldown = 0;
            hasPositionChanged = true;
            positionChangeCounter = 0;
            
            // Check if we've reached the current waypoint
            double distance = calculateDistance(
                entity.getX(), entity.getY(),
                target.x, target.y
            );
            
            if (distance < 10) { // Within 10 pixels of the waypoint
                pathIndex++; // Move to the next waypoint
                
                // If we've reached the end of the path, reset path
                if (pathIndex >= currentPath.size()) {
                    hasPath = false;
                    currentPath.clear();
                }
            }
        } else {
            // If collision, increment stuck counter
            stuckCounter++;
            directionChangeCounter++;
            
            // If stuck for too long, try a different direction
            if (stuckCounter >= maxStuckTime) {
                // Try to find a different pond if we're stuck
                if (accessiblePonds.size() > 1) {
                    // Remove the current pond from the list
                    accessiblePonds.remove(nearestPond);
                    // Try the next closest pond
                    if (!accessiblePonds.isEmpty()) {
                        nearestPond = accessiblePonds.get(0);
                    }
                }
                
                // Reset path and try to find a new one
                hasPath = false;
                currentPath.clear();
                pathUpdateCounter = pathUpdateInterval; // Force path update on next frame
                
                changeDirection();
                currentMovementDirection = entity.getDirection();
                stuckCounter = 0;
                directionChangeCounter = 0;
                directionChangeCooldown = 30; // Set cooldown after forced direction change
            } 
            // Change direction more frequently when stuck to avoid getting trapped
            else if (directionChangeCounter >= directionChangeInterval && directionChangeCooldown <= 0) {
                changeDirection();
                currentMovementDirection = entity.getDirection();
                directionChangeCounter = 0;
                directionChangeCooldown = 30; // Set cooldown after direction change
            }
        }
        
        // Decrease cooldown counter
        if (directionChangeCooldown > 0) {
            directionChangeCooldown--;
        }
    }
    
    private boolean checkIfNearPond() {
        if (nearestPond == null) return false;
        
        // Get the pond location - using reflection to access the private field
        Point2D.Float pondLocation = null;
        try {
            java.lang.reflect.Field locationField = Pond.class.getDeclaredField("location");
            locationField.setAccessible(true);
            pondLocation = (Point2D.Float) locationField.get(nearestPond);
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
        
        // Check if animal is close enough to the pond
        double distance = calculateDistance(
            entity.getX(), entity.getY(),
            pondLocation.x, pondLocation.y
        );
        
        return distance < nearPondThreshold; // Within threshold distance of the pond
    }
    
    private boolean canDrinkFromPosition() {
        if (nearestPond == null) return false;
        
        // Get the pond location - using reflection to access the private field
        Point2D.Float pondLocation = null;
        try {
            java.lang.reflect.Field locationField = Pond.class.getDeclaredField("location");
            locationField.setAccessible(true);
            pondLocation = (Point2D.Float) locationField.get(nearestPond);
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
        
        // Check if animal is close enough to drink from the pond
        double distance = calculateDistance(
            entity.getX(), entity.getY(),
            pondLocation.x, pondLocation.y
        );
        
        // Check if there's a water tile nearby (within 1 tile)
        int tileSize = mapPanel.getTitleSize();
        int animalTileX = (int)(entity.getX() / tileSize);
        int animalTileY = (int)(entity.getY() / tileSize);
        
        // Check surrounding tiles for water - reduced range to 1 tile
        for (int dx = -1; dx <= 1; dx++) {
            for (int dy = -1; dy <= 1; dy++) {
                int checkX = animalTileX + dx;
                int checkY = animalTileY + dy;
                
                // Make sure we're within map bounds
                if (checkX >= 0 && checkY >= 0 && 
                    checkX < mapPanel.getMaxWorldColumns() && 
                    checkY < mapPanel.getMaxWorldRows()) {
                    
                    // If there's a water tile nearby, allow drinking
                    if (mapPanel.getMapTileNum(checkX, checkY) == 1) { // 1 is water tile
                        return true;
                    }
                }
            }
        }
        
        // If no water tile is nearby, use the distance-based check as fallback
        return distance < drinkingDistance; // Within drinking distance of the pond
    }
    
    private void findNearestPond() {
        nearestPond = null;
        accessiblePonds.clear();
        double minDistance = Double.MAX_VALUE;
        
        // Since we don't have direct access to GameManager, we'll use a different approach
        // We'll look for water tiles in the map
        int mapWidth = mapPanel.getMaxWorldColumns();
        int mapHeight = mapPanel.getMaxWorldRows();
        int tileSize = mapPanel.getTitleSize();
        
        // Get current position
        double currentX = entity.getX();
        double currentY = entity.getY();
        
        // First, find all ponds within a reasonable radius
        for (int x = 0; x < mapWidth; x++) {
            for (int y = 0; y < mapHeight; y++) {
                if (mapPanel.getMapTileNum(x, y) == 1) { // Water tile
                    // Calculate center of the water tile
                    double pondX = x * tileSize + tileSize / 2;
                    double pondY = y * tileSize + tileSize / 2;
                    
                    double distance = calculateDistance(
                        currentX, currentY,
                        pondX, pondY
                    );
                    
                    // Only consider ponds within the search radius
                    if (distance < pondSearchRadius) {
                        // Create a temporary pond object with the location
                        Pond pond = new Pond(1, new Point2D.Float((float)pondX, (float)pondY));
                        accessiblePonds.add(pond);
                        
                        // Check if this is the closest pond so far
                        if (distance < minDistance) {
                            minDistance = distance;
                            nearestPond = pond;
                        }
                    }
                }
            }
        }
        
        // If we found ponds, try to find one that might be accessible
        if (!accessiblePonds.isEmpty()) {
            // Sort ponds by distance
            accessiblePonds.sort((p1, p2) -> {
                try {
                    java.lang.reflect.Field locationField = Pond.class.getDeclaredField("location");
                    locationField.setAccessible(true);
                    
                    Point2D.Float loc1 = (Point2D.Float) locationField.get(p1);
                    Point2D.Float loc2 = (Point2D.Float) locationField.get(p2);
                    
                    double dist1 = calculateDistance(currentX, currentY, loc1.x, loc1.y);
                    double dist2 = calculateDistance(currentX, currentY, loc2.x, loc2.y);
                    
                    return Double.compare(dist1, dist2);
                } catch (Exception e) {
                    e.printStackTrace();
                    return 0;
                }
            });
            
            // Try to find a pond that might be accessible
            for (Pond pond : accessiblePonds) {
                try {
                    java.lang.reflect.Field locationField = Pond.class.getDeclaredField("location");
                    locationField.setAccessible(true);
                    Point2D.Float pondLocation = (Point2D.Float) locationField.get(pond);
                    
                    // Check if there's a clear path to this pond
                    if (hasClearPathToPond(pondLocation.x, pondLocation.y)) {
                        nearestPond = pond;
                        break;
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }
    
    private boolean hasClearPathToPond(double pondX, double pondY) {
        // Simple line-of-sight check
        double dx = pondX - entity.getX();
        double dy = pondY - entity.getY();
        double distance = Math.sqrt(dx * dx + dy * dy);
        
        // Check a few points along the path
        int numChecks = 5;
        int tileSize = mapPanel.getTitleSize();
        
        for (int i = 1; i < numChecks; i++) {
            double checkX = entity.getX() + (dx * i / numChecks);
            double checkY = entity.getY() + (dy * i / numChecks);
            
            // Convert to tile coordinates
            int tileX = (int)(checkX / tileSize);
            int tileY = (int)(checkY / tileSize);
            
            // Check if this tile is walkable (not water, not obstacle)
            int tileType = mapPanel.getMapTileNum(tileX, tileY);
            if (tileType != 0) { // 0 is walkable, 1 is water, others are obstacles
                return false;
            }
        }
        
        return true;
    }
    
    private void moveTowardsPond() {
        if (nearestPond == null) return;
        
        // Get the pond location - using reflection to access the private field
        Point2D.Float pondLocation = null;
        try {
            java.lang.reflect.Field locationField = Pond.class.getDeclaredField("location");
            locationField.setAccessible(true);
            pondLocation = (Point2D.Float) locationField.get(nearestPond);
        } catch (Exception e) {
            e.printStackTrace();
            return;
        }
        
        // Calculate direction to pond
        double dx = pondLocation.x - entity.getX();
        double dy = pondLocation.y - entity.getY();
        
        // If we're near a pond, move directly toward it
        if (isNearPond) {
            // Determine the primary direction to move - only allow horizontal or vertical movement
            if (Math.abs(dx) > Math.abs(dy)) {
                // Move horizontally
                if (dx > 0) {
                    currentMovementDirection = "right";
                } else {
                    currentMovementDirection = "left";
                }
            } else {
                // Move vertically
                if (dy > 0) {
                    currentMovementDirection = "down";
                } else {
                    currentMovementDirection = "up";
                }
            }
        } 
        // Otherwise, use the normal direction selection logic
        else if (currentMovementDirection == null) {
            // Choose the direction with the larger difference
            if (Math.abs(dx) > Math.abs(dy)) {
                // Move horizontally
                if (dx > 0) {
                    currentMovementDirection = "right";
                } else {
                    currentMovementDirection = "left";
                }
            } else {
                // Move vertically
                if (dy > 0) {
                    currentMovementDirection = "down";
                } else {
                    currentMovementDirection = "up";
                }
            }
        }
        
        // Set the direction and update the sprite
        entity.setDirection(currentMovementDirection);
        updateNPCSprite(currentMovementDirection);
        
        // Check for collisions before moving
        entity.setColission(false);
        colissionController.checkTile(this);
        
        // Check for entity collisions
        int entityIndex = colissionController.checkEntity(this, mapPanel.getEntities());
        if (entityIndex != 999) {
            entity.setColission(true);
        }
        
        // Move if no collision
        if (!entity.getColission()) {
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
            
            // Reset stuck counter if we're moving
            stuckCounter = 0;
            directionChangeCounter = 0;
            directionChangeCooldown = 0;
            hasPositionChanged = true;
            positionChangeCounter = 0;
        } else {
            // If collision, increment stuck counter
            stuckCounter++;
            directionChangeCounter++;
            
            // If stuck for too long, try a different direction
            if (stuckCounter >= maxStuckTime) {
                // Try to find a different pond if we're stuck
                if (accessiblePonds.size() > 1) {
                    // Remove the current pond from the list
                    accessiblePonds.remove(nearestPond);
                    // Try the next closest pond
                    if (!accessiblePonds.isEmpty()) {
                        nearestPond = accessiblePonds.get(0);
                    }
                }
                
                changeDirection();
                currentMovementDirection = entity.getDirection();
                stuckCounter = 0;
                directionChangeCounter = 0;
                directionChangeCooldown = 30; // Set cooldown after forced direction change
            } 
            // Change direction more frequently when stuck to avoid getting trapped
            else if (directionChangeCounter >= directionChangeInterval && directionChangeCooldown <= 0) {
                changeDirection();
                currentMovementDirection = entity.getDirection();
                directionChangeCounter = 0;
                directionChangeCooldown = 30; // Set cooldown after direction change
            }
        }
        
        // Decrease cooldown counter
        if (directionChangeCooldown > 0) {
            directionChangeCooldown--;
        }
    }
    
    private boolean hasReachedPond() {
        if (nearestPond == null) return false;
        
        // Get the pond location - using reflection to access the private field
        Point2D.Float pondLocation = null;
        try {
            java.lang.reflect.Field locationField = Pond.class.getDeclaredField("location");
            locationField.setAccessible(true);
            pondLocation = (Point2D.Float) locationField.get(nearestPond);
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
        
        // Check if animal is close enough to the pond
        double distance = calculateDistance(
            entity.getX(), entity.getY(),
            pondLocation.x, pondLocation.y
        );
        
        // Check if there's a water tile nearby (within 1 tile)
        int tileSize = mapPanel.getTitleSize();
        int animalTileX = (int)(entity.getX() / tileSize);
        int animalTileY = (int)(entity.getY() / tileSize);
        
        // Check surrounding tiles for water - reduced range to 1 tile
        for (int dx = -1; dx <= 1; dx++) {
            for (int dy = -1; dy <= 1; dy++) {
                int checkX = animalTileX + dx;
                int checkY = animalTileY + dy;
                
                // Make sure we're within map bounds
                if (checkX >= 0 && checkY >= 0 && 
                    checkX < mapPanel.getMaxWorldColumns() && 
                    checkY < mapPanel.getMaxWorldRows()) {
                    
                    // If there's a water tile nearby, consider the pond reached
                    if (mapPanel.getMapTileNum(checkX, checkY) == 1) { // 1 is water tile
                        return true;
                    }
                }
            }
        }
        
        // If no water tile is nearby, use the distance-based check as fallback
        return distance < 50; // Within 50 pixels of the pond
    }
    
    private void handleNormalMovement() {
        // Move in current direction
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
        colissionController.checkTile(this);
        
        // Check for entity collisions
        int entityIndex = colissionController.checkEntity(this, mapPanel.getEntities());
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
            
            // Reset position change tracking
            hasPositionChanged = true;
            positionChangeCounter = 0;
        } else {
            // If collision, change direction immediately
            changeDirection();
        }
    }
    
    private void changeDirection() {
        currentDirectionIndex = (currentDirectionIndex + 1) % directions.length;
        entity.setDirection(directions[currentDirectionIndex]);
        updateNPCSprite(entity.getDirection());
    }
    
    private double calculateDistance(double x1, double y1, double x2, double y2) {
        return Math.sqrt(Math.pow(x2 - x1, 2) + Math.pow(y2 - y1, 2));
    }
    
    // Node class for A* pathfinding
    private static class Node {
        int x, y;
        Node parent;
        double gCost, hCost, fCost;
        
        Node(int x, int y, Node parent) {
            this.x = x;
            this.y = y;
            this.parent = parent;
            this.gCost = 0;
            this.hCost = 0;
            this.fCost = 0;
        }
        
        @Override
        public boolean equals(Object obj) {
            if (obj instanceof Node) {
                Node other = (Node) obj;
                return this.x == other.x && this.y == other.y;
            }
            return false;
        }
        
        @Override
        public int hashCode() {
            return x * 31 + y;
        }
    }
    
    private void checkPositionChange() {
        // Get current position
        double currentX = entity.getX();
        double currentY = entity.getY();
        
        // Check if position has changed significantly (more than 5 pixels)
        if (Math.abs(currentX - lastX) > 5 || Math.abs(currentY - lastY) > 5) {
            hasPositionChanged = true;
            positionChangeCounter = 0;
        } else {
            // If position hasn't changed, increment counter
            positionChangeCounter++;
            
            // If animal has been in the same position for too long, find a different pond
            if (positionChangeCounter >= maxPositionChangeTime && !hasPositionChanged) {
                // Try to find a different pond
                if (accessiblePonds.size() > 1) {
                    // Remove the current pond from the list
                    accessiblePonds.remove(nearestPond);
                    // Try the next closest pond
                    if (!accessiblePonds.isEmpty()) {
                        nearestPond = accessiblePonds.get(0);
                    }
                }
                
                // Reset path and try to find a new one
                hasPath = false;
                currentPath.clear();
                pathUpdateCounter = pathUpdateInterval; // Force path update on next frame
                
                // Reset position tracking
                hasPositionChanged = false;
                positionChangeCounter = 0;
                
                // Force a new pond search
                pondSeekCounter = pondSeekInterval;
            }
        }
        
        // Update last position
        lastX = currentX;
        lastY = currentY;
    }
    
    // Getters and setters for thirst
    public int getCurrentThirst() {
        return currentThirst;
    }
    
    public int getMaxThirst() {
        return maxThirst;
    }
    
    public boolean isDrinking() {
        return isDrinking;
    }
    
    // Getters and setters for hunger
    public int getCurrentHunger() {
        return currentHunger;
    }
    
    public int getMaxHunger() {
        return maxHunger;
    }
    
    public boolean isEating() {
        return isEating;
    }
    
    // Getters and setters for health
    public int getCurrentHealth() {
        return currentHealth;
    }
    
    public int getMaxHealth() {
        return maxHealth;
    }
    
    // Getters and setters for steak
    public boolean isSteak() {
        return isSteak;
    }
    
    public int getSteakExpirationTime() {
        return steakExpirationTime;
    }
    
    public int getSteakExpirationCounter() {
        return steakExpirationCounter;
    }

    private boolean isHerbivore() {
        return entity instanceof Herbivore;
    }

    private boolean isCarnivore() {
        return entity instanceof Carnivore;
    }

    private void handleHunting() {
        // Only carnivores hunt
        if (!isCarnivore()) return;
        
        // Check for prey periodically
        plantSeekCounter++;
        if (plantSeekCounter >= plantSeekInterval) {
            findNearestPrey();
            plantSeekCounter = 0;
        }
        
        // If we found prey, move towards it
        if (seekingPlant) {
            moveTowardsPrey();
        } else {
            // If no prey found, resume normal movement
            handleNormalMovement();
        }
    }

    private void findNearestPrey() {
        double minDistance = Double.MAX_VALUE;
        EntitySprite nearestPrey = null;
        
        for (EntitySprite sprite : mapPanel.getEntities()) {
            EntityController controller = sprite.getController();
            if (controller instanceof AnimalController) {
                AnimalController animalController = (AnimalController) controller;
                // Only target herbivores
                if (animalController.isHerbivore()) {
                    double distance = calculateDistance(
                        entity.getX(), entity.getY(),
                        animalController.getEntityX(), animalController.getEntityY()
                    );
                    
                    if (distance < minDistance && distance < plantSearchRadius) {
                        minDistance = distance;
                        nearestPrey = sprite;
                    }
                }
            }
        }
        
        if (nearestPrey != null) {
            seekingPlant = true;
            targetPosition = new Point2D.Double(
                nearestPrey.getController().getEntityX(),
                nearestPrey.getController().getEntityY()
            );
        } else {
            seekingPlant = false;
            targetPosition = null;
        }
    }

    private void moveTowardsPrey() {
        if (targetPosition == null) return;
        
        // Calculate direction to prey
        double dx = targetPosition.x - entity.getX();
        double dy = targetPosition.y - entity.getY();
        
        // Check if we're close enough to attack
        double distance = calculateDistance(
            entity.getX(), entity.getY(),
            targetPosition.x, targetPosition.y
        );
        
        if (distance < 50) { // Within 50 pixels of the prey
            // Find the prey entity
            for (EntitySprite sprite : mapPanel.getEntities()) {
                EntityController controller = sprite.getController();
                if (controller instanceof AnimalController) {
                    AnimalController animalController = (AnimalController) controller;
                    if (animalController.isHerbivore() &&
                        Math.abs(animalController.getEntityX() - targetPosition.x) < 50 &&
                        Math.abs(animalController.getEntityY() - targetPosition.y) < 50) {
                        
                        // Transform prey into steak
                        animalController.transformIntoSteak();
                        
                        // Increase hunger level
                        currentHunger = Math.min(maxHunger, currentHunger + 50);
                        
                        // Reset hunting state
                        isEating = true;
                        seekingPlant = false;
                        targetPosition = null;
                        currentMovementDirection = null;
                        return;
                    }
                }
            }
        }
        
        // Move towards prey
        if (currentMovementDirection == null) {
            // Choose the direction with the larger difference
            if (Math.abs(dx) > Math.abs(dy)) {
                // Move horizontally
                if (dx > 0) {
                    currentMovementDirection = "right";
                } else {
                    currentMovementDirection = "left";
                }
            } else {
                // Move vertically
                if (dy > 0) {
                    currentMovementDirection = "down";
                } else {
                    currentMovementDirection = "up";
                }
            }
        }
        
        // Set the direction and update the sprite
        entity.setDirection(currentMovementDirection);
        updateNPCSprite(currentMovementDirection);
        
        // Check for collisions before moving
        entity.setColission(false);
        colissionController.checkTile(this);
        
        // Check for entity collisions
        int entityIndex = colissionController.checkEntity(this, mapPanel.getEntities());
        if (entityIndex != 999) {
            entity.setColission(true);
        }
        
        // Move if no collision
        if (!entity.getColission()) {
            // Increase step size for more dynamic movement
            double stepSize = entity.getSpeed() * 1.5;
            
            switch (entity.getDirection()) {
                case "up":
                    entity.changeY(-stepSize);
                    break;
                case "down":
                    entity.changeY(stepSize);
                    break;
                case "right":
                    entity.changeX(stepSize);
                    break;
                case "left":
                    entity.changeX(-stepSize);
                    break;
            }
            
            // Update sprite animation
            updateSpriteAnimation();
            updateNPCSprite(entity.getDirection());
        } else {
            // If collision, try a different direction
            changeDirection();
            currentMovementDirection = entity.getDirection();
        }
    }

    private void handlePlantSeeking() {
        // Only herbivores seek plants
        if (!isHerbivore()) return;
        
        // Check for plants periodically
        plantSeekCounter++;
        if (plantSeekCounter >= plantSeekInterval) {
            findNearestPlant();
            plantSeekCounter = 0;
        }
        
        // If we found a plant, move towards it
        if (seekingPlant) {
            moveTowardsPlant();
        } else {
            // If no plant found, resume normal movement
            handleNormalMovement();
        }
    }
    
    private void findNearestPlant() {
        // Look for plant tiles in the map
        int mapWidth = mapPanel.getMaxWorldColumns();
        int mapHeight = mapPanel.getMaxWorldRows();
        int tileSize = mapPanel.getTitleSize();
        
        // Get current position
        double currentX = entity.getX();
        double currentY = entity.getY();
        
        // Find all plants within a reasonable radius
        double minDistance = Double.MAX_VALUE;
        Point2D.Double nearestPlant = null;
        
        for (int x = 0; x < mapWidth; x++) {
            for (int y = 0; y < mapHeight; y++) {
                int tileType = mapPanel.getMapTileNum(x, y);
                // Check for both trees (2) and bushes (3)
                if (tileType == 2 || tileType == 3) {
                    // Calculate center of the plant tile
                    double plantX = x * tileSize + tileSize / 2;
                    double plantY = y * tileSize + tileSize / 2;
                    
                    double distance = calculateDistance(
                        currentX, currentY,
                        plantX, plantY
                    );
                    
                    // Only consider plants within the search radius and closer than current target
                    if (distance < plantSearchRadius && distance < minDistance) {
                        // Check if there's a clear path to the plant
                        if (hasClearPathToPlant(plantX, plantY)) {
                            minDistance = distance;
                            nearestPlant = new Point2D.Double(plantX, plantY);
                        }
                    }
                }
            }
        }
        
        if (nearestPlant != null) {
            seekingPlant = true;
            targetPosition = nearestPlant;
            currentMovementDirection = null; // Reset movement direction when finding new plant
        } else {
            seekingPlant = false;
            targetPosition = null;
        }
    }
    
    private boolean hasClearPathToPlant(double plantX, double plantY) {
        // Simple line-of-sight check
        double dx = plantX - entity.getX();
        double dy = plantY - entity.getY();
        double distance = Math.sqrt(dx * dx + dy * dy);
        
        // Check a few points along the path
        int numChecks = 5;
        int tileSize = mapPanel.getTitleSize();
        
        for (int i = 1; i < numChecks; i++) {
            double checkX = entity.getX() + (dx * i / numChecks);
            double checkY = entity.getY() + (dy * i / numChecks);
            
            // Convert to tile coordinates
            int tileX = (int)(checkX / tileSize);
            int tileY = (int)(checkY / tileSize);
            
            // Check if this tile is walkable (not water, not obstacle)
            int tileType = mapPanel.getMapTileNum(tileX, tileY);
            if (tileType != 0) { // 0 is walkable, 1 is water, others are obstacles
                return false;
            }
        }
        
        return true;
    }
    
    private void moveTowardsPlant() {
        if (targetPosition == null) return;
        
        // Calculate direction to plant
        double dx = targetPosition.x - entity.getX();
        double dy = targetPosition.y - entity.getY();
        
        // Check if we're close enough to eat
        double distance = calculateDistance(
            entity.getX(), entity.getY(),
            targetPosition.x, targetPosition.y
        );
        
        if (distance < 50) { // Within 50 pixels of the plant
            isEating = true;
            seekingPlant = false;
            targetPosition = null;
            currentMovementDirection = null;
            return;
        }
        
        // Only change direction if we've reached the current axis or if we don't have a direction
        if (currentMovementDirection == null) {
            // Choose a random movement pattern
            int pattern = (int)(Math.random() * 3); // 0: L-shape, 1: +-shape, 2: triangle
            
            switch (pattern) {
                case 0: // L-shape
                    if (Math.abs(dx) > Math.abs(dy)) {
                        currentMovementDirection = dx > 0 ? "right" : "left";
                    } else {
                        currentMovementDirection = dy > 0 ? "down" : "up";
                    }
                    break;
                case 1: // +-shape
                    if (Math.random() < 0.5) {
                        currentMovementDirection = dx > 0 ? "right" : "left";
                    } else {
                        currentMovementDirection = dy > 0 ? "down" : "up";
                    }
                    break;
                case 2: // Triangle
                    if (Math.abs(dx) > Math.abs(dy)) {
                        if (Math.random() < 0.5) {
                            currentMovementDirection = dx > 0 ? "right" : "left";
                        } else {
                            currentMovementDirection = dy > 0 ? "down" : "up";
                        }
                    } else {
                        if (Math.random() < 0.5) {
                            currentMovementDirection = dy > 0 ? "down" : "up";
                        } else {
                            currentMovementDirection = dx > 0 ? "right" : "left";
                        }
                    }
                    break;
            }
        }
        
        // Set the direction and update the sprite
        entity.setDirection(currentMovementDirection);
        updateNPCSprite(currentMovementDirection);
        
        // Check for collisions before moving
        entity.setColission(false);
        colissionController.checkTile(this);
        
        // Check for entity collisions
        int entityIndex = colissionController.checkEntity(this, mapPanel.getEntities());
        if (entityIndex != 999) {
            entity.setColission(true);
        }
        
        // Move if no collision
        if (!entity.getColission()) {
            // Increase step size for more dynamic movement
            double stepSize = entity.getSpeed() * 1.5;
            
            switch (entity.getDirection()) {
                case "up":
                    entity.changeY(-stepSize);
                    break;
                case "down":
                    entity.changeY(stepSize);
                    break;
                case "right":
                    entity.changeX(stepSize);
                    break;
                case "left":
                    entity.changeX(-stepSize);
                    break;
            }
            
            // Update sprite animation
            updateSpriteAnimation();
            updateNPCSprite(entity.getDirection());
        } else {
            // If collision, try a different direction
            changeDirection();
            currentMovementDirection = entity.getDirection();
        }
    }
    
    private void handleEating() {
        if (!isEating) return;
        
        eatingCounter++;
        
        // If we're a carnivore and there's a steak nearby
        if (isCarnivore && targetPosition != null) {
            double dx = targetPosition.x - entity.getX();
            double dy = targetPosition.y - entity.getY();
            double distance = calculateDistance(
                entity.getX(), entity.getY(),
                targetPosition.x, targetPosition.y
            );
            
            // If we're close enough to the steak, continue eating
            if (distance < 50) {
                // Gradually refill hunger while eating
                if (eatingCounter % 10 == 0) { // Every 1/6 second
                    currentHunger = Math.min(maxHunger, currentHunger + 3);
                }
                
                // Increase expiration counter while eating
                steakExpirationCounter++;
                
                if (eatingCounter >= eatingTime) {
                    // Eating session complete
                    currentHunger = maxHunger;
                    isEating = false;
                    eatingCounter = 0;
                    targetPosition = null;
                    // Resume normal movement after eating
                    handleNormalMovement();
                }
            } else {
                // Move towards the steak using grid movement with collision checks
                boolean canMoveHorizontal = false;
                boolean canMoveVertical = false;
                
                // Check horizontal movement
                entity.setColission(false);
                colissionController.checkTile(this);
                if (!entity.getColission()) {
                    canMoveHorizontal = true;
                }
                
                // Check vertical movement
                entity.setColission(false);
                colissionController.checkTile(this);
                if (!entity.getColission()) {
                    canMoveVertical = true;
                }
                
                // If both directions are blocked, give up and resume normal movement
                if (!canMoveHorizontal && !canMoveVertical) {
                    isEating = false;
                    eatingCounter = 0;
                    targetPosition = null;
                    handleNormalMovement();
                    return;
                }
                
                // Choose movement direction based on available paths
                if (canMoveHorizontal && Math.abs(dx) > Math.abs(dy)) {
                    // Move horizontally
                    if (dx > 0) {
                        entity.setDirection("right");
                        entity.changeX(entity.getSpeed());
                    } else {
                        entity.setDirection("left");
                        entity.changeX(-entity.getSpeed());
                    }
                } else if (canMoveVertical) {
                    // Move vertically
                    if (dy > 0) {
                        entity.setDirection("down");
                        entity.changeY(entity.getSpeed());
                    } else {
                        entity.setDirection("up");
                        entity.changeY(-entity.getSpeed());
                    }
                }
                
                // Update animation
                updateSpriteAnimation();
                updateNPCSprite(entity.getDirection());
            }
        } else {
            // For herbivores, continue normal eating behavior
            if (eatingCounter % 10 == 0) { // Every 1/6 second
                currentHunger = Math.min(maxHunger, currentHunger + 3);
            }
            
            if (eatingCounter >= eatingTime) {
                // Eating session complete
                currentHunger = maxHunger;
                isEating = false;
                eatingCounter = 0;
            }
        }
    }

    private void checkForPrey() {
        if (!isCarnivore || isAttacking) return;
        
        // Get all entities from the map panel
        List<EntitySprite> sprites = mapPanel.getEntities();
        double minDistance = Double.MAX_VALUE;
        Entity closestPrey = null;
        
        // Find the closest herbivore within attack radius
        for (EntitySprite sprite : sprites) {
            Entity e = sprite.getController().getEntity();
            if (e instanceof Herbivore && !e.isRemoved()) {
                double distance = calculateDistance(
                    entity.getX(), entity.getY(),
                    e.getX(), e.getY()
                );
                
                if (distance < attackRadius && distance < minDistance) {
                    minDistance = distance;
                    closestPrey = e;
                }
            }
        }
        
        if (closestPrey != null) {
            targetPrey = closestPrey;
            isAttacking = true;
            // Stop any current movement
            seekingPond = false;
            seekingPlant = false;
            isDrinking = false;
            isEating = false;
        }
    }
    
    private void handleAttack() {
        if (!isAttacking || targetPrey == null || targetPrey.isRemoved()) {
            isAttacking = false;
            targetPrey = null;
            return;
        }
        
        // Check if prey is still in range
        double distance = calculateDistance(
            entity.getX(), entity.getY(),
            targetPrey.getX(), targetPrey.getY()
        );
        
        if (distance > attackRadius) {
            isAttacking = false;
            targetPrey = null;
            return;
        }
        
        // Move towards prey using grid movement with collision checks
        double dx = targetPrey.getX() - entity.getX();
        double dy = targetPrey.getY() - entity.getY();
        
        // Check for obstacles in both directions
        boolean canMoveHorizontal = false;
        boolean canMoveVertical = false;
        
        // Check horizontal movement
        entity.setColission(false);
        colissionController.checkTile(this);
        if (!entity.getColission()) {
            canMoveHorizontal = true;
        }
        
        // Check vertical movement
        entity.setColission(false);
        colissionController.checkTile(this);
        if (!entity.getColission()) {
            canMoveVertical = true;
        }
        
        // Choose movement direction based on available paths
        if (canMoveHorizontal && Math.abs(dx) > Math.abs(dy)) {
            // Move horizontally
            if (dx > 0) {
                entity.setDirection("right");
                entity.changeX(entity.getSpeed());
            } else {
                entity.setDirection("left");
                entity.changeX(-entity.getSpeed());
            }
        } else if (canMoveVertical) {
            // Move vertically
            if (dy > 0) {
                entity.setDirection("down");
                entity.changeY(entity.getSpeed());
            } else {
                entity.setDirection("up");
                entity.changeY(-entity.getSpeed());
            }
        } else {
            // If both directions are blocked, try to find a path around
            if (Math.abs(dx) > Math.abs(dy)) {
                // Try to move vertically first to get around obstacle
                if (dy > 0) {
                    entity.setDirection("down");
                    entity.changeY(entity.getSpeed());
                } else {
                    entity.setDirection("up");
                    entity.changeY(-entity.getSpeed());
                }
            } else {
                // Try to move horizontally first to get around obstacle
                if (dx > 0) {
                    entity.setDirection("right");
                    entity.changeX(entity.getSpeed());
                } else {
                    entity.setDirection("left");
                    entity.changeX(-entity.getSpeed());
                }
            }
        }
        
        // Update animation
        updateSpriteAnimation();
        updateNPCSprite(entity.getDirection());
        
        // Attack cooldown
        if (attackCooldownCounter > 0) {
            attackCooldownCounter--;
            return;
        }
        
        // Deal damage to prey
        for (EntitySprite sprite : mapPanel.getEntities()) {
            if (sprite.getController().getEntity() == targetPrey) {
                AnimalController preyController = (AnimalController) sprite.getController();
                preyController.takeDamage(attackDamage);
                
                // Reset cooldown
                attackCooldownCounter = attackCooldown;
                
                // If prey is dead, transform into steak and eat it immediately
                if (preyController.getCurrentHealth() <= 0) {
                    // Move the carnivore slightly away from the prey before transforming it into steak
                    double offsetX = 0;
                    double offsetY = 0;
                    
                    // Calculate offset based on current direction
                    switch (entity.getDirection()) {
                        case "up":
                            offsetY = -entity.getSpeed() * 2;
                            break;
                        case "down":
                            offsetY = entity.getSpeed() * 2;
                            break;
                        case "left":
                            offsetX = -entity.getSpeed() * 2;
                            break;
                        case "right":
                            offsetX = entity.getSpeed() * 2;
                            break;
                    }
                    
                    // Move the carnivore
                    entity.changeX(offsetX);
                    entity.changeY(offsetY);
                    
                    // Transform prey into steak
                    preyController.transformIntoSteak();
                    isAttacking = false;
                    targetPrey = null;
                    
                    // Immediately increase hunger level when eating the steak
                    currentHunger = Math.min(maxHunger, currentHunger + 50); // Increase hunger by 50 points
                    
                    // Start eating animation
                    isEating = true;
                    eatingCounter = 0;
                    
                    // Set the steak as the target for eating
                    targetPosition = new Point2D.Double(
                        preyController.getEntity().getX(),
                        preyController.getEntity().getY()
                    );
                }
                break;
            }
        }
    }
    
    private void handleEscape() {
        if (!isEscaping) return;
        
        escapeCounter++;
        if (escapeCounter >= escapeTime) {
            isEscaping = false;
            predator = null;
            return;
        }
        
        // If we have a predator, move away from it
        if (predator != null) {
            double dx = entity.getX() - predator.getX();
            double dy = entity.getY() - predator.getY();
            
            // Check for obstacles in both directions
            boolean canMoveHorizontal = false;
            boolean canMoveVertical = false;
            
            // Check horizontal movement
            entity.setColission(false);
            colissionController.checkTile(this);
            if (!entity.getColission()) {
                canMoveHorizontal = true;
            }
            
            // Check vertical movement
            entity.setColission(false);
            colissionController.checkTile(this);
            if (!entity.getColission()) {
                canMoveVertical = true;
            }
            
            // Choose movement direction based on available paths
            if (canMoveHorizontal && Math.abs(dx) > Math.abs(dy)) {
                // Move horizontally
                if (dx > 0) {
                    entity.setDirection("right");
                    entity.changeX(entity.getSpeed());
                } else {
                    entity.setDirection("left");
                    entity.changeX(-entity.getSpeed());
                }
            } else if (canMoveVertical) {
                // Move vertically
                if (dy > 0) {
                    entity.setDirection("down");
                    entity.changeY(entity.getSpeed());
                } else {
                    entity.setDirection("up");
                    entity.changeY(-entity.getSpeed());
                }
            } else {
                // If both directions are blocked, try to find a path around
                if (Math.abs(dx) > Math.abs(dy)) {
                    // Try to move vertically first to get around obstacle
                    if (dy > 0) {
                        entity.setDirection("down");
                        entity.changeY(entity.getSpeed());
                    } else {
                        entity.setDirection("up");
                        entity.changeY(-entity.getSpeed());
                    }
                } else {
                    // Try to move horizontally first to get around obstacle
                    if (dx > 0) {
                        entity.setDirection("right");
                        entity.changeX(entity.getSpeed());
                    } else {
                        entity.setDirection("left");
                        entity.changeX(-entity.getSpeed());
                    }
                }
            }
            
            // Update animation
            updateSpriteAnimation();
            updateNPCSprite(entity.getDirection());
        }
    }
    
    private void takeDamage(int damage) {
        currentHealth = Math.max(0, currentHealth - damage);
        
        // If herbivore, start escaping
        if (!isCarnivore) {
            isEscaping = true;
            escapeCounter = 0;
            // Stop current activities
            isEating = false;
            isDrinking = false;
            seekingPlant = false;
            seekingPond = false;
            
            // Set the predator reference
            for (EntitySprite sprite : mapPanel.getEntities()) {
                if (sprite.getController() instanceof AnimalController) {
                    AnimalController controller = (AnimalController) sprite.getController();
                    if (controller.isCarnivore && controller.isAttacking && controller.targetPrey == entity) {
                        predator = controller.getEntity();
                        break;
                    }
                }
            }
        }
    }
} 