package Model.Entity.Animal;

public class Lion extends Carnivore {
    private static final int LIFESPAN_LIMIT = 20;
    private static final int CONSUMPTION_LEVEL = 5;
    private static final double PRICE = 1000.0;
    private static final int SPEED = 3;

    public Lion(int x, int y) {
        super(LIFESPAN_LIMIT, CONSUMPTION_LEVEL, PRICE, Species.LION, x, y, SPEED);
    }
} 