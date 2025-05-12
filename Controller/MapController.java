package Controller;

import View.MapAssets.Tile;
import View.Scenes.MapPanel;
import java.awt.Graphics2D;
import java.awt.Point;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import javax.imageio.ImageIO;
//THIS CONTROLLERS IS IN CHARGE OF THE MAP AND THE CAMERA OF THE GAME

public class MapController {
    private MapPanel map;
    private Tile[] tiles;
    private int mapTileNum[][];
    private String mapPath;
    private double cameraX;
    private double cameraY;
    private final int cameraWidth = 44;
    private final int cameraHeight = 22;

    public MapController(MapPanel map, String mapPath) {
        this.map = map;
        this.mapPath = mapPath;
        tiles = new Tile[8];
        mapTileNum = new int[map.getMaxWorldColumns()][map.getMaxWorldRows()];
        // Center the camera
        int worldWidth = map.getMaxWorldColumns() * map.getTitleSize();
        int worldHeight = map.getMaxWorldRows() * map.getTitleSize();
        int screenWidth = map.getTitleSize() * cameraWidth;
        int screenHeight = map.getTitleSize() * cameraHeight;
        // Set camera in the center of the map
        cameraX = (worldWidth - screenWidth) / 2;
        cameraY = (worldHeight - screenHeight) / 2;
        GetTileImage();
        loadMap();
    }

    public void GetTileImage() {
        try {
            // SAND TILE
            tiles[0] = new Tile();
            tiles[0].setImage(ImageIO.read(getClass().getResourceAsStream("/View/Assets/sand.png")));

            // WATER TILE
            tiles[1] = new Tile();
            tiles[1].setImage(ImageIO.read(getClass().getResourceAsStream("/View/Assets/water.png")));
            tiles[1].setCollision(true);

            // TREE TILE
            tiles[2] = new Tile();
            tiles[2].setImage(ImageIO.read(getClass().getResourceAsStream("/View/Assets/tree.png")));
            tiles[2].setCollision(true); // Assuming tree is solid

            // BUSH TILE
            tiles[3] = new Tile();
            tiles[3].setImage(ImageIO.read(getClass().getResourceAsStream("/View/Assets/bush.png")));
            tiles[3].setCollision(true); // Assuming bush is not passable

            // FENCE TILE
            tiles[4] = new Tile();
            tiles[4].setImage(ImageIO.read(getClass().getResourceAsStream("/View/Assets/fence.png")));
            tiles[4].setCollision(true); // Assuming fence is solid

            // ROAD TILE
            tiles[5] = new Tile();
            tiles[5].setImage(ImageIO.read(getClass().getResourceAsStream("/View/Assets/road.png")));
            // ROCK TILE
            tiles[6] = new Tile();
            tiles[6].setImage(ImageIO.read(getClass().getResourceAsStream("/View/Assets/rock.png")));
            tiles[6].setCollision(true) ;
            // PARKING TILE
            tiles[7] = new Tile();
            tiles[7].setImage(ImageIO.read(getClass().getResourceAsStream("/View/Assets/parking.png")));
            tiles[7].setCollision(true) ;

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void loadMap() {
        try {
            InputStream is = getClass().getResourceAsStream(mapPath);
            BufferedReader br = new BufferedReader(new InputStreamReader(is));

            int col = 0;
            int row = 0;
            String line;
            while ((line = br.readLine()) != null && row < map.getMaxWorldRows()) {
                while (col < map.getMaxWorldColumns()) {
                    String numbers[] = line.split(" ");
                    int num = Integer.parseInt(numbers[col]);
                    mapTileNum[col][row] = num;
                    col++;
                }
                if (col == map.getMaxWorldColumns()) {
                    col = 0;
                    row++;
                }
            }
            br.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void updateCamera(double deltaX, double deltaY) {
        cameraX += deltaX;
        cameraY += deltaY;

        int maxWorldX = map.getMaxWorldColumns() * map.getTitleSize();
        int maxWorldY = map.getMaxWorldRows() * map.getTitleSize();
        int screenWidth = map.getTitleSize() * cameraWidth;
        int screenHeight = map.getTitleSize() * cameraHeight;

        // Allow camera to move further right to see the last 4 columns
        int maxRightPosition = maxWorldX - screenWidth + (4 * map.getTitleSize());
        cameraX = Math.max(0, Math.min(cameraX, maxRightPosition));
        cameraY = Math.max(0, Math.min(cameraY, maxWorldY - screenHeight));
    }

    public void draw(Graphics2D g2) {
        int maxScreenCol = cameraWidth;
        int maxScreenRow = cameraHeight;

        int startCol = (int) (cameraX / map.getTitleSize());
        int startRow = (int) (cameraY / map.getTitleSize());

        int cameraOffsetX = (int) (cameraX % map.getTitleSize());
        int cameraOffsetY = (int) (cameraY % map.getTitleSize());

        for (int screenRow = 0; screenRow <= maxScreenRow; screenRow++) {
            for (int screenCol = 0; screenCol <= maxScreenCol; screenCol++) {
                int worldCol = startCol + screenCol;
                int worldRow = startRow + screenRow;

                // Check if the tile is within world bounds for sake
                // of not loading all of tiles of the map
                if (worldCol < map.getMaxWorldColumns() && worldRow < map.getMaxWorldRows() &&
                        worldCol >= 0 && worldRow >= 0) {
                    int tileNum = mapTileNum[worldCol][worldRow];
                    int screenX = screenCol * map.getTitleSize() - cameraOffsetX;
                    int screenY = screenRow * map.getTitleSize() - cameraOffsetY;

                    if (screenX + map.getTitleSize() >= 0 && screenX < map.getTitleSize() * maxScreenCol &&
                            screenY + map.getTitleSize() >= 0 && screenY < map.getTitleSize() * maxScreenRow) {
                        g2.drawImage(tiles[tileNum].getImage(), screenX, screenY, map.getTitleSize(),
                                map.getTitleSize(),
                                null);
                    }
                }
            }
        }
    }
// for the shop
    public Point screenToWorldCoordinates(int screenX, int screenY) {
        // This is a simplified implementation - adjust based on your camera system
        double worldX = screenX + cameraX;
        double worldY = screenY + cameraY;
        
        return new Point((int)worldX, (int)worldY);
    }

    public boolean canMoveCamera(double deltaX, double deltaY) {
        double newCameraX = cameraX + deltaX;
        double newCameraY = cameraY + deltaY;

        int maxWorldX = map.getMaxWorldColumns() * map.getTitleSize();
        int maxWorldY = map.getMaxWorldRows() * map.getTitleSize();
        int screenWidth = map.getTitleSize() * cameraWidth;
        int screenHeight = map.getTitleSize() * cameraHeight;

        int maxRightPosition = maxWorldX - screenWidth + (4 * map.getTitleSize());

        if (deltaX < 0) {
            return newCameraX >= 0;
        } else if (deltaX > 0) {
            return newCameraX <= maxRightPosition;
        } else if (deltaY < 0) {
            return newCameraY >= 0;
        } else if (deltaY > 0) {
            return newCameraY <= maxWorldY - screenHeight;
        }

        return true;
    }

    public void changeMap(String newPath) {
        mapPath = newPath;
    }

    public int getMapTile(int x, int y) {
        return mapTileNum[x][y];
    }

    public void setMapTile(int col, int row, int tileType) {
        // Check if the coordinates are within bounds
        if (col >= 0 && col < map.getMaxWorldColumns() && row >= 0 && row < map.getMaxWorldRows()) {
            mapTileNum[col][row] = tileType;
        }
    }

    public Tile[] getTilesType() {
        return tiles;
    }

    public double getCameraX() {
        return cameraX;
    }

    public double getCameraY() {
        return cameraY;
    }
    public int getMaxWorldColumns() {
        return map.getMaxWorldColumns();
    }
    public int getMaxWorldRows() {
        return map.getMaxWorldRows();
    }
    public int [][] getMapTileNum() {
        return mapTileNum;
    }
    
    public void moveCamera(double deltaX, double deltaY) {
        if (canMoveCamera(deltaX, deltaY)) {
            cameraX += deltaX;
            cameraY += deltaY;
        }
    }

    public int getMapWidth() {
        return map.getMaxWorldColumns() * map.getTitleSize();
    }

    public int getMapHeight() {
        return map.getMaxWorldRows() * map.getTitleSize();
    }

    public int getTileSize() {
        return map.getTitleSize();
    }

    public boolean isPositionInBounds(int x, int y) {
        return x >= 0 && x < map.getMaxWorldColumns() && 
               y >= 0 && y < map.getMaxWorldRows();
    }

    public void resetMap() {
        // Reset the map to its initial state
        loadMap();
    }

    public void resetCamera() {
        // Reset camera to center of map
        int worldWidth = map.getMaxWorldColumns() * map.getTitleSize();
        int worldHeight = map.getMaxWorldRows() * map.getTitleSize();
        int screenWidth = map.getTitleSize() * cameraWidth;
        int screenHeight = map.getTitleSize() * cameraHeight;
        
        cameraX = (worldWidth - screenWidth) / 2;
        cameraY = (worldHeight - screenHeight) / 2;
    }
}
