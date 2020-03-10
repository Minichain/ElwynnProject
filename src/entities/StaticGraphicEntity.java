package entities;

import main.Coordinates;

public abstract class StaticGraphicEntity extends GraphicEntity {
    private Coordinates tileCoordinates;

    public StaticGraphicEntity(int x, int y) {
        super(x, y);
        tileCoordinates = Coordinates.worldCoordinatesToTileCoordinates(x, y);
    }

    public Coordinates getTileCoordinates() {
        return tileCoordinates;
    }
}