package main;

import entities.Camera;
import entities.Scene;

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
        newCoordinates[0] = (x + Camera.getInstance().getCoordinates().x - (Camera.getWidth() / 2)) / Camera.getZoom();
        newCoordinates[1] = (y + Camera.getInstance().getCoordinates().y - (Camera.getHeight() / 2)) / Camera.getZoom();
        return newCoordinates;
    }

    public static int[] cameraCoordinatesToTileCoordinates(int x, int y) {
        double[] worldCoordinates = new Coordinates(x, y).toWorldCoordinates();
        return worldCoordinatesToTileCoordinates((int) worldCoordinates[0], (int) worldCoordinates[1]);
    }

    public static int[] worldCoordinatesToTileCoordinates(int x, int y) {
        int i = x / (Scene.getTileWidth());
        int j = y / (Scene.getTileHeight());
        return new int[]{i, j};
    }

    public static int[] tileCoordinatesToWorldCoordinates(int i, int j) {
        int x = i * Scene.getTileWidth();
        int y = j * Scene.getTileHeight();
        return new int[]{x, y};
    }
}
