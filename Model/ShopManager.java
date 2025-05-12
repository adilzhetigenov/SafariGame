package Model;

import java.util.HashMap;
import java.util.Map;

import Model.Plant.PlantType;
import Model.Entity.Animal.Animal;
import Model.Entity.Animal.Species;
import Model.Plant.Plant;

public class ShopManager {
    private Map<Species, Double> availableAnimals;
    private Map<PlantType, Double> availablePlants;
    private int availablePonds;
    private int availablePoacher;
    private int availableRanger;
    private float poacherPrice;
    private float rangerPrice;
    private float pondPrice;
    private float roadPrice;
    private float jeepPrice;
    private GameManager gameManager;

    public ShopManager() {
        availableAnimals = new HashMap<>();
        availablePlants = new HashMap<>();
        jeepPrice = 50;
        pondPrice = 100;
    }

    public void buyAnimal(Species type) {
        if (canAfford(availableAnimals.get(type))) {
            deductFunds(availableAnimals.get(type));
            //LOGIC TO ADD A NEW ANIMAL
        }
    }

    public void buyPlant(PlantType type) {
        if (canAfford(availablePlants.get(type))) {
            deductFunds(availablePlants.get(type));
            //LOGIC TO ADD A NEW ANIMAL
        }
    }

    public void buyPond() {
        if (availablePonds >= 1 && canAfford(pondPrice)) {
            deductFunds(pondPrice);
            //LOGIC TO ADD A NEW ANIMAL
        }
    }

    public void buyJeep() {
        if (canAfford(jeepPrice)) {
            deductFunds(jeepPrice);
        }
    }

    public void buyRoad() {
        if (canAfford(roadPrice)) {
            deductFunds(roadPrice);
        }
    }

    public void hireRanger() {
        if (availableRanger >= 1 && canAfford(rangerPrice)) {
            deductFunds(rangerPrice);
            //LOGIC TO ADD NEW ANIMAL
        }
    }

    public void hirePoacher() {
        if (availablePoacher >= 1 && canAfford(poacherPrice)) {
            deductFunds(poacherPrice);
            //LOGIC TO ADD NEW ANIMAL
        }
    }

    public void sellAnimal(Animal animal) {
        addFunds(availableAnimals.get(animal.getSpecies()));
    }

    public void sellPlant(Plant plant) {
        addFunds(availablePlants.get(plant.getType()));
    }

    public void deductFunds(double amount) {
        gameManager.decreaseCapital(amount);
    }

    public void addFunds(double amount) {
        gameManager.increaseCapital(amount);
    }

    public boolean canAfford(double price) {
        double capital = gameManager.getCurrentCapital();
        return (capital - price) < 0;
    }
} 