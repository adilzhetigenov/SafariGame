package Controller;

import Model.Entity.Person.Ranger;
import View.Scenes.MapPanel;

public class RangerController extends EntityController {

    public RangerController(String name, KeyInputController keyH, MapPanel map, ColissionController colCont, int x, int y) {
        super(new Ranger(name, x, y), map, getRangerImages(), true, colCont, keyH);
    }
    
    private static String[] getRangerImages() {
        String[] images = new String[8];
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
