package Controller;

import Model.Entity.Person.Poacher;
import View.Scenes.MapPanel;

public class PoacherController extends EntityController {

    public PoacherController(String name, MapPanel map, ColissionController colCont, int x, int y) {
        super(new Poacher(name, x, y), map, getPoacherImages(), false, colCont, null);
    }
    
    private static String[] getPoacherImages() {
        String[] images = new String[8];
        // Using the same images as the Ranger for now
        images[0] = "/View/Assets/Ranger/boy_up_1.png";
        images[1] = "/View/Assets/Ranger/boy_up_2.png";
        images[2] = "/View/Assets/Ranger/boy_down_1.png";
        images[3] = "/View/Assets/Ranger/boy_down_2.png";
        images[4] = "/View/Assets/Ranger/boy_left_1.png";
        images[5] = "/View/Assets/Ranger/boy_left_2.png";
        images[6] = "/View/Assets/Ranger/boy_right_1.png";
        images[7] = "/View/Assets/Ranger/boy_right_2.png";
        return images;
    }
} 