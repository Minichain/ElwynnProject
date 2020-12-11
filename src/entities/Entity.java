package entities;

import main.Coordinates;

public abstract class Entity {
    private Coordinates worldCoordinates;
    private Coordinates cameraCoordinates;
    private boolean render = false;
    private boolean dead = false;

    public Entity(double x, double y) {
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

    public boolean isDead() {
        return dead;
    }

    public void setDead(boolean dead) {
        this.dead = dead;
    }

    public boolean isRender() {
        return render;
    }

    public void setRender(boolean render) {
        this.render = render;
    }
}
