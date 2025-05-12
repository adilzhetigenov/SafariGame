package Model.Plant;

public abstract class Plant {
    private PlantType type;
    private float health;

    public Plant(PlantType type) {
        this.type = type;
    }

    public PlantType getType() {
        return type;
    }

    public void reduceHealth(float amount) {
        health -= amount;
    }
} 