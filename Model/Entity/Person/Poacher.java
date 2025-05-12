package Model.Entity.Person;

import Model.Entity.Animal.Animal;
import Model.Entity.Entity;

public class Poacher extends Entity {
    private static final int speed = 4;

    public Poacher(String name, int x, int y) {
        super(x, y, speed);
    }

    public void hunt(Animal animal) {
        // TODO: Implement hunting logic
    }
}