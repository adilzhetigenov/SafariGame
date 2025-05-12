package View.Enities;

import Controller.ColissionController;
import Controller.KeyInputController;
import Controller.RangerController;
import View.Scenes.MapPanel;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

public class RangerSprite extends EntitySprite {
    
    public RangerSprite(MapPanel map, String name, KeyInputController kh, ColissionController colCont, int x, int y) {
        super(map, new RangerController(name, kh, map, colCont, x, y));
        
        // Add mouse listener for selection
        addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                // Toggle selection state
                boolean currentSelected = getController().isSelected();
                getController().setSelected(!currentSelected);
                
                // If this ranger is now selected, deselect all other entities
                if (!currentSelected) {
                    map.deselectAllEntitiesExcept(getController());
                }
            }
        });
    }
}
