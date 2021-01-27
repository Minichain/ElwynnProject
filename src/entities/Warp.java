package entities;

import main.Coordinates;

public abstract class Warp extends StaticGraphicEntity {

    public Warp(int x, int y) {
        super(x, y);
    }

    public abstract String getWarpToScene();

    public abstract void setWarpToScene(String warpToScene);

    public abstract Coordinates getWarpToCoordinates();

    public abstract void setWarpToScene(Coordinates warpToCoordinates);
}
