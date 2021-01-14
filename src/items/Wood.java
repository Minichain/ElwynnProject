package items;

import entities.Sprite;
import main.Strings;

public class Wood extends Item {
    private int cost;

    public Wood() {
        super();
        cost = -1;
        MAX_AMOUNT_PER_STACK = 5;
    }

    @Override
    public void use() {

    }

    @Override
    public String getName() {
        return Strings.getString("item_wood");
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
