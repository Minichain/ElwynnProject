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
        newCoordinates[0] = (x
                - Camera.getInstance().position.x
                + (Parameters.getInstance().getWindowWidth() / 2));
        newCoordinates[1] = (y
                - Camera.getInstance().position.y
                + (Parameters.getInstance().getWindowHeight() / 2));
        return newCoordinates;
    }

    public double[] toGlobalCoordinates() {
        double[] newCoordinates = new double[2];
        newCoordinates[0] = (x
                + Camera.getInstance().position.x
                - (Parameters.getInstance().getWindowWidth() / 2));
        newCoordinates[1] = (y
                + Camera.getInstance().position.y
                - (Parameters.getInstance().getWindowHeight() / 2));
        return newCoordinates;
    }
}
