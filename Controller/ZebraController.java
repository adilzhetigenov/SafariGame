package Controller;

import Model.Entity.Animal.Zebra;
import View.Scenes.MapPanel;

public class ZebraController extends AnimalController {

    public ZebraController(MapPanel map, ColissionController colCont, int x, int y) {
        super(new Zebra(x, y), map, getZebraImages(), false, colCont, null);
    }
    
    private static String[] getZebraImages() {
        String[] images = new String[8];
        images[0] = "/View/Assets/Zebra/zebra_up_1.png";
        images[1] = "/View/Assets/Zebra/zebra_up_2.png";
        images[2] = "/View/Assets/Zebra/zebra_down_1.png";
        images[3] = "/View/Assets/Zebra/zebra_down_2.png";
        images[4] = "/View/Assets/Zebra/zebra_left_1.png";
        images[5] = "/View/Assets/Zebra/zebra_left_2.png";
        images[6] = "/View/Assets/Zebra/zebra_right_1.png";
        images[7] = "/View/Assets/Zebra/zebra_right_2.png";
        return images;
    }
} 
