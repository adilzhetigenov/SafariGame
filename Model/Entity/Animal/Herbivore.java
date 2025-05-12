package Model.Entity.Animal;

public class Herbivore extends Animal {
    private static final int x = 100;
    private static final int y = 100;
    private static final int speed = 4;

    public Herbivore(int lifespanLimit, int consumpLevel, double price, Species species,int x, int y, int speed) {
        super(lifespanLimit, consumpLevel, price, AnimalType.HERBIVORE, species, x, y, speed);
    }
}