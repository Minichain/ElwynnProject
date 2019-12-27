package main;

import entities.Camera;

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
}
