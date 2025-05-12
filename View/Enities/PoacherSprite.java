package View.Enities;

import Controller.ColissionController;
import Controller.PoacherController;
import View.Scenes.MapPanel;

public class PoacherSprite extends EntitySprite {
    
    public PoacherSprite(MapPanel map, String name, ColissionController colCont, int x, int y) {
        super(map, new PoacherController(name, map, colCont, x, y));
    }
} 