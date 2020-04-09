package entities;

import main.Coordinates;

public abstract class StaticGraphicEntity extends GraphicEntity {
    private Coordinates tileCoordinates;
    private Collision collision;

    public StaticGraphicEntity(int x, int y) {
        super(x, y);
        tileCoordinates = Coordinates.worldCoordinatesToTileCoordinates(x, y);
    }

    public Coordinates getTileCoordinates() {
        return tileCoordinates;
    }

    public void setCollision(Collision collision) {
        this.collision = collision;
    }

    public Collision getCollision() {
        return collision;
    }
}