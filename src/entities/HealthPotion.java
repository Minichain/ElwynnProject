package entities;

public class HealthPotion extends Item {
    private String name;
    private int cost;

    public HealthPotion() {
        super();
        name = "Health Potion";
        cost = 10;
    }

    @Override
    public void use() {

    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public int getCost() {
        return cost;
    }
}
