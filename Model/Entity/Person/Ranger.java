package Model.Entity.Person;

import Model.Entity.Animal.Animal;
import Model.Entity.Entity;

public class Ranger extends Entity {
    
    //Static values for all of the instances
    private static int speed = 4;

    public Ranger(String name, int x, int y) {
        super(x, y, speed);
    }

    public void checkAnimal(Animal animal) {
        // TODO: Implement animal checking logic
    }

    public void patrolArea() {
        // TODO: Implement patrol logic
    }
 
} 