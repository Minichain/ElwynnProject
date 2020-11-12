package entities;

public class HealthPotion extends Item {
    private static String name = "Health Potion";
    private int cost;
    private float restoreValue;

    public HealthPotion() {
        super();
        cost = 10;
        restoreValue = 500f;
    }

    @Override
    public void use() {
        Player.getInstance().setHealth(Player.getInstance().getHealth() + restoreValue);
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
