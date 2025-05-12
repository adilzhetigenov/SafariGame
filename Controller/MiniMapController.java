package Controller;

import View.Components.MiniMapView;
import View.Scenes.MapPanel;
import java.awt.Graphics2D;
import java.awt.Rectangle;


public class MiniMapController {
   private MapPanel map;
   private MapController mapController;
   private MiniMapView miniMapView;
   public boolean miniMapOn = true; 

   public MiniMapController(MapPanel map, MapController mapController) {
      this.map = map;
      this.mapController = mapController;
      this.miniMapView = new MiniMapView(map, this);
   }

   public void createMiniMap() {
      miniMapView.createMiniMap();
   }

   public void drawMiniMap(Graphics2D g2) {
      miniMapView.drawMiniMap(g2);
   }
   
   public void toggleMiniMap() {
      miniMapOn = !miniMapOn;
   }
   
   public boolean isMiniMapOn() {
      return miniMapOn;
   }
   
   public Rectangle getMiniMapBounds() {
      return miniMapView.getMiniMapBounds();
   }
   
   public MapController getMapController() {
      return mapController;
   }
}
