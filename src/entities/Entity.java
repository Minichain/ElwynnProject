package entities;

import main.Coordinates;
import main.Texture;

public abstract class Entity {
    private Coordinates worldCoordinates;
    private Coordinates cameraCoordinates;

    public Entity(int x, int y) {
        worldCoordinates = new Coordinates(x, y);
        cameraCoordinates = worldCoordinates.toCameraCoordinates();
        loadSprite();
    }

    public Coordinates getWorldCoordinates() {
        return worldCoordinates;
    }

    public void setWorldCoordinates(Coordinates coordinates) {
        this.worldCoordinates = coordinates;
        cameraCoordinates = worldCoordinates.toCameraCoordinates();
    }

    public Coordinates getCameraCoordinates() {
        return cameraCoordinates;
    }

    public abstract void loadSprite();

    public abstract void drawSprite(int x, int y, Texture spriteSheet);

    public abstract Texture getSpriteSheet();

    public abstract void update(long timeElapsed);

    public void updateCoordinates() {
        cameraCoordinates = worldCoordinates.toCameraCoordinates();
    }
}
