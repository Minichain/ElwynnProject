package main;

import scene.Camera;
import scene.TileMap;

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

    public void translate(double x, double y) {
        this.x += x;
        this.y += y;
    }


    /** STATIC METHODS **/

    public static int[] cameraCoordinatesToTileCoordinates(double x, double y) {
        Coordinates worldCoordinates = new Coordinates(x, y).toWorldCoordinates();
        return worldCoordinatesToTileCoordinates(worldCoordinates.x, worldCoordinates.y);
    }

    public static int[] worldCoordinatesToTileCoordinates(double x, double y) {
        return new int[]{(int) x / TileMap.TILE_WIDTH, (int) y / TileMap.TILE_HEIGHT};
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

    public static Coordinates cameraToVertexCoordinates(double x, double y) {
        return new Coordinates((x / ((double) Parameters.getResolutionWidth() / 2.0)) - 1.0, (y / ((double) Parameters.getResolutionHeight() / 2.0)) - 1.0);
    }

    public static Coordinates cameraToVertexCoordinates(Coordinates coordinates) {
        return cameraToVertexCoordinates(coordinates.x, coordinates.y);
    }

    public static Coordinates cameraToFragmentCoordinates(double x, double y) {
        return new Coordinates(x, y - Window.getHeight());
    }

    public static Coordinates cameraToFragmentCoordinates(Coordinates coordinates) {
        return cameraToFragmentCoordinates(coordinates.x, coordinates.y);
    }

    public String toString() {
        return "(" + this.x + ", " + this.y + ")";
    }
}
