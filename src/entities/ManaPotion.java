package entities;

public class ManaPotion extends Item {
    private String name;
    private int cost;

    public ManaPotion() {
        super();
        name = "Mana Potion";
        cost = 15;
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
