package items;

import entities.Sprite;

public abstract class Item {

    public abstract void use();

    public abstract String getName();

    public abstract int getCost();

    public abstract Sprite getSprite();
}
