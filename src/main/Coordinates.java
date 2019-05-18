package main;

import entities.Camera;

public class Coordinates {
    private double xCoordinate;
    private double yCoordinate;

    public Coordinates(double x, double y) {
        xCoordinate = x;
        yCoordinate = y;
    }

    public void setxCoordinate(double xCoordinate) {
        this.xCoordinate = xCoordinate;
    }

    public double getxCoordinate() {
        return xCoordinate;
    }

    public void setyCoordinate(double yCoordinate) {
        this.yCoordinate = yCoordinate;
    }

    public double getyCoordinate() {
        return yCoordinate;
    }

    public double[] toLocalCoordinates() {
        double[] newCoordinates = new double[2];
        newCoordinates[0] = (xCoordinate
                - Camera.getInstance().getCoordinates().getxCoordinate()
                + (Parameters.getInstance().getWindowWidth() / 2));
        newCoordinates[1] = (yCoordinate
                - Camera.getInstance().getCoordinates().getyCoordinate()
                + (Parameters.getInstance().getWindowHeight() / 2));
        return newCoordinates;
    }
}
