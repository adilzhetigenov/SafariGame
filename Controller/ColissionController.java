package Controller;

import View.Enities.EntitySprite;
import View.Scenes.MapPanel;
import java.util.List;

public class ColissionController {
    private MapPanel map;

    public ColissionController(MapPanel map) {
        this.map = map;
    }

    public void checkTile(EntityController entity) {
        int entityLeftWorldX = (int) entity.getEntityX() + entity.getSolidAreaX();
        int entityRightWorldX = (int) entity.getEntityX() + entity.getSolidAreaX() + entity.getSolidAreaWidth();
        int entityToptWorldY = (int) entity.getEntityY() + entity.getSolidAreaY();
        int entityBottomtWorldY = (int) entity.getEntityY() + entity.getSolidAreaY() + entity.getSolidAreaHeight();

        int entityLeftCol = entityLeftWorldX / map.getTitleSize();
        int entityRightCol = entityRightWorldX / map.getTitleSize();
        int entityTopRow = entityToptWorldY / map.getTitleSize();
        int entityBottomRow = entityBottomtWorldY / map.getTitleSize();

        if (entityLeftCol < 0 || entityRightCol >= map.getMaxWorldColumns() || 
            entityTopRow < 0 || entityBottomRow >= map.getMaxWorldRows()) {
            entity.setColission(true);
            return;
        }

        int tileNum1, tileNum2;
        switch (entity.getDirection()) {
            case "up":
                entityTopRow = (entityToptWorldY - entity.getSpeed()) / map.getTitleSize();
                if (entityTopRow < 0) {
                    entity.setColission(true);
                    return;
                }
                tileNum1 = map.getMapTileNum(entityLeftCol, entityTopRow);
                tileNum2 = map.getMapTileNum(entityRightCol, entityTopRow);
                if (entity instanceof JeepController) {
                    // For jeeps, only allow movement on road tiles (type 5)
                    if (tileNum1 != 5 || tileNum2 != 5) {
                        entity.setColission(true);
                    }
                } else if (map.getTilesType()[tileNum1].isCollision() || map.getTilesType()[tileNum2].isCollision()) {
                    entity.setColission(true);
                }
                break;
            case "down":
                entityBottomRow = (entityBottomtWorldY + entity.getSpeed()) / map.getTitleSize();
                if (entityBottomRow >= map.getMaxWorldRows()) {
                    entity.setColission(true);
                    return;
                }
                tileNum1 = map.getMapTileNum(entityLeftCol, entityBottomRow);
                tileNum2 = map.getMapTileNum(entityRightCol, entityBottomRow);
                if (entity instanceof JeepController) {
                    // For jeeps, only allow movement on road tiles (type 5)
                    if (tileNum1 != 5 || tileNum2 != 5) {
                        entity.setColission(true);
                    }
                } else if (map.getTilesType()[tileNum1].isCollision() || map.getTilesType()[tileNum2].isCollision()) {
                    entity.setColission(true);
                }
                break;
            case "left":
                entityLeftCol = (entityLeftWorldX - entity.getSpeed()) / map.getTitleSize();
                if (entityLeftCol < 0) {
                    entity.setColission(true);
                    return;
                }
                tileNum1 = map.getMapTileNum(entityLeftCol, entityTopRow);
                tileNum2 = map.getMapTileNum(entityLeftCol, entityBottomRow);
                if (entity instanceof JeepController) {
                    // For jeeps, only allow movement on road tiles (type 5)
                    if (tileNum1 != 5 || tileNum2 != 5) {
                        entity.setColission(true);
                    }
                } else if (map.getTilesType()[tileNum1].isCollision() || map.getTilesType()[tileNum2].isCollision()) {
                    entity.setColission(true);
                }
                break;
            case "right":
                entityRightCol = (entityRightWorldX + entity.getSpeed()) / map.getTitleSize();
                if (entityRightCol >= map.getMaxWorldColumns()) {
                    entity.setColission(true);
                    return;
                }
                tileNum1 = map.getMapTileNum(entityRightCol, entityTopRow);
                tileNum2 = map.getMapTileNum(entityRightCol, entityBottomRow);
                if (entity instanceof JeepController) {
                    // For jeeps, only allow movement on road tiles (type 5)
                    if (tileNum1 != 5 || tileNum2 != 5) {
                        entity.setColission(true);
                    }
                } else if (map.getTilesType()[tileNum1].isCollision() || map.getTilesType()[tileNum2].isCollision()) {
                    entity.setColission(true);
                }
                break;
        }
    }
    public int checkEntity(EntityController entity, List<EntitySprite> entities) {
        int index = 999;
        
        int entityLeftWorldX = (int) entity.getEntityX() + entity.getSolidAreaX();
        int entityRightWorldX = (int) entity.getEntityX() + entity.getSolidAreaX() + entity.getSolidAreaWidth();
        int entityTopWorldY = (int) entity.getEntityY() + entity.getSolidAreaY();
        int entityBottomWorldY = (int) entity.getEntityY() + entity.getSolidAreaY() + entity.getSolidAreaHeight();

       
        int bufferZone = 2; 

      
        for (int i = 0; i < entities.size(); i++) {
            EntityController otherEntity = entities.get(i).getController();
          
            if (otherEntity == entity) {
                continue;
            }

          
            int otherLeftWorldX = (int) otherEntity.getEntityX() + otherEntity.getSolidAreaX();
            int otherRightWorldX = (int) otherEntity.getEntityX() + otherEntity.getSolidAreaX() + otherEntity.getSolidAreaWidth();
            int otherTopWorldY = (int) otherEntity.getEntityY() + otherEntity.getSolidAreaY();
            int otherBottomWorldY = (int) otherEntity.getEntityY() + otherEntity.getSolidAreaY() + otherEntity.getSolidAreaHeight();

         
            boolean collision = false;
            String direction = entity.getDirection();

            switch (direction) {
                case "up":
                 
                    if (entityTopWorldY - bufferZone > otherBottomWorldY) {
                        collision = false;
                    } else if (entityRightWorldX + bufferZone > otherLeftWorldX && 
                             entityLeftWorldX - bufferZone < otherRightWorldX && 
                             entityTopWorldY - bufferZone <= otherBottomWorldY && 
                             entityTopWorldY >= otherTopWorldY) {
                        collision = true;
                    }
                    break;
                case "down":
                 
                    if (entityBottomWorldY + bufferZone < otherTopWorldY) {
                        collision = false;
                    } else if (entityRightWorldX + bufferZone > otherLeftWorldX && 
                             entityLeftWorldX - bufferZone < otherRightWorldX && 
                             entityBottomWorldY + bufferZone >= otherTopWorldY && 
                             entityBottomWorldY <= otherBottomWorldY) {
                        collision = true;
                    }
                    break;
                case "left":
                  
                    if (entityLeftWorldX - bufferZone > otherRightWorldX) {
                        collision = false;
                    } else if (entityBottomWorldY + bufferZone > otherTopWorldY && 
                             entityTopWorldY - bufferZone < otherBottomWorldY && 
                             entityLeftWorldX - bufferZone <= otherRightWorldX && 
                             entityLeftWorldX >= otherLeftWorldX) {
                        collision = true;
                    }
                    break;
                case "right":
                 
                    if (entityRightWorldX + bufferZone < otherLeftWorldX) {
                        collision = false;
                    } else if (entityBottomWorldY + bufferZone > otherTopWorldY && 
                             entityTopWorldY - bufferZone < otherBottomWorldY && 
                             entityRightWorldX + bufferZone >= otherLeftWorldX && 
                             entityRightWorldX <= otherRightWorldX) {
                        collision = true;
                    }
                    break;
            }

            if (collision) {
                index = i;
                break;
            }
        }
        
        return index;
    }
}
