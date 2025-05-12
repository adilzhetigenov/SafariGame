package Model.Entity.Animal;

public class Carnivore extends Animal {
    private static final int speed = 4;

    public Carnivore(int lifespanLimit, int consumpLevel, double price, Species species, int x, int y, int speed) {
        super(lifespanLimit, consumpLevel, price, AnimalType.CARNIVORE, species, x, y, speed);
    }
}