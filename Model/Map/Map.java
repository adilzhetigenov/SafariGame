package Model.Map;

import java.util.ArrayList;
import java.util.List;

import Model.Entity.Animal.Animal;
import Model.Entity.Animal.Carnivore;
import Model.Entity.Animal.Herbivore;
import Model.Entity.Person.Ranger;
import Model.Entity.Person.Tourist;
import Model.Plant.Plant;
import Model.Scenery.Pond;
import Model.Transportation.Road;
import Model.Scenery.Terrain;      

public class Map {
    private Terrain[][] grid;
    private int mapSize;
    private List<Animal> animals = new ArrayList<>();
    private List<Tourist> tourists = new ArrayList<>();
    private List<Ranger> rangers = new ArrayList<>();
    private List<Pond> ponds = new ArrayList<>();
    private List<Road> roads = new ArrayList<>();
    private List<Plant> plants = new ArrayList<>();

    public Map(int mapSize) {
        this.mapSize = mapSize;
        grid = new Terrain[mapSize][mapSize];
    }

    public void generateMap() {
        //PLACEHOLDER
    }

    public int carnivoreCount() {
        return (int) animals.stream().filter(a -> a instanceof Carnivore).count();
    }

    public int herbivoreCount() {
        return (int) animals.stream().filter(a -> a instanceof Herbivore).count();
    }

    public int animalCount() {
        return animals.size();
    }

    public List<Animal> getAllAnimals() {
        return animals;
    }

    public void addAnimal(Animal animal) {
        animals.add(animal);
    }

    public void removeAnimal(Animal animal) {
        //PLACEHOLDER FOR HANDLING NOT FOUND
        animals.remove(animal);
    }

    public void addTourist(Tourist tourist) {
        tourists.add(tourist);
    }

    public void removeTourist(Tourist tourist) {
        //PLACEHOLDER FOR HANDLING NOT FOUND
        tourists.remove(tourist);
    }

    public List<Tourist> getAllTourists() {
        return tourists;
    }

    public void addRanger(Ranger ranger) {
        rangers.add(ranger);
    }

    public void removeRanger(Ranger ranger) {
        //PLACEHOLDER FOR HANDLING NOT FOUND
        rangers.remove(ranger);
    }

    public List<Ranger> getAllRangers() {
        return rangers;
    }

    public void addPond(Pond pond) {
        ponds.add(pond);
    }

    public void removePond(Pond pond) {
        //PLACEHOLDER FOR HANDLING NOT FOUND
        ponds.remove(pond);
    }

    public List<Pond> getAllPonds() {
        return ponds;
    }

    public void addRoad(Road road) {
        roads.add(road);
    }

    public void removeRoad(Road road) {
        //PLACEHOLDER FOR HANDLING NOT FOUND
        roads.remove(road);
    }

    public List<Road> getAllRoads() {
        return roads;
    }

    public int getMapSize() {
        return mapSize;
    }
} 