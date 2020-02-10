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
        newCoordinates[0] = (x - Camera.getInstance().getCoordinates().x + ((double) Parameters.getInstance().getWindowWidth() / 2));
        newCoordinates[1] = (y - Camera.getInstance().getCoordinates().y + ((double) Parameters.getInstance().getWindowHeight() / 2));
        return newCoordinates;
    }

    public double[] toGlobalCoordinates() {
        double[] newCoordinates = new double[2];
        newCoordinates[0] = (x + Camera.getInstance().getCoordinates().x - ((double) Parameters.getInstance().getWindowWidth() / 2));
        newCoordinates[1] = (y + Camera.getInstance().getCoordinates().y - ((double) Parameters.getInstance().getWindowHeight() / 2));
        return newCoordinates;
    }

    public static int[] localCoordinatesToTileCoordinates(int x, int y) {
        double[] globalCoordinates = new Coordinates(x, y).toGlobalCoordinates();
        return globalCoordinatesToTileCoordinates((int) globalCoordinates[0], (int) globalCoordinates[1]);
    }

    public static int[] globalCoordinatesToTileCoordinates(int x, int y) {
        int i = (int) (x / (Scene.getTileWidth() * Camera.getZoom()));
        int j = (int) (y / (Scene.getTileHeight() * Camera.getZoom()));
        return new int[]{i, j};
    }

    public static int[] tileCoordinatesToGlobalCoordinates(int i, int j) {
        int x = i * (int) (Scene.getTileWidth() * Camera.getZoom());
        int y = j * (int) (Scene.getTileHeight() * Camera.getZoom());
        return new int[]{x, y};
    }
}
