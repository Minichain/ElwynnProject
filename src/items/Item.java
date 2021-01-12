package items;

import entities.Sprite;

public abstract class Item {
    public int MAX_AMOUNT_PER_STACK;

    public abstract void use();

    public abstract String getName();

    public abstract int getCost();

    public abstract Sprite getSprite();
}
