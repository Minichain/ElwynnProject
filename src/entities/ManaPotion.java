package entities;

public class ManaPotion extends Item {
    private static String name = "Mana Potion";
    private int cost;
    private float restoreValue;

    public ManaPotion() {
        super();
        cost = 15;
        restoreValue = 10f;
    }

    @Override
    public void use() {
        Player.getInstance().setMana(Player.getInstance().getMana() + restoreValue);
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
