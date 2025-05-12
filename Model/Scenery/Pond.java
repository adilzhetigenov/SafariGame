package Model.Scenery;
import java.awt.geom.Point2D;

public class Pond {
    private float waterLevel;
    private int dryingRate;
    private Point2D.Float location;

    public Pond(int dryingRate, Point2D.Float location) {
        this.dryingRate = dryingRate;
        this.location = location;
    }

    public void setWaterLevel(float waterLevel) {
        this.waterLevel = waterLevel;
    }

    public float getWaterLevel() {
        return waterLevel;
    }

    public void decreaseWaterLevel(float waterLevel) {
        this.waterLevel -= waterLevel;
    }

    public boolean isDry() {
        return waterLevel <= 0;
    }
} 