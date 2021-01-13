package items;

import entities.Sprite;

public class Wood extends Item {
    private int cost;

    public Wood() {
        super();
        MAX_AMOUNT_PER_STACK = 5;
    }

    @Override
    public void use() {

    }

    @Override
    public String getName() {
        return "";
    }

    @Override
    public int getCost() {
        return cost;
    }

    @Override
    public Sprite getSprite() {
        return ItemType.WOOD.getSprite();
    }
}
