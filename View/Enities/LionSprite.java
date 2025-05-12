package View.Enities;

import Controller.ColissionController;
import Controller.LionController;
import View.Scenes.MapPanel;

public class LionSprite extends EntitySprite {
    
    public LionSprite(MapPanel map, ColissionController colCont, int x, int y) {
        super(map, new LionController(map, colCont, x, y));
    }
}
