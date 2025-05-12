package View.Enities;

import Controller.ColissionController;
import Controller.ZebraController;
import View.Scenes.MapPanel;

public class ZebraSprite extends EntitySprite {
    
    public ZebraSprite(MapPanel map, ColissionController colCont, int x, int y) {
        super(map, new ZebraController(map, colCont, x, y));
    }
}
