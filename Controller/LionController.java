package Controller;

import Model.Entity.Animal.Lion;
import View.Scenes.MapPanel;

public class LionController extends AnimalController {

    public LionController(MapPanel map, ColissionController colCont, int x, int y) {
        super(new Lion(x, y), map, getLionImages(), false, colCont, null);
    }
    
    private static String[] getLionImages() {
        String[] images = new String[8];
        images[0] = "/View/Assets/Lion/lion_up_1.png";
        images[1] = "/View/Assets/Lion/lion_up_2.png";
        images[2] = "/View/Assets/Lion/lion_down_1.png";
        images[3] = "/View/Assets/Lion/lion_down_2.png";
        images[4] = "/View/Assets/Lion/lion_left_1.png";
        images[5] = "/View/Assets/Lion/lion_left_2.png";
        images[6] = "/View/Assets/Lion/lion_right_1.png";
        images[7] = "/View/Assets/Lion/lion_right_2.png";
        return images;
    }
} 