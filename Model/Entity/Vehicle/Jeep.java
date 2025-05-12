package Model.Entity.Vehicle;

import Model.Entity.Entity;
import Model.Entity.Person.Tourist;
import java.util.ArrayList;
import java.util.List;

public class Jeep extends Entity {
    private static final double PRICE = 500.0;
    private static final int SPEED = 4; // No movement
    private static final int MAX_PASSENGERS = 4;
    
    private List<Tourist> passengers;
    private boolean isRented;
    private double rentalPrice;
    private double revenue;
    private double x;
    private double y;
    private int speed; // Change to int to match parent class

    public Jeep(int x, int y) {
        super(x, y, SPEED);
        this.passengers = new ArrayList<>();
        this.isRented = false;
        this.rentalPrice = 100.0; // Base rental price
        this.revenue = 0.0;
        this.x = x;
        this.y = y;
        this.speed = SPEED; // Initialize speed
    }

    public double getX() {
        return x;
    }

    public double getY() {
        return y;
    }

    public void setX(double x) {
        this.x = x;
    }

    public void setY(double y) {
        this.y = y;
    }

    public double getPrice() {
        return PRICE;
    }

    public boolean addPassenger(Tourist tourist) {
        if (passengers.size() < MAX_PASSENGERS) {
            passengers.add(tourist);
            return true;
        }
        return false;
    }

    public void removePassenger(Tourist tourist) {
        passengers.remove(tourist);
    }

    public List<Tourist> getPassengers() {
        return passengers;
    }

    public int getPassengerCount() {
        return passengers.size();
    }

    public boolean isFull() {
        return passengers.size() >= MAX_PASSENGERS;
    }

    public boolean isRented() {
        return isRented;
    }

    public void setRented(boolean rented) {
        this.isRented = rented;
    }

    public double getRentalPrice() {
        return rentalPrice;
    }

    public void setRentalPrice(double price) {
        this.rentalPrice = price;
    }

    public double getRevenue() {
        return revenue;
    }

    public void addRevenue(double amount) {
        this.revenue += amount;
    }

    public void clearPassengers() {
        passengers.clear();
    }

    // Update getter and setter for speed to use int
    @Override
    public int getSpeed() {
        return speed;
    }

    public void setSpeed(int speed) {
        this.speed = speed;
    }
} 