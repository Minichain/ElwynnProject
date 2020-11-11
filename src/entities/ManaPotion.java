package entities;

public class ManaPotion extends Item {
    private String name;

    public ManaPotion() {
        super();
        name = "Mana Potion";
    }

    @Override
    public void use() {

    }

    @Override
    public String getName() {
        return name;
    }
}
