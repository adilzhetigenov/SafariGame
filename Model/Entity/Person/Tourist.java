package Model.Entity.Person;

import Model.Entity.Animal.Animal;

public class Tourist extends Person {
    private double budget;
    private double joyLevel;

    //STATIC VALUES FOR ALL ENTITIES
    private static final int x = 100;
    private static final int y = 100;
    private static final int speed = 4;


    public Tourist(String name) {
        super(name, PersonType.TOURIST,x,y,speed);
        this.joyLevel = 100;
    }

    public void takePhoto(Animal animal) {
        // TODO: Implement photo taking logic
    }

    public void observeAnimals() {
        // TODO: Implement animal observation logic
    }

    public void increaseJoy(double amount) {
        if (joyLevel + amount <= 100) {
            joyLevel += amount;
        }
    }

    public void decreaseJoy(double amount) {
        if (joyLevel - amount >= 0) {
            joyLevel -= amount;
        }
    }

    public void spendMoney(double amount) {
        budget -= amount;
    }

    public double getJoyLevel() {
        return joyLevel;
    }
} 