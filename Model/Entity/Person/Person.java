package Model.Entity.Person;

import Model.Entity.Entity;

public abstract class Person extends Entity {
    private String name;
    private PersonType type;

    public Person(String name, PersonType type, int x, int y, int speed) {
        super(x, y, speed);
        this.name = name;
        this.type = type;
    }

    public String getName() {
        return name;
    }

    public PersonType getType() {
        return type;
    }
} 