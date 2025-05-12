package Model.Map;

import Model.Entity.Animal.Animal;
import Model.Plant.Plant;
import java.util.ArrayList;
import java.util.List;

public class Minimap {
    private Map map;
    private int size;
    private float zoomLevel;
    private List<Animal> visibleAnimals = new ArrayList<>();
    private List<Plant> visiblePlants = new ArrayList<>();

    public Minimap(Map map) {
        this.map = map;
        this.zoomLevel = 0;
    }

    public void updateMinimap() {
        //PLACEHOLDER
    }

    public void zoomIn() {
        zoomLevel += 1;
        updateMinimap();
    }

    public void zoomOut() {
        zoomLevel -= 1;
        updateMinimap();
    }
} 