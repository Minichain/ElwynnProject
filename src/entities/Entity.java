package entities;

import main.Coordinates;

public abstract class Entity {
    private Coordinates worldCoordinates;
    private Coordinates cameraCoordinates;
    public boolean render = false;

    public Entity(int x, int y) {
        worldCoordinates = new Coordinates(x, y);
        cameraCoordinates = worldCoordinates.toCameraCoordinates();
    }

    public Coordinates getWorldCoordinates() {
        return worldCoordinates;
    }

    public void setWorldCoordinates(Coordinates coordinates) {
        this.worldCoordinates.x = coordinates.x;
        this.worldCoordinates.y = coordinates.y;
        cameraCoordinates = worldCoordinates.toCameraCoordinates();
    }

    public Coordinates getCameraCoordinates() {
        return cameraCoordinates;
    }

    public abstract void update(long timeElapsed);

    public void updateCoordinates() {
        cameraCoordinates = worldCoordinates.toCameraCoordinates();
    }
}
