package entities;

public class HealthPotion extends Item {
    private String name;

    public HealthPotion() {
        super();
        name = "Health Potion";
    }

    @Override
    public void use() {

    }

    @Override
    public String getName() {
        return name;
    }
}
