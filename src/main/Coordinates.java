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

    public double[] toCameraCoordinates() {
        double[] newCoordinates = new double[2];
        newCoordinates[0] = (x - Camera.getInstance().getCoordinates().x + (Camera.getWidth() / 2)) * Camera.getZoom();
        newCoordinates[1] = (y - Camera.getInstance().getCoordinates().y + (Camera.getHeight() / 2)) * Camera.getZoom();
        return newCoordinates;
    }

    public double[] toWorldCoordinates() {
        double[] newCoordinates = new double[2];
        newCoordinates[0] = (x / Camera.getZoom() + Camera.getInstance().getCoordinates().x - (Camera.getWidth() / 2));
        newCoordinates[1] = (y / Camera.getZoom() + Camera.getInstance().getCoordinates().y - (Camera.getHeight() / 2));
        return newCoordinates;
    }

    public static int[] cameraCoordinatesToTileCoordinates(int x, int y) {
        double[] worldCoordinates = new Coordinates(x, y).toWorldCoordinates();
        return worldCoordinatesToTileCoordinates((int) worldCoordinates[0], (int) worldCoordinates[1]);
    }

    public static int[] worldCoordinatesToTileCoordinates(int x, int y) {
        int i = x / TileMap.TILE_WIDTH;
        int j = y / TileMap.TILE_HEIGHT;
        return new int[]{i, j};
    }

    public static int[] tileCoordinatesToWorldCoordinates(int i, int j) {
        int x = i * TileMap.TILE_WIDTH;
        int y = j * TileMap.TILE_HEIGHT;
        return new int[]{x, y};
    }
}
