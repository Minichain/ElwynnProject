package main;

import entities.Camera;
import entities.TileMap;

public class Coordinates {
    public double x;
    public double y;

    public Coordinates(double x, double y) {
        this.x = x;
        this.y = y;
    }

    public Coordinates toCameraCoordinates() {
        return new Coordinates((x - Camera.getInstance().getCoordinates().x + (Camera.getWidth() / 2)) * Camera.getZoom(),
                (y - Camera.getInstance().getCoordinates().y + (Camera.getHeight() / 2)) * Camera.getZoom());
    }

    public Coordinates toWorldCoordinates() {
        return new Coordinates(x / Camera.getZoom() + Camera.getInstance().getCoordinates().x - (Camera.getWidth() / 2),
                y / Camera.getZoom() + Camera.getInstance().getCoordinates().y - (Camera.getHeight() / 2));
    }

    public static Coordinates cameraCoordinatesToTileCoordinates(double x, double y) {
        Coordinates worldCoordinates = new Coordinates(x, y).toWorldCoordinates();
        return worldCoordinatesToTileCoordinates(worldCoordinates.x, worldCoordinates.y);
    }

    public static Coordinates worldCoordinatesToTileCoordinates(double x, double y) {
        return new Coordinates(x / TileMap.TILE_WIDTH, y / TileMap.TILE_HEIGHT);
    }

    public static Coordinates tileCoordinatesToWorldCoordinates(int i, int j) {
        return new Coordinates(i * TileMap.TILE_WIDTH, j * TileMap.TILE_HEIGHT);
    }

    public static Coordinates cameraToWindowCoordinates(double x, double y) {
        return new Coordinates(x * Window.getCameraWindowScaleFactor()[0], y * Window.getCameraWindowScaleFactor()[1]);
    }

    public static Coordinates windowToCameraCoordinates(double x, double y) {
        return new Coordinates(x / Window.getCameraWindowScaleFactor()[0], y / Window.getCameraWindowScaleFactor()[1]);
    }

    public String toString() {
        return "(" + this.x + ", " + this.y + ")";
    }
}
