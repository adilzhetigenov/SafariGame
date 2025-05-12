package Model.Entity.Animal;

import Model.Entity.Entity;
import Model.Plant.Plant;
import Model.Scenery.Pond;
import java.awt.geom.Point2D;
import java.util.HashMap;
import java.util.Map;

public abstract class Animal extends Entity{
    private int lifespanLimit;
    private Map<Animal, Double> availableAnimals = new HashMap<>();
    private AnimalType type;
    private Species species;
    private int age;
    private int consumpLevel;
    private boolean canReproduce;
    private int hungerLevel;
    private int thirstLevel;
    private double price;
    private boolean isAlive;
    private String group;
    private float health;

    public Animal(int lifespanLimit, int consumpLevel, double price, AnimalType type, Species species, int x,int y,int speed) {
        super(x, y, speed);
        this.lifespanLimit = lifespanLimit;
        this.consumpLevel = consumpLevel;
        this.price = price;
        this.type = type;
        this.species = species;
        this.age = 0;
        this.canReproduce = false;
        this.hungerLevel = 0;
        this.thirstLevel = 0;
        this.isAlive = true;
        this.health = 100f;
        this.group = "None";
    }

    // Getters and Setters
    public int getLifespanLimit() { return lifespanLimit; }
    public void setLifespanLimit(int lifespanLimit) { this.lifespanLimit = lifespanLimit; }
    
    public int getConsumpLevel() { return consumpLevel; }
    public void setConsumpLevel(int consumpLevel) { this.consumpLevel = consumpLevel; }
    
    public double getPrice() { return price; }
    private void setPrice(double price) { this.price = price; }
    
    public int getAge() { return age; }
    
    public boolean isCanReproduce() { return canReproduce; }
    
    public AnimalType getType() { return type; }
    
    public Species getSpecies() { return species; }

    // Movement
    public void move(Point2D.Double destination) {
        System.out.println("Moving to " + destination);
    }

    // Food and water search
    private void findFood() {
        System.out.println("Searching for food...");
    }

    private void findWater() {
        System.out.println("Searching for water...");
    }

    // Eating and drinking
    private void eat(Plant food) {
        System.out.println("Eating " + food.getType() + ".");
        hungerLevel = 0;
    }

    private void drink(Pond waterSource) {
        System.out.println("Drinking water.");
        thirstLevel = 0;
    }

    // Checking needs
    private boolean checkHungerLevel() {
        return hungerLevel > 50;
    }

    private boolean checkThirstLevel() {
        return thirstLevel > 50;
    }

    // Age and health management
    private void ageUp() {
        age++;
        if (age >= lifespanLimit) die();
        if (age >= lifespanLimit / 2) canReproduce = true;
    }

    private void die() {
        isAlive = false;
        System.out.println("The animal has died.");
    }

    public String getStatus() {
        return "Age: " + age + ", Health: " + health + ", Hunger: " + hungerLevel + 
               ", Thirst: " + thirstLevel + ", Alive: " + isAlive;
    }

    public boolean isAlive() {
        return isAlive;
    }

    public void setGroup(String group) {
        this.group = group;
    }

    public String getGroup() {
        return group;
    }

    private void updateHealth(float value) {
        health = Math.max(0, Math.min(100, health + value));
        if (health <= 0) die();
    }
}