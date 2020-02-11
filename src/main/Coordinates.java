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

    public double[] toLocalCoordinates() {
        double[] newCoordinates = new double[2];
        newCoordinates[0] = (x - Camera.getInstance().getCoordinates().x + (Camera.getWidth() / 2)) * Camera.getZoom();
        newCoordinates[1] = (y - Camera.getInstance().getCoordinates().y + (Camera.getHeight() / 2)) * Camera.getZoom();
        return newCoordinates;
    }

    public double[] toGlobalCoordinates() {
        double[] newCoordinates = new double[2];
        newCoordinates[0] = (x + Camera.getInstance().getCoordinates().x - (Camera.getWidth() / 2)) / Camera.getZoom();
        newCoordinates[1] = (y + Camera.getInstance().getCoordinates().y - (Camera.getHeight() / 2)) / Camera.getZoom();
        return newCoordinates;
    }

    public static int[] localCoordinatesToTileCoordinates(int x, int y) {
        double[] globalCoordinates = new Coordinates(x, y).toGlobalCoordinates();
        return globalCoordinatesToTileCoordinates((int) globalCoordinates[0], (int) globalCoordinates[1]);
    }

    public static int[] globalCoordinatesToTileCoordinates(int x, int y) {
        int i = x / (Scene.getTileWidth());
        int j = y / (Scene.getTileHeight());
        return new int[]{i, j};
    }

    public static int[] tileCoordinatesToGlobalCoordinates(int i, int j) {
        int x = i * Scene.getTileWidth();
        int y = j * Scene.getTileHeight();
        return new int[]{x, y};
    }
}
