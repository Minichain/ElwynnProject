package entities;

import main.Coordinates;
import main.Texture;

public abstract class Entity {
    private Coordinates coordinates;

    public Entity(int x, int y) {
        coordinates = new Coordinates((double)x, (double)y);
    }

    public Coordinates getCoordinates() {
        return coordinates;
    }

    public void setCoordinates(Coordinates coordinates) {
        this.coordinates = coordinates;
    }

    public abstract void drawSprite(int x, int y, Texture spriteSheet);

    public abstract Texture getSpriteSheet();
}
