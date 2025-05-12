package Model;  

import Model.Entity.Animal.Animal;
import Model.Entity.Person.Ranger;
import Model.Entity.Person.Tourist;
import Model.Map.Map;
import Model.Scenery.Pond;
import Model.Transportation.Road;
import java.util.List;

public class GameManager {
    private double gameTime;
    private int[] date;
    private double capital;
    private int customerCount;
    private GameState gameState;
    private Mode difficulty;
    private Map map;
    private boolean isGameOver;

    public GameManager(int[] date, int customerCount, double capital, Mode difficulty) {
        // Initialize date array with default values if null
        if (date == null || date.length != 3) {
            this.date = new int[]{1, 1, 2024}; // Default to January 1st, 2024
        } else {
            this.date = date;
        }
        this.customerCount = customerCount;
        this.capital = capital;
        this.difficulty = difficulty;
        this.gameState = new GameState();
        this.map = new Map(4);
        this.isGameOver = false;
    }

    public void startNewGame() {
        //PLACEHOLDER
    }

    public void resumeGame() {
        //PLACEHOLDER
    }

    public void saveGame() {
        //PLACEHOLDER
    }

    public void loadGame() {
        //PLACEHOLDER
    }

    public void updateGameTime() {
        //Placeholder to use next day 
    }

    private void nextDay() {
        date[0]++;
        if (date[1] > 30) {
            nextMonth();
            date[0] = 0;
        }
    }

    private void nextMonth() {
        date[1]++;
        if (date[1] > 12) {
            date[2]++;
            date[1] = 1;
        }
    }

    public boolean isNight() {
        return false;
    }

    public void increaseCapital(double amount) {
        this.capital += amount;
    }

    public void decreaseCapital(double amount) {
        this.capital -= amount;
        if (this.capital <= 0) {
            this.isGameOver = true;
        }
    }

    public double getCurrentCapital() {
        return capital;
    }

    public void increaseCustomer(int amount) {
        customerCount += amount;
    }

    public void removeCustomer(int amount) {
        customerCount = Math.max(customerCount - amount,0);
    }

    public void setCustomer(int amount) {
        customerCount = Math.max(amount,0);
    }


    public void addAnimal(Animal animal) {
        map.addAnimal(animal);
    }

    public List<Animal> getAllAnimals() {
        return map.getAllAnimals();
    }

    public void addTourist(Tourist tourist) {
        map.addTourist(tourist);
    }

    public void removeTourist(Tourist tourist) {
        //PLACEHOLDER FOR HANDLING NOT FOUND
        map.removeTourist(tourist);
    }

    public List<Tourist> getAllTourists() {
        return map.getAllTourists();
    }

    public int getAllCustomers() {
        return customerCount;
    }

    public void addRanger(Ranger ranger) {
        map.addRanger(ranger);
    }

    public void removeRanger(Ranger ranger) {
        //PLACEHOLDER FOR HANDLING NOT FOUND
        map.removeRanger(ranger);
    }

    public List<Ranger> getAllRangers() {
        return map.getAllRangers();
    }

    public void addPond(Pond pond) {
        map.addPond(pond);
    }

    public void removePond(Pond pond) {
        //PLACEHOLDER FOR HANDLING NOT FOUND
        map.removePond(pond);
    }

    public List<Pond> getAllPonds() {
        return map.getAllPonds();
    }

    public void addRoad(Road road) {
        map.addRoad(road);
    }

    public void removeRoad(Road road) {
        //PLACEHOLDER FOR HANDLING NOT FOUND
        map.removeRoad(road);
    }

    public List<Road> getAllRoads() {
        return map.getAllRoads();
    }

    public GameState getCurrentState() {
        return gameState;
    }

    public void setGameState(GameState state) {
        gameState = state;
    }

    public int getDay() {
        return date != null ? date[0] : 1;
    }

    public int getMonth() {
        return date != null ? date[1] : 1;
    }

    public int getYear() {
        return date != null ? date[2] : 2024;
    }

    public void setCapital(double amount) {
        this.capital = amount;
    }
    
    public void setCustomerCount(int count) {
        this.customerCount = Math.max(0, count);
    }
    
    public boolean isGameOver() {
        return isGameOver;
    }

    public void resetGame() {
        this.capital = 1000.0;
        this.isGameOver = false;
    }
} 